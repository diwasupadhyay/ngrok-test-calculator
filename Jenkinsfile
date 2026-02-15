pipeline {
    agent any

    environment {
        IMAGE_NAME = 'scientific-calculator'
        CONTAINER_NAME = 'scientific-calculator-app'
        MAVEN_IMAGE = 'maven:3.9.9-eclipse-temurin-17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Validate Environment') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker --version'
                    } else {
                        bat 'docker --version'
                    }
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker run --rm -v "${WORKSPACE}:/app" -w /app ${MAVEN_IMAGE} mvn -B clean test'
                    } else {
                        bat 'docker run --rm -v "%WORKSPACE%:/app" -w /app %MAVEN_IMAGE% mvn -B clean test'
                    }
                }
            }
        }

        stage('Build JAR') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker run --rm -v "${WORKSPACE}:/app" -w /app ${MAVEN_IMAGE} mvn -B clean package -DskipTests'
                    } else {
                        bat 'docker run --rm -v "%WORKSPACE%:/app" -w /app %MAVEN_IMAGE% mvn -B clean package -DskipTests'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                            docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest
                        '''
                    } else {
                        bat '''
                            docker build -t %IMAGE_NAME%:%BUILD_NUMBER% .
                            docker tag %IMAGE_NAME%:%BUILD_NUMBER% %IMAGE_NAME%:latest
                        '''
                    }
                }
            }
        }

        stage('Smoke Test Docker Container') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            docker rm -f ${CONTAINER_NAME} || true
                            docker run -d --name ${CONTAINER_NAME} -p 8081:8080 ${IMAGE_NAME}:latest
                            sleep 15
                            curl --fail "http://localhost:8081/api/calculate?operation=add&a=2&b=3"
                        '''
                    } else {
                        bat '''
                            docker rm -f %CONTAINER_NAME% 2>NUL
                            docker run -d --name %CONTAINER_NAME% -p 8081:8080 %IMAGE_NAME%:latest
                            timeout /t 15 /nobreak
                            powershell -Command "Invoke-WebRequest -Uri 'http://localhost:8081/api/calculate?operation=add&a=2&b=3' -UseBasicParsing | Select-Object -ExpandProperty StatusCode"
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (isUnix()) {
                    sh 'docker rm -f ${CONTAINER_NAME} || true'
                } else {
                    bat 'docker rm -f %CONTAINER_NAME% 2>NUL'
                }
            }
        }
        success {
            echo 'Pipeline finished successfully: tests passed and Docker image built.'
        }
    }
}
