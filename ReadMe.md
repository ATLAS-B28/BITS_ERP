# BITS ERP Backend

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Python](https://img.shields.io/badge/Python-3.10%2B-yellow)](https://www.python.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.100.0-green)](https://fastapi.tiangolo.com/)
[![License](https://img.shields.io/badge/License-MIT-purple)](LICENSE)

**BITS ERP Backend** is a comprehensive enterprise resource planning system designed for educational institutions like BITS Pilani. It provides robust modules for authentication, role-based access control (RBAC), inventory management, procurement, sales, finance, and geographic information systems (GIS) — all powered by **Java Spring Boot**. Additionally, it integrates an AI service built with **Python (FastAPI)** for advanced analytics, predictions, and intelligent automation.

---

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Implementation Status](#implementation-status)
- [Prerequisites](#prerequisites)

---

## Features

- **Authentication & Authorization**: JWT-based secure authentication with Spring Security, including refresh tokens and role-based access control (RBAC).
- **Inventory Management**: Track stock levels, manage items, categories, suppliers, and automate reorder alerts.
- **Procurement**: Handle purchase orders, vendor management, and purchase requisitions with approval workflows.
- **Sales**: Manage customer orders, invoices, and payment tracking.
- **Finance**: General ledger, accounts payable/receivable, budgeting, and financial reporting.
- **GIS Integration**: Spatial data management for campus mapping, asset location, and facility planning using PostGIS.
- **AI-Powered Insights**: Python-based microservice offering demand forecasting, anomaly detection, and smart recommendations.
- **RESTful APIs**: Well-documented OpenAPI/Swagger endpoints for all modules.
- **Audit Logging**: Track all changes for compliance and transparency.

---

## Tech Stack

| Component          | Technology                                                     | Status          |
|--------------------|----------------------------------------------------------------|-----------------|
| **Core Backend**   | Java 17, Spring Boot 3.2, Spring Data JPA, Spring Security     | ✅ Ready        |
| **Database**       | PostgreSQL (with PostGIS extension)                            | ✅ Ready        |
| **GIS**            | PostGIS, Hibernate Spatial                                     | ✅ Ready        |
| **AI Service**     | Python 3.10, FastAPI, Pandas, Scikit-learn, TensorFlow (optional) | ⏳ *Remaining*    |
| **AI Models**      | Demand forecasting, anomaly detection, recommendations         | ⏳ *Remaining*  |
| **Message Queue**  | RabbitMQ / Apache Kafka (for async tasks)                      | ⏳ *Remaining*  |
| **API Documentation** | SpringDoc OpenAPI (Swagger UI)                               | ✅ Ready        |
| **Build Tools**    | Maven (Java), Pipenv (Python)                                  | ✅ Ready        |
| **Containerization** | Docker, Docker Compose                                        | ⏳ *Remaining*  |

---

## Architecture Overview

The system is split into two main services:

1. **Java Spring Boot Service** – Handles all core ERP logic, database interactions, and exposes REST APIs.
2. **Python AI Service** – Dedicated microservice that consumes data from the main API, runs ML models, and exposes endpoints for predictions, analytics, and reports.

Communication between services occurs via:
- Synchronous REST calls (for real-time predictions)

---

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Python 3.10+
- PostgreSQL 14+ with PostGIS extension
- Redis (optional, for caching)
- RabbitMQ (optional, for async tasks – **⏳ not yet required**)
- Docker & Docker Compose (optional, for containerized deployment – **⏳ in progress**)

