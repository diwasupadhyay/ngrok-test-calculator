pipeline {
    agent any

    environment {
        IMAGE_NAME = 'scientific-calculator'
        SMOKE_CONTAINER_NAME = 'scientific-calculator-smoke'
        DEPLOY_CONTAINER_NAME = 'scientific-calculator-app'
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
                            docker rm -f ${SMOKE_CONTAINER_NAME} || true
                            docker run -d --name ${SMOKE_CONTAINER_NAME} -p 8082:8080 ${IMAGE_NAME}:latest
                            sleep 15
                            curl --fail "http://localhost:8082/api/calculate?operation=add&a=2&b=3"
                        '''
                    } else {
                        bat '''
                            @echo on
                            docker rm -f %SMOKE_CONTAINER_NAME% >NUL 2>&1 || echo No previous smoke container
                            docker run -d --name %SMOKE_CONTAINER_NAME% -p 8082:8080 %IMAGE_NAME%:latest
                            timeout /t 15 /nobreak
                            powershell -NoProfile -Command "$r = Invoke-WebRequest -Uri 'http://localhost:8082/api/calculate?operation=add&a=2&b=3' -UseBasicParsing; if ($r.StatusCode -ne 200) { exit 1 }"
                            if errorlevel 1 (
                                docker logs --tail 100 %SMOKE_CONTAINER_NAME%
                                exit /b 1
                            )
                        '''
                    }
                }
            }
        }

        stage('Deploy Docker Container') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            docker rm -f ${DEPLOY_CONTAINER_NAME} || true
                            docker run -d --name ${DEPLOY_CONTAINER_NAME} -p 8081:8080 ${IMAGE_NAME}:latest
                            sleep 5
                            curl --fail "http://localhost:8081/api/calculate?operation=add&a=2&b=3"
                        '''
                    } else {
                        bat '''
                            @echo on
                            docker rm -f %DEPLOY_CONTAINER_NAME% >NUL 2>&1 || echo No previous deployed container
                            docker run -d --name %DEPLOY_CONTAINER_NAME% -p 8090:8080 %IMAGE_NAME%:latest
                            if errorlevel 1 (
                                echo Deploy failed. Port 8090 may already be in use.
                                docker ps -a
                                exit /b 1
                            )
                            timeout /t 5 /nobreak
                            powershell -NoProfile -Command "$r = Invoke-WebRequest -Uri 'http://localhost:8090/api/calculate?operation=add&a=2&b=3' -UseBasicParsing; if ($r.StatusCode -ne 200) { exit 1 }"
                            if errorlevel 1 (
                                docker logs --tail 100 %DEPLOY_CONTAINER_NAME%
                                exit /b 1
                            )
                            docker ps
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
                    sh 'docker rm -f ${SMOKE_CONTAINER_NAME} || true'
                } else {
                    bat 'docker rm -f %SMOKE_CONTAINER_NAME% >NUL 2>&1 || echo No smoke container to cleanup'
                }
            }
        }
        success {
            echo 'Pipeline finished successfully: tests passed and Docker image built.'
        }
    }
}
