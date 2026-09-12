# Brief
## Learning Objectives
### By completing MediTrack mentees will demonstrate proficiency in:

1. Java setup and JVM basics (JDK, JRE, JVM internals).

2. Core OOP: encapsulation, inheritance, polymorphism, abstraction.

3. Advanced OOP: cloning (deep vs shallow), immutability, enums, static initialization.

4. Collections, generics, comparators, iterators, equals/hashCode.

5. Exception handling (custom exceptions, chaining, try-with-resources).

6. File I/O, CSV parsing, serialization/deserialization.

7. Intro to concurrency: threads, synchronization, AtomicInteger, TimerTask.

8. Design patterns: Singleton, Factory, Strategy, Template Method, Observer (optional).

9. Java 8+ features: streams & lambdas.

10. Testing (manual runner), JavaDocs, and command-line usage.

11. Git-based collaboration.

12. Project Requirements & Grading Breakdown

13. Environment Setup & JVM Understanding (10 pts)

14. Install & configure Java (JDK/JRE). Provide Setup_Instructions.md with screenshots.

15. JVM_Report.md covering:

16. Class Loader

17. Runtime Data Areas (Heap, Stack, Method Area, PC Register)

18. Execution Engine

19. JIT Compiler vs Interpreter

20. "Write Once, Run Anywhere"

21. Deliverables: docs/Setup_Instructions.md, docs/JVM_Report.md

## Package Structure & Java Basics (10 pts)


1. Base package: com.airtribe.meditrack

2. Sub-packages (updated to include missing pointers):

3. entity – Person, Doctor, Patient, Appointment, Bill, BillSummary (immutable)

4. service – DoctorService, PatientService, AppointmentService

5. util – Validator, DateUtil, CSVUtil, IdGenerator, AIHelper (optional), DataStore<T> (generic)

6. exception – AppointmentNotFoundException, InvalidDataException

7. interface – Searchable, Payable

8. constants – Constants (tax rate, file paths)

9. test – TestRunner (manual tests)

10. Demonstrate:

11. Access modifiers

12. Variable scopes (static vs instance); use static blocks & initialization

13. Primitive types and casting

## Core OOP Implementation (35 pts)

1. Encapsulation (8 pts)
2. Private fields + getters/setters
3. Centralized validation via Validator 
4. Inheritance (10 pts)
5. Person → Doctor, Patient 
6. Use super, this, constructor chaining

### Polymorphism (7 pts)


1. Overloading: searchPatient() by ID / name / age
2. Overriding: generateBill() behavior in appropriate classes
3. Demonstrate dynamic dispatch
4. Abstraction & Interfaces (10 pts)
5. Abstract class MedicalEntity for common behavior 
6. Interfaces: Payable, Searchable with default methods where suitable

### Advanced OOP Additions


1. Deep vs Shallow Copy: implement Cloneable for Patient and Appointment, demonstrate deep copy semantics (clone nested objects correctly).
2. Immutable Class: BillSummary — final fields, no setters, thread-safe. 
3. Enums: Specialization, AppointmentStatus (e.g., CONFIRMED, CANCELLED, PENDING) instead of strings. 
4. Static blocks: initialize application-wide config or counters.

### Application Logic (15 pts)


1. CRUD for Patients & Doctors. 
2. Appointments: create, view, cancel. Use AppointmentStatus enum. 
3. Billing: Bill object, taxes, multiple billing strategies (Strategy Pattern bonus). 
4. Search: dynamic search for doctors/patients.

### Menu-driven console UI in Main.java.

1. Use ArrayList, HashMap, DataStore<T> generic class for storage.
2. Bonus Features — choose any two (20 pts total)


### A. File I/O & Persistence (10 pts)


1. Save/load Patient/Doctor/Appointment via CSV and/or Java Serialization.

2. Use try-with-resources. Implement CSVUtil with String.split(",").

3. Persisted data loaded by --loadData command-line arg.

### B. Design Patterns (10 pts)


1. Singleton — App configuration / IdGenerator (eager & lazy examples).

2. Factory — bill creation (refined factory returning different Bill types).

3. Observer — appointment notifications (console reminders).

### C. AI Feature (10 pts)


1. Rule-based doctor recommendation by symptoms. Auto-suggest appointment slots.

### D. Java Streams + Lambdas (10 pts)

1. Filter doctors by specialization, compute average fee, analytics (appointments per doctor) using streams.




MediTrack- JAVA.pdf


Submission guidelines
• Code Repository: Push complete project to GitHub with proper README.md
• Submission Format:
○ Email with GitHub repository link
○ Subject: "Java Assignment - [Your Name]"

