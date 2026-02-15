# Complete Assignment Guide: Jenkins + Docker + ngrok Webhook

This guide helps you run a full CI/CD flow for this calculator project.

---

## 1) Prerequisites (already installed as you said)

- Java 17+
- Maven 3.9+
- Docker
- Jenkins
- ngrok
- GitHub account + repository

---

## 2) Put project in GitHub

From project folder:

```bash
git init
git add .
git commit -m "Initial scientific calculator with Jenkins and Docker"
git branch -M main
git remote add origin <your-github-repo-url>
git push -u origin main
```

---

## 3) Verify app locally

```bash
mvn clean test
mvn spring-boot:run
```

Check in browser:

- `http://localhost:8080` (UI)
- `http://localhost:8080/api/calculate?operation=add&a=10&b=5` (API)

Stop app when done.

---

## 4) Verify Docker locally

```bash
docker build -t scientific-calculator:local .
docker run --rm -p 8080:8080 scientific-calculator:local
```

Open `http://localhost:8080`.

---

## 5) Configure Jenkins Pipeline Job

1. Open Jenkins dashboard.
2. Install plugins if missing:
   - **Pipeline**
   - **Git**
   - **GitHub**
3. Click **New Item** → name it `scientific-calculator-pipeline` → choose **Pipeline**.
4. In job config:
   - Under **Build Triggers**, check **GitHub hook trigger for GITScm polling**.
   - Under **Pipeline**, select **Pipeline script from SCM**.
   - SCM: **Git**.
   - Repository URL: your GitHub repo URL.
   - Branch: `*/main`.
   - Script Path: `Jenkinsfile`.
5. Save.
6. Click **Build Now** once manually to validate.

---

## 6) Expose Jenkins with ngrok (for GitHub webhook)

If Jenkins runs on port 8080:

```bash
ngrok http 8080
```

Copy the HTTPS forwarding URL, for example:

`https://abcd-1234.ngrok-free.app`

Your Jenkins webhook endpoint becomes:

`https://abcd-1234.ngrok-free.app/github-webhook/`

Important:
- Keep ngrok running while testing webhooks.
- Free ngrok URLs change when restarted, so update GitHub webhook each time.

---

## 7) Add GitHub Webhook

In your GitHub repository:

1. Go to **Settings** → **Webhooks** → **Add webhook**.
2. Payload URL:
   - `https://<your-ngrok-url>/github-webhook/`
3. Content type: `application/json`
4. Secret: optional (leave empty for basic classroom setup)
5. Events: **Just the push event**
6. Save webhook.

Now every `git push` should trigger Jenkins pipeline automatically.

---

## 8) CI/CD Flow in this project

The `Jenkinsfile` stages are:

1. **Checkout** source from GitHub.
2. **Validate Environment** (`docker --version`).
3. **Run Unit Tests** (inside Dockerized Maven container).
4. **Build JAR** (inside Dockerized Maven container).
5. **Build Docker Image** (`scientific-calculator:latest` and build number tag).
6. **Smoke Test** by running a temporary container on host port `8082`.
7. **Deploy Docker Container** by running app container on host port `8081`.

If all steps pass, pipeline is successful and app stays deployed at:

- `http://localhost:8081`

---

## 9) Demo Script (for submission/viva)

1. Start Jenkins.
2. Start ngrok (`ngrok http 8080`).
3. Ensure webhook payload URL uses current ngrok URL.
4. Make a tiny code change in project.
5. Commit and push.
6. Show Jenkins job auto-trigger.
7. Show successful stages and logs.
8. Optionally run produced Docker image locally.

---

## 10) Common Issues & Fixes

- **Webhook not triggering**
  - Check ngrok is running.
  - Check webhook URL ends with `/github-webhook/`.
  - Check GitHub webhook recent deliveries for status code.

- **Stage logs show only "Get contextual object from internal APIs"**
  - Usually an earlier stage failed and next stages were skipped.
  - Open full console output and check the first failed command.

- **`dockerDesktopLinuxEngine` / Docker daemon not found**
  - Start Docker Desktop and ensure Linux containers mode is active.
  - Re-run the pipeline after Docker is healthy.

- **Docker command fails in Jenkins**
  - Ensure Jenkins user has permission to access Docker daemon.
  - If Jenkins is in Docker, mount Docker socket correctly.

- **Port conflict (8080/8081)**
  - Change host port in `docker run` / `docker-compose.yml`.

- **App not visible on localhost:8081 after pipeline**
  - Confirm `Deploy Docker Container` stage succeeded.
  - Run `docker ps` and check container name `scientific-calculator-app`.
  - If Jenkins runs on another machine/VM, open `http://<jenkins-host-ip>:8081` instead of your own localhost.

---

## 11) Useful Commands

```bash
# Run tests
mvn test

# Build jar
mvn clean package -DskipTests

# Build docker image
docker build -t scientific-calculator:latest .

# Run docker container
docker run --rm -p 8080:8080 scientific-calculator:latest

# Stop and remove all containers (careful)
docker rm -f $(docker ps -aq)
```

---

You now have a complete assignment project with code + CI/CD + webhook guide.
