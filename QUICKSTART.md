# Quick Start Guide

## Compile
```bash
cd src
javac model/*.java repository/*.java service/*.java util/*.java ui/*.java
```

## Run
```bash
cd /Users/aasish/Downloads/oop-internship-handler-main
java -cp src ui.Main
```

## Test Login Credentials

### Student
- User ID: `U1234567A`
- Password: `pass123`

### Staff (Career Center)
- User ID: `staff001`
- Password: `admin123`

### Company Representative
1. Choose option 2 from main menu to register
2. Provide email, name, company details
3. Login as staff and approve the company rep
4. Now the company rep can login

## Example Workflow

### 1. Register & Approve Company Rep
```
Main Menu → 2 (Register) → Fill details
Login as staff → 1 (Approve Reps) → Select & Approve
```

### 2. Create & Approve Internship
```
Login as CompanyRep → 2 (Create Internship) → Fill details
Login as Staff → 2 (Approve Internships) → Select & Approve
```

### 3. Student Apply & Accept
```
Login as Student → 1 (View Available) → 3 (Apply) → Enter ID
Login as CompanyRep → 6 (Approve Application)
Login as Student → 4 (Accept Placement)
```

### 4. Withdrawal Workflow
```
Login as Student → 5 (Request Withdrawal) → Enter reason
Login as Staff → 3 (Approve Withdrawals) → Select & Approve
```

## All Features
- ✅ Login/Logout
- ✅ Password Change
- ✅ Company Rep Registration & Approval
- ✅ Internship Creation (max 5 per rep, max 10 slots)
- ✅ Internship Approval by Staff
- ✅ Student Applications (max 3 pending)
- ✅ Application Approval/Rejection
- ✅ Placement Acceptance (auto-withdraws others)
- ✅ Withdrawal Requests & Approval
- ✅ Visibility Toggle
- ✅ Report Generation with Filters
- ✅ Eligibility Checks (year/major/level)
- ✅ Slot Management (auto increment/decrement)

## Design Highlights
- **SOLID Principles**: Full compliance
- **Repository Pattern**: Data access abstraction
- **Service Layer**: Business logic isolation
- **Dependency Injection**: Constructor injection throughout
- **Clean Architecture**: model → repository → service → UI
