# MediTrack - Clinic Appointment Management System

A comprehensive Java-based clinic appointment management system demonstrating advanced OOP concepts, design patterns, and best practices.

## Overview

MediTrack is a console-based application for managing clinic operations including patient registration, doctor management, appointment scheduling, and billing. The system showcases proficiency in Java fundamentals, object-oriented programming, design patterns, and modern Java features.

## Features

### Core Functionality
- **Patient Management**: Register, search, update, and delete patient records
- **Doctor Management**: Manage doctor profiles with specializations and fees
- **Appointment Scheduling**: Book, cancel, reschedule, and complete appointments
- **Billing System**: Generate bills with multiple billing strategies (Standard, Emergency)
- **Payment Processing**: Handle payments with various payment methods
- **Search & Analytics**: Dynamic search with stream-based analytics

### Advanced Features
- **File Persistence**: CSV-based data persistence with try-with-resources
- **AI Recommendations**: Rule-based doctor recommendations by symptoms
- **Observer Pattern**: Real-time appointment notifications
- **Strategy Pattern**: Pluggable billing strategies
- **Factory Pattern**: Centralized object creation
- **Singleton Pattern**: Thread-safe ID generation

## Technology Stack

- **Language**: Java 17+ (compatible with Java 21)
- **Build Tool**: Maven 3.x
- **Architecture**: Layered architecture (Entity, Service, Util, UI)
- **Design Patterns**: Singleton, Factory, Strategy, Observer, Builder
- **Java Features**: Streams, Lambdas, Generics, Enums, Cloning, Immutability

## Project Structure

```
MediTrack-Clinic-Appointment-Management-System/
├── docs/
│   ├── Setup_Instructions.md      # Environment setup guide
│   ├── JVM_Report.md              # JVM internals documentation
│   ├── design-patterns-used.md    # Design patterns documentation
│   └── design-principles-used.md  # Design principles documentation
├── src/main/java/com/airtribe/meditrack/
│   ├── entity/                    # Domain entities
│   │   ├── appointment/           # Appointment-related entities
│   │   ├── billing/               # Billing-related entities
│   │   ├── persons/               # Person entities (Doctor, Patient)
│   │   ├── factory/               # Factory implementations
│   │   └── idgenerators/          # ID generation (Singleton)
│   ├── service/                   # Business logic layer
│   ├── util/                      # Utility classes
│   ├── interfaces/                # Interface definitions
│   ├── exception/                 # Custom exceptions
│   ├── constants/                 # Application constants
│   ├── strategey/                 # Strategy pattern implementations
│   ├── ui/                        # Console UI menus
│   ├── test/                      # Manual test runners
│   └── Main.java                  # Application entry point
├── pom.xml                        # Maven configuration
└── problem-statement.md          # Assignment requirements
```

## Installation & Setup

### Prerequisites
- JDK 17 or higher
- Maven 3.x (optional, can use javac directly)

### Build Instructions

#### Using Maven (Recommended)
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.airtribe.meditrack.Main"
```

#### Using javac
```bash
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
java -cp out com.airtribe.meditrack.Main
```

### Command-line Arguments
```bash
# Load persisted data from CSV on startup
java -cp out com.airtribe.meditrack.Main --loadData
```

## Usage

### Main Menu
```
==========================================
        MEDITRACK CLINIC SYSTEM          
==========================================
1. Patient Operations
2. Doctor Operations
3. Appointment Operations
4. Billing & Payment
0. Exit
Select an option:
```

### Patient Operations
- Register new patients
- Search patients by ID, name, or age
- Update medical history
- Delete patient records
- Clone patient records (deep copy demonstration)

### Doctor Operations
- Add new doctors with specialization
- Search doctors by name, specialization, or fee
- Update doctor fees
- View doctor analytics (average fee, appointments per doctor)

### Appointment Operations
- Book appointments with validation
- Cancel appointments (with state guards)
- Reschedule appointments
- Complete appointments
- Search by various criteria (patient, doctor, status, date)
- View analytics (revenue, average fee, status distribution)

### Billing Operations
- Generate bills using different strategies
- Process payments with multiple methods
- View bill summaries
- Apply discounts and taxes

## Design Patterns

### Singleton Pattern
- **Location**: `IdGenerators.java`
- **Purpose**: Ensure single instance for ID generation
- **Implementation**: Initialization-on-demand holder idiom (thread-safe, lazy)

### Factory Pattern
- **Location**: `BillFactory.java`, `DoctorFactory.java`
- **Purpose**: Centralized object creation with type selection
- **Implementation**: Interface-based factory with strategy selection

### Strategy Pattern
- **Location**: `BillingStrategey.java` with implementations
- **Purpose**: Pluggable billing algorithms
- **Implementations**: StandardBillingStrategy, EmergencyBillingStrategy

### Observer Pattern
- **Location**: `AppointmentObserver.java`, `ConsoleReminderObserver.java`
- **Purpose**: Real-time appointment notifications
- **Usage**: Notify observers on booking, cancellation, rescheduling, completion

### Builder Pattern
- **Location**: All entity classes (Person, Doctor, Patient, Appointment, Bill, BillSummary)
- **Purpose**: Fluent object construction with optional parameters
- **Implementation**: Nested static builder classes

*For detailed documentation, see [docs/design-patterns-used.md](docs/design-patterns-used.md)*

## Design Principles

### SOLID Principles
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension, closed for modification (Strategy pattern)
- **Liskov Substitution**: Subtypes are substitutable for base types
- **Interface Segregation**: Small, focused interfaces (Searchable, Payable)
- **Dependency Inversion**: Depend on abstractions, not concretions

### Other Principles
- **DRY (Don't_REPEAT Yourself)**: Reusable utility classes
- **Encapsulation**: Private fields with controlled access
- **Immutability**: BillSummary is immutable
- **Defensive Copying**: Date objects are defensively copied

*For detailed documentation, see [docs/design-principles-used.md](docs/design-principles-used.md)*

## Testing

### Manual Test Runner
```bash
java -cp out com.airtribe.meditrack.test.AppointmentTestRunner
```

### Test Coverage
- Booking and retrieval
- Validation and error handling
- Cancellation and rescheduling
- Deep cloning
- Observer pattern
- Streams and analytics
- CSV persistence round-trip

## Documentation

- [Setup Instructions](docs/Setup_Instructions.md) - Environment setup and configuration
- [JVM Report](docs/JVM_Report.md) - JVM internals and architecture
- [Design Patterns](docs/design-patterns-used.md) - In-depth design pattern documentation
- [Design Principles](docs/design-principles-used.md) - In-depth design principle documentation

## Learning Objectives Demonstrated

1. Java setup and JVM basics (JDK, JRE, JVM internals)
2. Core OOP: encapsulation, inheritance, polymorphism, abstraction
3. Advanced OOP: cloning (deep vs shallow), immutability, enums, static initialization
4. Collections, generics, comparators, iterators, equals/hashCode
5. Exception handling (custom exceptions, chaining, try-with-resources)
6. File I/O, CSV parsing, serialization/deserialization
7. Intro to concurrency: threads, synchronization, AtomicInteger, TimerTask
8. Design patterns: Singleton, Factory, Strategy, Template Method, Observer
9. Java 8+ features: streams & lambdas
10. Testing (manual runner), JavaDocs, and command-line usage

## Program Output

### Sample Session

```
==========================================
        MEDITRACK CLINIC SYSTEM          
==========================================
1. Patient Operations
2. Doctor Operations
3. Appointment Operations
4. Billing & Payment
0. Exit
Select an option: 1

--- Patient Menu ---
1. Register Patient
2. Search Patient by ID
3. Search Patient by Name
4. Search Patient by Age
5. Update Medical History
6. Delete Patient
7. Clone Patient
8. List All Patients
0. Back to Main Menu
Select an option: 1

Enter first name: John
Enter last name: Doe
Enter age: 35
Enter gender (MALE/FEMALE/OTHER): MALE
Enter medical history: Hypertension, Diabetes

Patient registered successfully!
Patient ID: 1
Name: John Doe
Age: 35
Gender: MALE
Medical History: Hypertension, Diabetes

--- Patient Menu ---
1. Register Patient
2. Search Patient by ID
3. Search Patient by Name
4. Search Patient by Age
5. Update Medical History
6. Delete Patient
7. Clone Patient
8. List All Patients
0. Back to Main Menu
Select an option: 0

==========================================
        MEDITRACK CLINIC SYSTEM          
==========================================
1. Patient Operations
2. Doctor Operations
3. Appointment Operations
4. Billing & Payment
0. Exit
Select an option: 2

--- Doctor Menu ---
1. Add Doctor
2. Search Doctor by ID
3. Search Doctor by Name
4. Search Doctor by Specialization
5. Update Doctor Fees
6. List All Doctors
7. View Doctor Analytics
0. Back to Main Menu
Select an option: 1

Enter first name: Arjun
Enter last name: Mehta
Enter age: 45
Enter gender (MALE/FEMALE/OTHER): MALE
Enter specialization (MBBS, CARDIOLOGY, DERMATOLOGY, NEUROLOGY, ORTHOPEDICS, PEDIATRICS): CARDIOLOGY
Enter consultation fees: 500.0

Doctor registered successfully!
Doctor ID: 1
Name: Dr. Arjun Mehta
Age: 45
Gender: MALE
Specialization: CARDIOLOGY
Fees: 500.0

--- Doctor Menu ---
1. Add Doctor
2. Search Doctor by ID
3. Search Doctor by Name
4. Search Doctor by Specialization
5. Update Doctor Fees
6. List All Doctors
7. View Doctor Analytics
0. Back to Main Menu
Select an option: 0

==========================================
        MEDITRACK CLINIC SYSTEM          
==========================================
1. Patient Operations
2. Doctor Operations
3. Appointment Operations
4. Billing & Payment
0. Exit
Select an option: 3

--- Appointment Menu ---
1. Book Appointment
2. View Appointment
3. Cancel Appointment
4. Reschedule Appointment
5. Complete Appointment
6. Search Appointments
7. View Analytics
8. Save to CSV
9. Load from CSV
0. Back to Main Menu
Select an option: 1

Available Doctors:
1. Dr. Arjun Mehta (CARDIOLOGY) - Fees: 500.0
Select doctor ID: 1

Enter appointment date (YYYY-MM-DD HH:MM): 2026-09-15 10:00
Enter appointment type (INITIAL/FOLLOWUP): INITIAL

Appointment booked successfully!
Appointment ID: 1
Date: 2026-09-15 10:00
Status: PENDING
Type: INITIAL
Fees: 500.0
Doctor: Dr. Arjun Mehta
Patient: John Doe

[Console Reminder]: Appointment #1 booked for 2026-09-15 10:00

--- Appointment Menu ---
1. Book Appointment
2. View Appointment
3. Cancel Appointment
4. Reschedule Appointment
5. Complete Appointment
6. Search Appointments
7. View Analytics
8. Save to CSV
9. Load from CSV
0. Back to Main Menu
Select an option: 7

--- Appointment Analytics ---
Total Appointments: 1
Pending: 1
Cancelled: 0
Completed: 0
Total Revenue: 500.0
Average Fee: 500.0
Appointments per Doctor:
  Dr. Arjun Mehta (ID: 1): 1

--- Appointment Menu ---
1. Book Appointment
2. View Appointment
3. Cancel Appointment
4. Reschedule Appointment
5. Complete Appointment
6. Search Appointments
7. View Analytics
8. Save to CSV
9. Load from CSV
0. Back to Main Menu
Select an option: 0

==========================================
        MEDITRACK CLINIC SYSTEM          
==========================================
1. Patient Operations
2. Doctor Operations
3. Appointment Operations
4. Billing & Payment
0. Exit
Select an option: 4

--- Billing Menu ---
1. Generate Bill
2. Process Payment
3. View Bill Summary
0. Back to Main Menu
Select an option: 1

Enter appointment ID: 1

*****************************************
              INVOICE                    
*****************************************
Bill ID: 1
Patient Name: JohnDoe
Attending Doctor: Arjun Mehta
Date: Fri Sep 12 16:00:00 UTC 2026
-----------------------------------------
Description			Amount
-----------------------------------------
Doctor Fees:			500.00
Appointment Fees:		500.00
-----------------------------------------
Subtotal:			1000.00
Tax (8.0%):			80.00
-----------------------------------------
Total Amount Payable:		1080.00
*****************************************

--- Billing Menu ---
1. Generate Bill
2. Process Payment
3. View Bill Summary
0. Back to Main Menu
Select an option: 0

==========================================
        MEDITRACK CLINIC SYSTEM          
==========================================
1. Patient Operations
2. Doctor Operations
3. Appointment Operations
4. Billing & Payment
0. Exit
Select an option: 0

Exiting MediTrack system. Goodbye!
```

## License

This project is created for educational purposes as part of the Java assignment.

## Contact

For questions or issues, please refer to the assignment documentation or contact the instructor.
