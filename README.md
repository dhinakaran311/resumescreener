<div align="center">

# AI-Powered ATS Resume Screener

**Intelligent Resume Screening Engine built with Java, Spring Boot & NLP Techniques**

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

</div>

---

## Overview

In modern recruitment, hiring teams manually review hundreds of resumes per job posting — a process that is slow, inconsistent, and prone to human bias. This project solves that problem by automating resume screening using **Natural Language Processing (NLP)** and **Machine Learning** techniques.

The system accepts PDF resumes and a job description, then performs intelligent matching through a multi-stage pipeline: **text extraction → cleaning → skill detection → TF-IDF vectorization → cosine similarity scoring → weighted ranking**. It delivers actionable insights including match scores, matched skills, missing skills, and category-level breakdowns — enabling recruiters to shortlist candidates in seconds.

---

## Features

- **PDF Resume Parsing** — Extracts raw text from uploaded PDF resumes using Apache PDFBox
- **Text Preprocessing** — Cleans, normalizes, and removes stop words for analysis-ready data
- **Categorized Skill Detection** — Identifies skills across 9 categories (Programming, Backend, Frontend, Database, ML/AI, Data Science, Cloud, DevOps, Tools)
- **TF-IDF Vectorization** — Converts documents into term frequency-inverse document frequency vectors
- **Cosine Similarity Matching** — Measures semantic similarity between resume and job description
- **Weighted Scoring** — Combines skill matching (60%) and TF-IDF similarity (40%) for a balanced assessment
- **Skill Gap Analysis** — Reports matched skills and identifies missing competencies
- **Edge Case Handling** — Validates inputs and handles empty/invalid job descriptions gracefully

---

## Architecture

```
┌──────────────┐     ┌──────────────────┐     ┌───────────────┐
│  Resume PDF  │────▶│  Text Extraction  │────▶│  Text Cleaner │
│  Upload      │     │  (PDFBox)         │     │  (Stop Words) │
└──────────────┘     └──────────────────┘     └───────┬───────┘
                                                       │
                     ┌──────────────────┐              │
                     │  Job Description │──────────────┤
                     │  (Text Input)    │              │
                     └──────────────────┘              ▼
                                              ┌───────────────┐
                                              │ Skill Detection│
                                              │ (Dictionary)   │
                                              └───────┬───────┘
                                                       │
                  ┌────────────────────────────────────┤
                  ▼                                    ▼
         ┌────────────────┐                   ┌────────────────┐
         │ TF-IDF Vectors │                   │ Skill Matching │
         │ (Vectorization)│                   │ (Category Map) │
         └───────┬────────┘                   └───────┬────────┘
                  │                                    │
                  ▼                                    ▼
         ┌────────────────┐                   ┌────────────────┐
         │    Cosine       │                   │  Skill Score   │
         │   Similarity    │                   │  (Matched %)   │
         └───────┬────────┘                   └───────┬────────┘
                  │                                    │
                  └──────────────┬──────────────────────┘
                                 ▼
                        ┌────────────────┐
                        │ Weighted Score │
                        │ 60% Skill +   │
                        │ 40% TF-IDF    │
                        └────────────────┘
```

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 17+** | Core programming language |
| **Spring Boot** | REST API framework & dependency injection |
| **Apache PDFBox** | PDF text extraction |
| **Maven** | Build automation & dependency management |
| **MySQL** | Candidate data persistence |
| **Lombok** | Boilerplate code reduction |

---

## API Endpoints

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| `POST` | `/api/resume` | Upload and analyze a resume | `file` (PDF) |
| `POST` | `/api/match` | Match resume against a job description | `file` (PDF), `jd` (Text) |

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.9+
- MySQL 8.0+

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/dhinakaran311/resumescreener.git
cd resumescreener

# 2. Configure MySQL in application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/resumescreener
# spring.datasource.username=root
# spring.datasource.password=yourpassword

# 3. Build and run the application
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`

---

## Usage Examples

### Upload and Analyze Resume

```bash
curl -X POST http://localhost:8080/api/resume \
  -F "file=@resume.pdf"
```

**Response:**
```
Resume processed. Skills detected: {PROGRAMMING=3, BACKEND=2, FRONTEND=3, DATABASE=2, DEVOPS=1, TOOLS=2}
```

### Match Resume Against Job Description

```bash
curl -X POST http://localhost:8080/api/match \
  -F "file=@resume.pdf" \
  -F "jd=Looking for a Java Developer with Spring Boot, React, Docker, Kubernetes, MySQL, and AWS experience"
```

**Response:**
```
Overall Match Score: 80%
Skill Match Score: 68%
TF-IDF Match Score: 100%
Matched Skills: 13
Missing Skills: [kubernetes, aws]
```

---

## Project Structure

```
resumescreener/
├── src/main/java/com/ats/resumescreener/
│   ├── ResumescreenerApplication.java      # Spring Boot entry point
│   ├── controller/
│   │   └── ResumeController.java           # REST API endpoints
│   ├── service/
│   │   └── ResumeService.java              # Core business logic
│   ├── util/
│   │   ├── PdfUtil.java                    # PDF text extraction
│   │   ├── TextCleaner.java                # Text preprocessing & stop word removal
│   │   ├── SkillDictionary.java            # Categorized skill definitions
│   │   ├── VectorUtil.java                 # TF-IDF vectorization engine
│   │   └── SimilarityUtil.java             # Cosine similarity calculator
│   └── model/                              # Entity classes
├── src/main/resources/
│   └── application.properties              # App configuration
├── pom.xml                                 # Maven dependencies
└── README.md
```

---

## Future Enhancements

- [ ] Multi-resume batch upload with automated ranking
- [ ] MySQL integration for storing candidate scores and history
- [ ] Experience detection and years of experience parsing
- [ ] Frontend dashboard with React or Angular
- [ ] Export ranked candidates as CSV/PDF reports
- [ ] Support for DOCX and other resume formats
- [ ] Role-specific weighted scoring profiles
- [ ] Docker containerization for deployment

---

## Contributing

Contributions are welcome. To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## Author

**Dhinakaran M S**

[![GitHub](https://img.shields.io/badge/GitHub-dhinakaran311-181717?style=flat-square&logo=github)](https://github.com/dhinakaran311)
