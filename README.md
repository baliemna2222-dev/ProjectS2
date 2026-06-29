<div align="center">

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=30&pause=1000&color=E50914&center=true&vCenter=true&width=600&lines=🎬+Raksha+Streaming+App;Netflix-Inspired+JavaFX+Desktop+App;Dual-Role+%7C+Secure+%7C+Cinematic" alt="Typing SVG" />

<br/>

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-UI_Framework-0078D4?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-Data_Access-F7931E?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build_Tool-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

<br/>

![Status](https://img.shields.io/badge/Status-Completed_✓-brightgreen?style=flat-square)
![Year](https://img.shields.io/badge/Year-2026-blueviolet?style=flat-square)
![Developer](https://img.shields.io/badge/Developer-Solo_Project-E50914?style=flat-square)
![Architecture](https://img.shields.io/badge/Architecture-MVC-informational?style=flat-square)

<br/>

> **A Netflix-inspired desktop application built entirely with Java and JavaFX.**  
> Dual user/admin roles · Secure JDBC authentication · Video browsing · Full content management dashboard — all in one polished interface.

<br/>

[🚀 View Source Code](https://github.com/baliemna2222-dev/ProjectS2/tree/emna) · [🌐 Project Details](https://baliemna2222-dev.github.io/emna-benali.github.io/raksha.html) · [👩‍💻 Portfolio](https://baliemna2222-dev.github.io/emna-benali.github.io/index.html)

<br/>
[![Database](https://img.shields.io/badge/🗄️_Database-jstreamdb.sql-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://github.com/baliemna2222-dev/Raksha-Streaming-Platform/blob/emna/jstreamdb%20(8)%20(2).sql)

[![Manual](https://img.shields.io/badge/📖_Manual-manuel.pdf-0078D4?style=for-the-badge&logo=adobeacrobatreader&logoColor=white)](https://github.com/baliemna2222-dev/Raksha-Streaming-Platform/blob/emna/manuel%20(1)%20(2).pdf)
</div>

---

## 📸 Preview

<div align="center">

| User Interface | Admin Dashboard |
|:-:|:-:|
| 🎬 Browse & Stream | 🛠️ Manage Content |
| Dark Netflix-inspired UI | Full CRUD operations |

</div>

---

## ✨ What is Raksha?

**Raksha** is a feature-complete desktop streaming simulation built from the ground up using **Java 17** and **JavaFX**, following the **MVC architectural pattern**. It replicates the look, feel, and functionality of real-world streaming platforms — with a dual-role system, a secure authentication flow, and a fully integrated MySQL backend.

Designed and developed **solo**, this project demonstrates clean software engineering principles: modularity, layered architecture, role-based access control, and real-time database integration.

---

## 🗂️ Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Database Setup](#-database-setup)
- [App Manual](#-app-manual)
- [Challenges & Solutions](#-challenges--solutions)
- [Project Structure](#-project-structure)
- [Developer](#-developer)

---

## 🎯 Features

<details>
<summary><b>🔑 Role-Based Authentication</b></summary>

Login system supporting two roles: **User** and **Admin**. Role is stored in the database and determines which interface loads on sign-in. Sessions are managed in memory throughout the application lifecycle. Passwords are **SHA-256 hashed** — no plain text ever touches the database.

</details>

<details>
<summary><b>🎬 Content Browsing & Discovery</b></summary>

Users can browse a full library of **movies and series**. Content is loaded dynamically from MySQL via JDBC and presented in a horizontally scrollable row layout — similar to Netflix's core browsing UX.

</details>

<details>
<summary><b>▶️ In-App Video Streaming</b></summary>

Integrated **JavaFX MediaPlayer** for in-application playback. Custom controls include play/pause, seek bar, volume, and fullscreen — all built from scratch.

</details>

<details>
<summary><b>🛠️ Admin Content Management</b></summary>

Admins access a **separate dashboard** to add, edit, and delete movies and series. All changes persist to the database immediately and reflect in the user-facing library in real time.

</details>

<details>
<summary><b>👥 User Management</b></summary>

Admin panel includes full user management: view registered accounts, change roles, or deactivate accounts — all updating the MySQL user table live.

</details>

<details>
<summary><b>🎨 Dark Netflix-Inspired UI</b></summary>

JavaFX styled with **custom CSS** — dark colour palette, smooth scene transitions, hover states, and a layout hierarchy modelled after real streaming platforms.

</details>

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│              USER INTERFACE LAYER                │
│         JavaFX · FXML · Custom CSS              │
│    [ User View ]        [ Admin Dashboard ]     │
└──────────────────────┬──────────────────────────┘
                       │ Presentation Layer
                       ▼
┌─────────────────────────────────────────────────┐
│            APPLICATION LAYER (MVC)               │
│   Controllers · Event Handlers · Business Logic  │
│         SceneManager · Session State            │
└──────────────────────┬──────────────────────────┘
                       │ Service / Logic Layer
                       ▼
┌─────────────────────────────────────────────────┐
│             DATA ACCESS LAYER                    │
│       JDBC · DAO Classes · Prepared Statements   │
└──────────────────────┬──────────────────────────┘
                       │ Database Layer
                       ▼
┌─────────────────────────────────────────────────┐
│               MySQL DATABASE                     │
│     Users · Content · Roles · Watch History      │
└─────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Technology | Role | Highlights |
|------------|------|------------|
| ☕ **Java 17** | Core language | OOP, MVC, event handling, business logic |
| 🎨 **JavaFX** | UI Framework | Scenes, FXML, CSS theming, media playback |
| 🗄️ **MySQL** | Database | Normalized schema — users, roles, content, sessions |
| 🔌 **JDBC** | Data Access | Prepared statements, DAO pattern, SQL injection protection |
| 🏗️ **Maven** | Build Tool | Dependency management, project structure |
| 🖼️ **Scene Builder** | UI Design | FXML-based layout design |

---

## 🚀 Getting Started

### Prerequisites

```
Java 17+
MySQL 8.0+
Maven 3.8+
```

### Setup

```bash
# 1. Clone the repository
git clone https://github.com/baliemna2222-dev/ProjectS2.git
cd ProjectS2
git checkout emna

# 2. Import the database — see section below ↓

# 3. Edit src/main/resources/db.properties with your MySQL credentials
db.url=jdbc:mysql://localhost:3306/raksha_db
db.username=your_username
db.password=your_password

# 4. Build and run
mvn clean javafx:run
```

---

## 🗄️ Database Setup

> [`jstreamdb (8) (2).sql`](jstreamdb%20(8)%20(2).sql) contains the **complete database** for this project — all tables, relationships, and sample data. You must import this file into MySQL **before** launching the app.

### What's inside

- ✅ Creates `raksha_db` automatically
- ✅ All 7 tables: `users`, `roles`, `movies`, `series`, `episodes`, `categories`, `watch_history`
- ✅ Foreign keys, constraints, and UTF-8 encoding
- ✅ Default admin & user accounts with hashed passwords
- ✅ Sample movies, series, episodes, and categories ready to use

### How to import

**Option A — Command Line**
```bash
mysql -u root -p < "jstreamdb (8) (2).sql"
```

**Option B — MySQL Workbench**
1. Open MySQL Workbench and connect to your server
2. Go to **File → Open SQL Script** → select `jstreamdb (8) (2).sql`
3. Click ⚡ **Execute**

**Option C — phpMyAdmin**
1. Open `http://localhost/phpmyadmin`
2. Click **Import** → choose `jstreamdb (8) (2).sql` → click **Go**

### Default accounts

| Role | Username | Password |
|------|----------|----------|
| 👑 Admin | `admin` | `admin123` |
| 👤 User | `demo_user` | `user123` |

> ⚠️ Change these passwords after your first login.

---

## 📖 App Manual

> [`manuel (1) (2).pdf`](manuel%20(1)%20(2).pdf) is a **step-by-step guide** to using every part of Raksha — for both regular users and administrators.

<details>
<summary><b>👤 What the User Guide covers</b></summary>

- Logging in and navigating the home screen
- Browsing movies and series in scrollable rows
- Watching content with the built-in video player
- Player controls: play/pause, seek, volume, fullscreen
- Watching episodes of a series by season
- Viewing your watch history
- Logging out

</details>

<details>
<summary><b>🛠️ What the Admin Guide covers</b></summary>

- Accessing the Admin Dashboard (loads automatically on admin login)
- Adding, editing, and deleting **movies**
- Managing **series** and their **episodes** per season
- Creating and renaming **categories**
- Viewing all users, changing their role, or deactivating their account

</details>

<details>
<summary><b>🔧 Troubleshooting</b></summary>

Common issues and fixes — wrong credentials, missing database, video not playing, blank content rows, and more. All detailed in [`manuel (1) (2).pdf`](manuel%20(1)%20(2).pdf).

</details>

---

## 🧠 Challenges & Solutions

### 1 · Role-Based Scene Management

> JavaFX has no built-in routing system.

**→ Solution:** Built a central `SceneManager` utility that maps roles to FXML roots. On login, the controller queries the user's role from the DB, then delegates to `SceneManager` to load and swap the correct scene on the primary Stage — clean, no duplication.

---

### 2 · Secure Password Storage

> Storing plain-text passwords is a critical vulnerability.

**→ Solution:** Used Java's built-in `MessageDigest` with **SHA-256** to hash passwords before storage. On login, the input is hashed and compared to the stored hash — no plain text ever reaches the database.

```java
MessageDigest md = MessageDigest.getInstance("SHA-256");
byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
```

---

### 3 · Dynamic Content Loading Without UI Freezes

> JavaFX runs on a single UI thread — blocking it with DB queries freezes the app.

**→ Solution:** Offloaded all database queries to JavaFX `Task` objects on background threads. Used `Platform.runLater()` to safely update UI components once data was ready — keeping the interface fully responsive.

```java
Task<List<Movie>> loadMovies = new Task<>() {
    @Override protected List<Movie> call() { return movieDAO.findAll(); }
};
loadMovies.setOnSucceeded(e -> Platform.runLater(() -> updateUI(loadMovies.getValue())));
new Thread(loadMovies).start();
```

---

## 📁 Project Structure

```
raksha/
├── jstreamdb (8) (2).sql      ← Import this into MySQL first
├── manuel (1) (2).pdf         ← User & Admin guide
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   ├── controllers/   # JavaFX controllers (MVC)
        │   ├── dao/           # Data Access Objects
        │   ├── models/        # Entity classes
        │   ├── services/      # Business logic
        │   └── utils/         # SceneManager, HashUtil, etc.
        └── resources/
            ├── fxml/          # Scene layouts
            ├── css/           # Stylesheets
            └── db.properties  # DB credentials
```

---

## 👩‍💻 Developer

<div align="center">

**Emna Ben Ali**  
Software Engineering Student · Faculty of Sciences of Bizerte · GLSI

[![Portfolio](https://img.shields.io/badge/Portfolio-Visit-E50914?style=for-the-badge&logo=googlechrome&logoColor=white)](https://baliemna2222-dev.github.io/emna-benali.github.io/index.html)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/baliemna2222-dev)

</div>

---

<div align="center">

*Built with ☕ Java, 🎨 JavaFX, and ∞ ambition.*

**© 2026 — Emna Ben Ali**

</div>
