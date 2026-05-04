# 🌿 Plant Identifying AI

Plant Identifying AI is an AI-powered Spring Boot application that identifies plants from uploaded images and provides detailed botanical information using AI vision models.

The application also supports an interactive chat system where users can ask plant-related questions after identification.

---

# 🚀 Features

- 🌱 AI-based plant identification
- 📸 Upload plant images
- 💬 Ask questions about identified plants
- 🧠 Context-aware AI chat
- 📚 Detailed plant information
- ⚡ REST API support
- 🔒 Session-based conversation tracking
- 🪴 Plant care and maintenance suggestions

---

# 🛠️ Tech Stack

- Java
- Spring Boot
- Spring AI
- OpenAI API
- Maven
- REST APIs
- Jackson
- Lombok

---

# 📂 Project Structure

```bash
Plant-Identifying-AI/
│
├── src/
│   ├── main/
│   │   ├── java/com/Plant_Identifying_AI/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   └── config/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│
├── pom.xml
└── README.md
```

---

# ⚙️ API Endpoints

## 🌿 Identify Plant

Upload a plant image for identification.

```http
POST /identify
```

### Request
- Content-Type: `multipart/form-data`
- Parameter: `image`

### Example Response

```json
{
  "isPlant": true,
  "commonName": "Rose",
  "scientificName": "Rosa",
  "family": "Rosaceae",
  "climateType": "Temperate",
  "wateringSchedule": "Water twice a week",
  "careDifficulty": "Easy",
  "confidencePercent": 95
}
```

---

## 💬 Ask Questions

Ask questions related to the identified plant.

```http
POST /ask
```

### Example Request

```json
{
  "role": "user",
  "content": "How often should I water this plant?"
}
```

### Example Response

```text
Water the plant twice a week and keep the soil slightly moist.
```

---

## 🔄 Reset Session

Clear current session and chat history.

```http
POST /reset
```

---

# 🧠 How It Works

1. User uploads a plant image
2. AI analyzes the image
3. Application checks whether the image contains a plant
4. Detailed plant information is generated
5. User can ask follow-up questions
6. Chat history is maintained using HTTP sessions

---

# ⚙️ Setup & Installation

## 1️⃣ Clone Repository

```bash
git clone https://github.com/your-username/Plant-Identifying-AI.git
```

---

## 2️⃣ Open Project

```bash
cd Plant-Identifying-AI
```

---

## 3️⃣ Configure API Key

Add your OpenAI API key in `application.properties`

```properties
spring.ai.openai.api-key=YOUR_API_KEY
```

---

## 4️⃣ Run Application

```bash
mvn spring-boot:run
```

---

# 📌 Main Functionalities

The AI can provide information about:

- Plant Name
- Scientific Name
- Family
- Habitat
- Climate Type
- Soil Requirements
- Watering Methods
- Sunlight Needs
- Growth Rate
- Flowering Season
- Diseases & Pests
- Medicinal Properties
- Toxicity Information
- Care Tips
- Similar Species

---

# 🔮 Future Improvements

- 📱 Mobile Application
- 🌍 Multi-language Support
- 🎥 Live Camera Detection
- 🦠 Plant Disease Detection
- ☁️ Cloud Deployment
- 📊 Plant Identification History

---

# 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to your branch
5. Open a Pull Request

---

# 📜 License

This project is licensed under the MIT License.

---

# 👨‍💻 Author
GitHub: Smriti-1
