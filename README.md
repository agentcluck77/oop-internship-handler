<div align="center">

# Internship Placement Management System (IPMS) <br/><br/> SC2002 Project

</div> 
<br/>

## Overview

IPMS is a Java console application developed for the SC2002 Object-Oriented Design & Programming course at Nanyang Technological University. The system acts as a centralized hub for Students, Company Representatives, and Career Center Staff, streamlining internship application management while emphasizing reusability, extensibility, and maintainability.

## Features
- Student workflows: browse filtered internships, apply, track status, withdraw, accept placements.
- Company representative workflows: register for approval, create/edit/delete postings (pre-approval), review applications, toggle visibility.
- Staff workflows: approve reps, approve internships, process withdrawals, view filtered reports.
- Business rules enforced through dedicated managers and services.

## Getting Started
1. Ensure JDK 17+ is installed and on your PATH.
2. Clone this repository
3. Compile and run:
   ```bash
   javac -d out src/*.java
   java -cp out Main
   ```

Seed data is loaded from `students.csv` and `staff.csv` at startup. Company representatives self-register within the application.

## Testing
- **Automated JUnit Suite:** `scripts/run_tests.sh` rebuilds the project, compiles tests under `test/`, and runs the JUnit 5 console launcher (requires `lib/junit-platform-console-standalone.jar` which is already included).
- **Integration Coverage:** `FullWorkflowIntegrationTest` exercises the full happy-path scenario (rep registration → staff approval → student apply/accept).

## Project Structure
- `src/` – production code organized by controllers, services, managers, UI, and factories.
- `test/` – JUnit tests (unit + integration) with helper utilities.
- `lib/` – JUnit 5 standalone console jar.
- `scripts/` – utility scripts.
- `docs/` – assignment materials, UML diagrams.
- `students.csv`, `staff.csv` – sample data files loaded on startup.

## Team Members
- Aasish Mamidi
- Aloysius Chia Chu-Yang
- Arya Rajesh Kudlur
- Chong Xin Le
- Muhammad Izzat Bin Ramlee
