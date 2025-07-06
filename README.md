# 📈 Jofi — AI-Powered Stock Analysis App

Jofi is a full-stack web application that helps users analyze stock performance using AI. It combines a responsive Angular frontend with a secure Spring Boot backend, integrating OpenAI and Hugging Face APIs for intelligent financial insights.

---

## 🧩 Tech Stack

| Layer       | Technology                     |
|-------------|--------------------------------|
| Frontend    | Angular 17, Angular Material   |
| Backend     | Spring Boot 3, Java 21         |
| AI Services | OpenAI API, Hugging Face API   |
| Build Tools | Maven, GitHub Actions          |
| Deployment  | Render (backend), Netlify or GitHub Pages (frontend) |

---

## 🚀 Features

- 📊 Upload and analyze stock metrics (high, low, open, close, return)
- 🧠 AI-generated insights based on investment strategy (e.g. "aggressive", "low-risk")
- 🔐 Secure API key handling via environment variables
- ⚙️ GitHub Actions CI/CD for automated builds and deployment
- 📱 Responsive UI with Angular Material

---

## ⚙️ Setup Instructions

### 🔧 Backend (Spring Boot)

```bash
cd api
cp .env.example .env  # Add your API keys
./mvnw clean package
./mvnw spring-boot:run
```

### 🔧 UI (Angular)
```bash
cd frontend
npm install
npm start
```

### environment variables
OPENAI_API_KEY=sk-...

HUGGINGFACE_API_KEY=hf-...

---

## 📄 License
MIT License.
