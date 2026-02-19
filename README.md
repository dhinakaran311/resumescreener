<div align="center">

# AI-Powered ATS Resume Screener

**Intelligent Resume Screening Engine built with Java, Spring Boot & NLP Techniques**

[![Java](https://img.shields.io/badge/Java-24-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

</div>

---

## Overview

This project automates resume screening using **Natural Language Processing (NLP)** techniques. It accepts PDF resumes and a job description, then runs them through a multi-stage intelligence pipeline:

**PDF Extraction → Text Cleaning → Skill Detection → TF-IDF Vectorization → Cosine Similarity → Experience Parsing → Weighted Ranking → MySQL Persistence**

Recruiters can shortlist candidates in seconds with actionable insights: match scores, matched/missing skills, experience years detected, and persistent result history.

---

## Features

- **PDF Resume Parsing** — Extracts raw text from uploaded PDFs using Apache PDFBox
- **Text Preprocessing** — Cleans, normalizes, and removes stop words
- **Categorized Skill Detection** — Identifies skills across 9 categories: Programming, Backend, Frontend, Database, ML/AI, Data Science, Cloud, DevOps, Tools
- **TF-IDF Vectorization** — Converts documents into smoothed TF-IDF vectors (fixed IDF formula)
- **Cosine Similarity Matching** — Measures semantic overlap between resume and job description
- **Experience Extraction** — Detects years of experience from patterns like `"3 years"`, `"5+ yrs"`
- **Weighted Scoring** — `(Skill Match × 60%) + (TF-IDF × 40%) + Experience Boost (max +10)`
- **Experience Boost** — Up to +10 points for senior candidates (2pts per year, capped at 5 years)
- **Skill Gap Analysis** — Reports matched skills and missing competencies
- **Multi-Resume Ranking** — Ranks all uploaded resumes; tiebreaker by matched skill count
- **MySQL Persistence** — Every ranking result saved to DB (Candidate, JobDescription, MatchResult tables)
- **Results History** — Retrieve all past results via REST API

---

## Scoring Formula

```
finalScore = (skillScore × 0.6) + (tfidfScore × 0.4) + min(experienceYears, 5) × 2
```

| Component | Weight | Max |
|-----------|--------|-----|
| Skill Match Score | 60% | 60 pts |
| TF-IDF Cosine Similarity | 40% | 40 pts |
| Experience Boost | +2 per year | +10 pts |

---

## Architecture

```
┌──────────────┐     ┌──────────────────┐     ┌───────────────┐
│  Resume PDF  │────▶│  Text Extraction  │────▶│  Text Cleaner │
│  Upload      │     │  (PDFBox)         │     │  (Stop Words) │
└──────────────┘     └──────────────────┘     └───────┬───────┘
                                                       │
                      ┌──────────────────┐             │
                      │  Job Description │─────────────┤
                      │  (Text Input)    │             │
                      └──────────────────┘             ▼
                                              ┌────────────────┐
                                              │ Skill Detection │
                                              │ (Dictionary)    │
                                              └───────┬────────┘
                                                      │
              ┌───────────────────────────────────────┤
              ▼                                       ▼
     ┌────────────────┐                    ┌────────────────┐
     │ TF-IDF Vectors │                    │ Skill Matching │
     │ (Vectorization)│                    │ (Category Map) │
     └───────┬────────┘                    └───────┬────────┘
              │                                     │
              ▼                                     ▼
     ┌────────────────┐                    ┌────────────────┐
     │    Cosine       │                   │  Skill Score   │
     │   Similarity    │                   │  (Matched %)   │
     └───────┬────────┘                    └───────┬────────┘
              │                                     │
              └─────────────────┬───────────────────┘
                                │
                     ┌──────────┴───────────┐
                     │  Experience Boost    │
                     │  (ExperienceUtil)    │
                     └──────────┬───────────┘
                                ▼
                       ┌────────────────┐
                       │  Final Score   │◀── Ranked Output
                       │  + MySQL Save  │
                       └────────────────┘
```

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 24** | Core programming language |
| **Spring Boot 3.3.5** | REST API framework & dependency injection |
| **Apache PDFBox** | PDF text extraction |
| **Spring Data JPA** | ORM & database persistence |
| **MySQL 8** | Candidate & result storage |
| **Maven** | Build automation & dependency management |

---

## API Endpoints

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| `POST` | `/api/resume` | Upload and analyze a single resume | `file` (PDF) |
| `POST` | `/api/match` | Match one resume against a job description | `file` (PDF), `jd` (String) |
| `POST` | `/api/rank` | Rank multiple resumes against a job description | `files[]` (PDFs), `jd` (String) |
| `GET`  | `/api/results` | Retrieve all stored match results from DB | — |

---

## Getting Started

### Prerequisites

- Java 24
- Maven 3.9+
- MySQL 8.0+

### Database Setup

```sql
CREATE DATABASE ats;
CREATE USER 'ats_user'@'localhost' IDENTIFIED BY 'ats_password';
GRANT ALL PRIVILEGES ON ats.* TO 'ats_user'@'localhost';
FLUSH PRIVILEGES;
```

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/dhinakaran311/resumescreener.git
cd resumescreener

# 2. Run the application
./mvnw spring-boot:run
```

Hibernate auto-creates all tables on first boot (`ddl-auto=update`).

The server starts at `http://localhost:8080`

---

## Usage Examples

### Upload and Analyze Resume

```bash
curl -X POST http://localhost:8080/api/resume \
  -F "file=@resume.pdf"
```

### Match Resume Against Job Description

```bash
curl -X POST http://localhost:8080/api/match \
  -F "file=@resume.pdf" \
  -F "jd=Looking for a Java Developer with Spring Boot, React, Docker, MySQL"
```

### Rank Multiple Resumes

```bash
curl -X POST http://localhost:8080/api/rank \
  -F "files=@resume1.pdf" \
  -F "files=@resume2.pdf" \
  -F "files=@resume3.pdf" \
  -F "jd=Java Spring Boot developer with 3+ years experience"
```

**Response:**
```json
[
  {
    "name": "KarthickRes.pdf",
    "score": 91.34,
    "matchedSkills": ["java", "spring", "mysql"],
    "missingSkills": ["docker"],
    "matchedCount": 3,
    "missingCount": 1
  },
  ...
]
```

### Get All Stored Results

```bash
curl http://localhost:8080/api/results
```

---

## Project Structure

```
resumescreener/
├── src/main/java/com/ats/resumescreener/
│   ├── ResumescreenerApplication.java
│   ├── controller/
│   │   └── ResumeController.java          # REST endpoints
│   ├── service/
│   │   └── ResumeService.java             # Core scoring & ranking logic
│   ├── entity/
│   │   ├── Candidate.java                 # JPA entity — candidate name
│   │   ├── JobDescription.java            # JPA entity — JD content
│   │   └── MatchResult.java               # JPA entity — scores & metadata
│   ├── repository/
│   │   ├── CandidateRepository.java
│   │   ├── JobDescriptionRepository.java
│   │   └── MatchResultRepository.java
│   ├── model/
│   │   └── CandidateResult.java           # API response model
│   └── util/
│       ├── PdfUtil.java                   # PDF text extraction
│       ├── TextCleaner.java               # Preprocessing & stop word removal
│       ├── SkillDictionary.java           # Categorized skill definitions
│       ├── VectorUtil.java                # TF-IDF vectorization
│       ├── SimilarityUtil.java            # Cosine similarity calculator
│       └── ExperienceUtil.java            # Experience year extractor
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── README.md
```

---

## Database Schema

```
candidate          job_description       match_result
──────────         ───────────────       ────────────────────────
id (PK)            id (PK)               id (PK)
name               content               final_score
                                         skill_score
                                         tfidf_score
                                         matched_count
                                         missing_count
                                         experience_years
                                         candidate_id (FK)
                                         job_description_id (FK)
```

---

## Contributing

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
