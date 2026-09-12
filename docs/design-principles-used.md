# Design Principles Used in MediTrack

This document provides an in-depth analysis of the design principles implemented in the MediTrack Clinic Appointment Management System, explaining how and where each principle is applied and the benefits it provides.

---

## Table of Contents

1. [SOLID Principles](#solid-principles)
   - [Single Responsibility Principle (SRP)](#1-single-responsibility-principle-srp)
   - [Open/Closed Principle (OCP)](#2-openclosed-principle-ocp)
   - [Liskov Substitution Principle (LSP)](#3-liskov-substitution-principle-lsp)
   - [Interface Segregation Principle (ISP)](#4-interface-segregation-principle-isp)
   - [Dependency Inversion Principle (DIP)](#5-dependency-inversion-principle-dip)
2. [Other Key Principles](#other-key-principles)
   - [DRY (Don't Repeat Yourself)](#6-dry-dont-repeat-yourself)
   - [KISS (Keep It Simple, Stupid)](#7-kiss-keep-it-simple-stupid)
   - [Encapsulation](#8-encapsulation)
   - [Immutability](#9-immutability)
   - [Defensive Copying](#10-defensive-copying)
   - [Composition over Inheritance](#11-composition-over-inheritance)
   - [Separation of Concerns](#12-separation-of-concerns)
   - [Tell, Don't Ask](#13-tell-dont-ask)

---

## SOLID Principles

### 1. Single Responsibility Principle (SRP)

#### Definition
A class should have only one reason to change. Each class should have a single, well-defined responsibility.

#### Implementation in MediTrack

##### Service Layer Classes
Each service class has a single, focused responsibility:

**PatientService.java**
```java
public class PatientService {
    // Responsibility: Patient CRUD operations only
    public Patient registerPatient(Patient patient) { /* ... */ }
    public List<Patient> getAllPatients() { /* ... */ }
    public Optional<Patient> searchPatient(int id) { /* ... */ }
    public List<Patient> searchPatient(String name) { /* ... */ }
    public Patient updateMedicalHistory(int patientId, String newHistory) { /* ... */ }
    public boolean deletePatient(int patientId) { /* ... */ }
    public Patient clonePatient(int patientId) { /* ... */ }
}
```

**DoctorService.java**
```java
public class DoctorService {
    // Responsibility: Doctor CRUD operations only
    public void addDoctor(Doctor doctor) { /* ... */ }
    public Doctor getDoctorById(int docId) { /* ... */ }
    public List<Doctor> getAllDoctors() { /* ... */ }
    public List<Doctor> searchDoctors(String searchTerm) { /* ... */ }
    public boolean updateDoctor(Doctor doctor) { /* ... */ }
    public boolean deleteDoctor(int docId) { /* ... */ }
    // Doctor-specific operations
    public List<Doctor> getDoctorsBySpecialization(Specialization specialization) { /* ... */ }
    public double getAverageFee() { /* ... */ }
}
```

**AppointmentService.java**
```java
public class AppointmentService implements Searchable {
    // Responsibility: Appointment lifecycle management only
    public Appointment bookAppointment(Date date, AppointmentType type, 
                                       Doctor doctor, Patient patient) { /* ... */ }
    public Appointment viewAppointment(int appointmentId) { /* ... */ }
    public Appointment cancelAppointment(int appointmentId) { /* ... */ }
    public Appointment rescheduleAppointment(int appointmentId, Date newDate) { /* ... */ }
    public Appointment completeAppointment(int appointmentId) { /* ... */ }
    // Search operations
    public List<Appointment> searchAppointment(String patientName) { /* ... */ }
    public List<Appointment> searchAppointment(AppointmentStatus status) { /* ... */ }
}
```

**BillingService.java**
```java
public class BillingService {
    // Responsibility: Billing and payment operations only
    public Bill generateBill(Appointment appointment, BillingStrategey strategy) { /* ... */ }
    public BillSummary generateBillSummary(Bill bill) { /* ... */ }
    public Payment processPayment(BillSummary billSummary) { /* ... */ }
}
```

##### Utility Layer Classes
Each utility class has a single, focused responsibility:

**Validator.java**
```java
public class Validator {
    // Responsibility: Validation logic only
    public static boolean validatePatient(Patient patient) { /* ... */ }
    public static void validateAppointment(Date date, AppointmentType type, 
                                           double fees, Doctor doctor, Patient patient) { /* ... */ }
    public static boolean isValidName(String name) { /* ... */ }
    public static boolean isValidId(int id) { /* ... */ }
}
```

**CSVUtil.java**
```java
public class CSVUtil {
    // Responsibility: CSV parsing and writing only
    public static List<String[]> readCSV(String filePath) { /* ... */ }
    public static void writeCSV(String filePath, List<String[]> data) { /* ... */ }
    public static String toCSVString(List<String[]> data) { /* ... */ }
    public static List<String[]> fromCSVString(String csvString) { /* ... */ }
}
```

**DateUtil.java**
```java
public class DateUtil {
    // Responsibility: Date formatting and parsing only
    public static String formatDate(Date date) { /* ... */ }
    public static String formatDateTime(Date date) { /* ... */ }
    public static Date parseDateTime(String dateStr) { /* ... */ }
    public static boolean isFuture(Date date) { /* ... */ }
    public static boolean isSameSlot(Date d1, Date d2) { /* ... */ }
}
```

#### Benefits and Outcomes

1. **Easier Maintenance**
   - Changes to patient logic only affect PatientService
   - Changes to validation logic only affect Validator
   - Reduces risk of unintended side effects

2. **Clear Code Organization**
   - Each class has a clear, focused purpose
   - Easy to locate where specific functionality is implemented
   - Improves code navigation and understanding

3. **Testability**
   - Each service can be tested independently
   - Unit tests are focused and simple
   - Mocking is straightforward

4. **Reusability**
   - Utility classes can be reused across different services
   - Validator is used by multiple services
   - CSVUtil is used for different entity types

5. **Parallel Development**
   - Different developers can work on different services simultaneously
   - Minimal merge conflicts
   - Clear ownership of functionality

#### Violations Avoided
- **Avoided**: Putting appointment logic in PatientService
- **Avoided**: Mixing validation with business logic in service classes
- **Avoided**: Putting persistence logic in entity classes
- **Avoided**: Combining UI logic with business logic

---

### 2. Open/Closed Principle (OCP)

#### Definition
Software entities should be open for extension but closed for modification. You should be able to extend a class's behavior without modifying it.

#### Implementation in MediTrack

##### Strategy Pattern for Billing
The billing system is open for extension (new strategies) but closed for modification (existing strategies unchanged).

**BillingStrategey Interface**
```java
public interface BillingStrategey {
    double calculateSubTotal(Bill bill);
    double calculateTaxAmount(Bill bill);
    double calculateTotal(Bill bill);
}
```

**Existing Strategies**
```java
public class StandardBillingStrategy implements BillingStrategey {
    @Override
    public double calculateSubTotal(Bill bill) {
        return bill.getDoctor_fees() + bill.getAppointmentFees();
    }
    @Override
    public double calculateTaxAmount(Bill bill) {
        return calculateSubTotal(bill) * Constants.getTAX_RATE();
    }
    @Override
    public double calculateTotal(Bill bill) {
        return calculateSubTotal(bill) + calculateTaxAmount(bill);
    }
}

public class EmergencyBillingStrategy implements BillingStrategey {
    // Different implementation for emergency billing
    @Override
    public double calculateSubTotal(Bill bill) {
        return bill.getDoctor_fees() + bill.getAppointmentFees();
    }
    // ... different tax calculation for emergency
}
```

**Extending with New Strategy (No Modification Needed)**
```java
// New strategy can be added without modifying existing code
public class InsuranceBillingStrategy implements BillingStrategey {
    @Override
    public double calculateSubTotal(Bill bill) {
        double subtotal = bill.getDoctor_fees() + bill.getAppointmentFees();
        return subtotal * (1 - Constants.getINSURANCE_COVERAGE());
    }
    @Override
    public double calculateTaxAmount(Bill bill) {
        return calculateSubTotal(bill) * Constants.getTAX_RATE();
    }
    @Override
    public double calculateTotal(Bill bill) {
        return calculateSubTotal(bill) + calculateTaxAmount(bill);
    }
}
```

##### Observer Pattern for Notifications
The notification system is open for new observer types but closed for modification of existing observers.

**AppointmentObserver Interface**
```java
public interface AppointmentObserver {
    default void onBooked(Appointment appointment) { }
    default void onCancelled(Appointment appointment) { }
    default void onRescheduled(Appointment appointment) { }
    default void onCompleted(Appointment appointment) { }
}
```

**Existing Observer**
```java
public class ConsoleReminderObserver implements AppointmentObserver {
    @Override
    public void onBooked(Appointment appointment) {
        System.out.println("[Console Reminder]: Appointment booked");
    }
}
```

**Extending with New Observer (No Modification Needed)**
```java
// New observer can be added without modifying existing code
public class EmailReminderObserver implements AppointmentObserver {
    private EmailService emailService;
    
    @Override
    public void onBooked(Appointment appointment) {
        emailService.sendAppointmentConfirmation(appointment);
    }
    
    @Override
    public void onCancelled(Appointment appointment) {
        emailService.sendCancellationNotice(appointment);
    }
}
```

##### Factory Pattern for Object Creation
Factories are open for new product types but closed for modification of existing products.

**BillFactory Interface**
```java
public interface BillFactory {
    Bill createBill(Appointment appointment, BillingStrategey billingStrategey);
    Bill createStandardBill(Appointment appointment);
    Bill createEmergencyBill(Appointment appointment);
}
```

**Extending with New Bill Type (No Modification Needed)**
```java
// Can add new factory methods without modifying existing ones
public interface BillFactory {
    Bill createBill(Appointment appointment, BillingStrategey billingStrategey);
    Bill createStandardBill(Appointment appointment);
    Bill createEmergencyBill(Appointment appointment);
    // New method added without modifying existing methods
    Bill createInsuranceBill(Appointment appointment, InsurancePolicy policy);
}
```

#### Benefits and Outcomes

1. **Stability**
   - Existing code is not modified when adding new features
   - Reduces risk of introducing bugs
   - Maintains backward compatibility

2. **Flexibility**
   - New behaviors can be added through extension
   - System can evolve with new requirements
   - Supports plugin-like architecture

3. **Maintainability**
   - Changes are localized to new classes
   - Existing classes remain stable
   - Easier to understand system evolution

4. **Testability**
   - New strategies can be tested independently
   - Existing tests continue to pass
   - Regression testing is simplified

5. **Scalability**
   - System can grow without becoming fragile
   - New features don't require extensive refactoring
   - Supports long-term maintenance

---

### 3. Liskov Substitution Principle (LSP)

#### Definition
Subtypes must be substitutable for their base types without altering the correctness of the program. If a program is using a base class, it should be able to use any subclass without knowing it.

#### Implementation in MediTrack

##### Person Hierarchy
Doctor and Patient are substitutable for Person in any context where Person is expected.

**Person (Base Class)**
```java
public abstract class Person implements Cloneable {
    private int p_id;
    private int age;
    private String F_name;
    private String L_name;
    private Gender gender;
    
    // Common methods
    public int getP_id() { return p_id; }
    public int getAge() { return age; }
    public String getF_name() { return F_name; }
    public String getL_name() { return L_name; }
    public Gender getGender() { return gender; }
    
    public Person clone() { /* ... */ }
    public boolean equals(Object o) { /* ... */ }
    public int hashCode() { /* ... */ }
}
```

**Doctor (Subclass)**
```java
public class Doctor extends Person implements Searchable {
    private int doc_id;
    private Specialization specialization;
    private double fees;
    
    // Inherits all Person methods
    // Adds Doctor-specific methods
    public int getDoc_id() { return doc_id; }
    public Specialization getSpecialization() { return specialization; }
    public double getFees() { return fees; }
    
    // Overrides clone() but maintains contract
    @Override
    public Doctor clone() { /* ... */ }
}
```

**Patient (Subclass)**
```java
public class Patient extends Person implements Searchable {
    private int pat_id;
    private String medical_history;
    
    // Inherits all Person methods
    // Adds Patient-specific methods
    public int getPat_id() { return pat_id; }
    public String getMedical_history() { return medical_history; }
    
    // Overrides clone() but maintains contract
    @Override
    public Patient clone() { /* ... */ }
}
```

**Substitutable Usage**
```java
// Any method expecting Person can accept Doctor or Patient
public void processPerson(Person person) {
    System.out.println("Processing: " + person.getF_name() + " " + person.getL_name());
    System.out.println("Age: " + person.getAge());
    // Works for both Doctor and Patient
}

processPerson(new Doctor(45, "Arjun", "Mehta", Gender.MALE));  // Works
processPerson(new Patient(35, "John", "Doe", Gender.MALE));   // Works
```

##### Billing Strategy Hierarchy
All billing strategies are substitutable for BillingStrategey.

**BillingStrategey (Interface)**
```java
public interface BillingStrategey {
    double calculateSubTotal(Bill bill);
    double calculateTaxAmount(Bill bill);
    double calculateTotal(Bill bill);
}
```

**StandardBillingStrategy (Implementation)**
```java
public class StandardBillingStrategy implements BillingStrategey {
    @Override
    public double calculateSubTotal(Bill bill) {
        return bill.getDoctor_fees() + bill.getAppointmentFees();
    }
    @Override
    public double calculateTaxAmount(Bill bill) {
        return calculateSubTotal(bill) * Constants.getTAX_RATE();
    }
    @Override
    public double calculateTotal(Bill bill) {
        return calculateSubTotal(bill) + calculateTaxAmount(bill);
    }
}
```

**EmergencyBillingStrategy (Implementation)**
```java
public class EmergencyBillingStrategy implements BillingStrategey {
    @Override
    public double calculateSubTotal(Bill bill) {
        return bill.getDoctor_fees() + bill.getAppointmentFees();
    }
    @Override
    public double calculateTaxAmount(Bill bill) {
        return calculateSubTotal(bill) * Constants.getTAX_RATE();
    }
    @Override
    public double calculateTotal(Bill bill) {
        return calculateSubTotal(bill) + calculateTaxAmount(bill);
    }
}
```

**Substitutable Usage**
```java
// Any method expecting BillingStrategey can accept any implementation
public void processBilling(Bill bill, BillingStrategey strategy) {
    double subtotal = strategy.calculateSubTotal(bill);
    double tax = strategy.calculateTaxAmount(bill);
    double total = strategy.calculateTotal(bill);
    System.out.println("Total: " + total);
}

processBilling(bill, new StandardBillingStrategy());   // Works
processBilling(bill, new EmergencyBillingStrategy());  // Works
```

##### Observer Hierarchy
All observers are substitutable for AppointmentObserver.

**AppointmentObserver (Interface)**
```java
public interface AppointmentObserver {
    default void onBooked(Appointment appointment) { }
    default void onCancelled(Appointment appointment) { }
    default void onRescheduled(Appointment appointment) { }
    default void onCompleted(Appointment appointment) { }
}
```

**Substitutable Usage**
```java
// Any method expecting AppointmentObserver can accept any implementation
public void registerObserver(AppointmentObserver observer) {
    observers.add(observer);
}

registerObserver(new ConsoleReminderObserver());  // Works
registerObserver(new EmailReminderObserver());    // Would work
registerObserver(new SMSReminderObserver());      // Would work
```

#### Benefits and Outcomes

1. **Polymorphism**
   - Code can work with base types without knowing concrete implementations
   - Enables flexible, reusable code
   - Supports runtime polymorphism

2. **Reliability**
   - Subclasses maintain the contract of the base class
   - Behavior is predictable when using subclasses
   - Reduces runtime errors

3. **Extensibility**
   - New subclasses can be added without modifying client code
   - System can grow with new implementations
   - Supports plugin architecture

4. **Testability**
   - Can test with mock implementations
   - Can substitute test doubles for real implementations
   - Simplifies unit testing

5. **Code Reuse**
   - Code written for base types works with all subclasses
   - Reduces code duplication
   - Promotes generic algorithms

#### LSP Violations Avoided
- **Avoided**: Doctor or Patient breaking Person's contract (e.g., throwing unexpected exceptions)
- **Avoided**: Billing strategies returning invalid values (e.g., negative totals)
- **Avoided**: Observers throwing unchecked exceptions that break the notification system

---

### 4. Interface Segregation Principle (ISP)

#### Definition
Clients should not be forced to depend on interfaces they don't use. Many small, specific interfaces are better than one large, general-purpose interface.

#### Implementation in MediTrack

##### Searchable Interface
Focused on search functionality only, not mixed with other concerns.

**Searchable.java**
```java
public interface Searchable {
    boolean matches(String searchTerm);
    String getSearchKey();
    boolean matchesId(int id);
    boolean matchesName(String name);
    
    // Default methods for convenience
    default boolean exists(int id) {
        return matchesId(id);
    }
    default int count() {
        return 1;
    }
}
```

**Usage**
```java
// Doctor implements Searchable for search functionality
public class Doctor extends Person implements Searchable {
    @Override
    public boolean matches(String searchTerm) { /* ... */ }
    @Override
    public String getSearchKey() { /* ... */ }
    @Override
    public boolean matchesId(int id) { /* ... */ }
    @Override
    public boolean matchesName(String name) { /* ... */ }
}

// Patient implements Searchable for search functionality
public class Patient extends Person implements Searchable {
    @Override
    public boolean matches(String searchTerm) { /* ... */ }
    @Override
    public String getSearchKey() { /* ... */ }
    @Override
    public boolean matchesId(int id) { /* ... */ }
    @Override
    public boolean matchesName(String name) { /* ... */ }
}
```

**Avoided Anti-Pattern**
```java
// BAD: Large interface with unrelated methods
public interface EntityOperations {
    // Search methods
    boolean matches(String searchTerm);
    boolean matchesId(int id);
    
    // Payment methods (unrelated to search)
    double processPayment();
    String getPaymentStatus();
    
    // Persistence methods (unrelated to search)
    void saveToDatabase();
    void loadFromDatabase();
    
    // Validation methods (unrelated to search)
    boolean validate();
}
```

##### Payable Interface
Focused on payment functionality only, not mixed with other concerns.

**Payable.java**
```java
public interface Payable {
    default double processPayment() {
        return 0.0;
    }
    
    default String getPaymentStatus() {
        return "UNKNOWN";
    }
    
    default boolean validatePayment() {
        return true;
    }
}
```

**Usage**
```java
// Bill implements Payable for payment functionality
public class Bill extends Appointment implements Payable {
    @Override
    public double processPayment() {
        // Custom payment processing
        return getTotalAmount();
    }
    
    @Override
    public String getPaymentStatus() {
        // Custom status logic
        return "PENDING";
    }
}
```

##### AppointmentObserver Interface
Focused on notification events only, with default implementations for flexibility.

**AppointmentObserver.java**
```java
public interface AppointmentObserver {
    default void onBooked(Appointment appointment) {
        // Default: do nothing
    }
    
    default void onCancelled(Appointment appointment) {
        // Default: do nothing
    }
    
    default void onRescheduled(Appointment appointment) {
        // Default: do nothing
    }
    
    default void onCompleted(Appointment appointment) {
        // Default: do nothing
    }
}
```

**Usage**
```java
// Observer can implement only the methods it needs
public class ConsoleReminderObserver implements AppointmentObserver {
    @Override
    public void onBooked(Appointment appointment) {
        System.out.println("[Reminder]: Appointment booked");
    }
    
    // Other methods use default (do nothing)
}

// Another observer might implement different methods
public class AnalyticsObserver implements AppointmentObserver {
    @Override
    public void onBooked(Appointment appointment) {
        analytics.trackBooking(appointment);
    }
    
    @Override
    public void onCancelled(Appointment appointment) {
        analytics.trackCancellation(appointment);
    }
    
    // Other methods use default (do nothing)
}
```

##### BillingStrategey Interface
Focused on billing calculations only, not mixed with persistence or UI.

**BillingStrategey.java**
```java
public interface BillingStrategey {
    double calculateSubTotal(Bill bill);
    double calculateTaxAmount(Bill bill);
    double calculateTotal(Bill bill);
}
```

#### Benefits and Outcomes

1. **Focused Interfaces**
   - Each interface has a single, clear purpose
   - Easy to understand what the interface provides
   - Reduces cognitive load

2. **Flexible Implementation**
   - Classes can implement only the interfaces they need
   - Not forced to implement unused methods
   - Reduces boilerplate code

3. **Loose Coupling**
   - Clients depend only on methods they actually use
   - Changes to unrelated methods don't affect clients
   - Improves maintainability

4. **Easier Testing**
   - Can create focused test doubles
   - Mock only the methods needed for testing
   - Simplifies test setup

5. **Better Documentation**
   - Interface name clearly indicates its purpose
   - Methods are logically grouped
   - Self-documenting code

#### ISP Violations Avoided
- **Avoided**: Creating a single `Entity` interface with all possible methods
- **Avoided**: Forcing Doctor to implement payment methods it doesn't need
- **Avoided**: Forcing Patient to implement billing methods it doesn't use
- **Avoided**: Mixing persistence methods with business logic interfaces

---

### 5. Dependency Inversion Principle (DIP)

#### Definition
High-level modules should not depend on low-level modules. Both should depend on abstractions. Abstractions should not depend on details. Details should depend on abstractions.

#### Implementation in MediTrack

##### Service Layer Depends on Interfaces, Not Implementations
High-level services depend on interfaces, allowing low-level implementations to vary.

**BillingStrategey (Abstraction)**
```java
public interface BillingStrategey {
    double calculateSubTotal(Bill bill);
    double calculateTaxAmount(Bill bill);
    double calculateTotal(Bill bill);
}
```

**BillingService (High-Level Module) Depends on Abstraction**
```java
public class BillingService {
    // Depends on interface, not concrete implementations
    public Bill generateBill(Appointment appointment, BillingStrategey strategy) {
        Bill bill = factory.createBill(appointment, strategy);
        bill.generateBill();
        return bill;
    }
    
    // Can work with any BillingStrategey implementation
    public Bill generateStandardBill(Appointment appointment) {
        return generateBill(appointment, new StandardBillingStrategy());
    }
    
    public Bill generateEmergencyBill(Appointment appointment) {
        return generateBill(appointment, new EmergencyBillingStrategy());
    }
}
```

**StandardBillingStrategy (Low-Level Module)**
```java
public class StandardBillingStrategy implements BillingStrategey {
    @Override
    public double calculateSubTotal(Bill bill) {
        return bill.getDoctor_fees() + bill.getAppointmentFees();
    }
    // ... other methods
}
```

**EmergencyBillingStrategy (Low-Level Module)**
```java
public class EmergencyBillingStrategy implements BillingStrategey {
    @Override
    public double calculateSubTotal(Bill bill) {
        return bill.getDoctor_fees() + bill.getAppointmentFees();
    }
    // ... other methods
}
```

##### Factory Pattern for Object Creation
Factories depend on abstractions, not concrete classes.

**BillFactory (Abstraction)**
```java
public interface BillFactory {
    Bill createBill(Appointment appointment, BillingStrategey billingStrategey);
    Bill createStandardBill(Appointment appointment);
    Bill createEmergencyBill(Appointment appointment);
}
```

**BillingService Depends on Factory Interface**
```java
public class BillingService {
    private BillFactory factory;  // Depends on interface
    
    public BillingService(BillFactory factory) {
        this.factory = factory;  // Can inject any implementation
    }
    
    public Bill generateBill(Appointment appointment, BillingStrategey strategy) {
        return factory.createBill(appointment, strategy);
    }
}
```

**BillFactoryImpl (Low-Level Implementation)**
```java
public class BillFactoryImpl implements BillFactory {
    @Override
    public Bill createBill(Appointment appointment, BillingStrategey billingStrategey) {
        return new Bill.BillBuilder()
                .appointment_date(appointment.getAppointment_date())
                .status(appointment.getStatus())
                .type(appointment.getType())
                .appointmentFees(appointment.getAppointmentFees())
                .doctor(appointment.getDoctor())
                .patient(appointment.getPatient())
                .doctor_fees(appointment.getDoctor().getFees())
                .totalAmount(0.0)
                .billingStrategey(billingStrategey)
                .build();
    }
    // ... other methods
}
```

##### Observer Pattern for Notifications
Services depend on observer interface, not concrete observers.

**AppointmentObserver (Abstraction)**
```java
public interface AppointmentObserver {
    default void onBooked(Appointment appointment) { }
    default void onCancelled(Appointment appointment) { }
    default void onRescheduled(Appointment appointment) { }
    default void onCompleted(Appointment appointment) { }
}
```

**AppointmentService Depends on Observer Interface**
```java
public class AppointmentService implements Searchable {
    private final List<AppointmentObserver> observers = new ArrayList<>();
    
    // Depends on interface, not concrete observers
    public void register(AppointmentObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }
    
    public Appointment bookAppointment(Date date, AppointmentType type, 
                                       Doctor doctor, Patient patient) {
        // ... create appointment ...
        observers.forEach(observer -> observer.onBooked(appointment));
        return appointment;
    }
}
```

**ConsoleReminderObserver (Low-Level Implementation)**
```java
public class ConsoleReminderObserver implements AppointmentObserver {
    @Override
    public void onBooked(Appointment appointment) {
        System.out.println("[Console Reminder]: Appointment booked");
    }
    // ... other methods
}
```

##### DataStore Generic Class
High-level code depends on generic DataStore abstraction, not specific storage implementations.

**DataStore<T> (Abstraction)**
```java
public class DataStore<T> {
    private final Map<Integer, T> store = new HashMap<>();
    private final Map<String, T> store2 = new ConcurrentHashMap<>();
    private final List<T> listStore = new ArrayList<>();
    
    // Generic methods that work with any type
    public void add(String key, T value) { /* ... */ }
    public void put(int id, T entity) { /* ... */ }
    public T get(String key) { /* ... */ }
    public Optional<T> get(int id) { /* ... */ }
    public List<T> getAll() { /* ... */ }
    public List<T> filter(Predicate<T> predicate) { /* ... */ }
}
```

**Services Use DataStore Abstraction**
```java
public class PatientService {
    private final DataStore<Patient> patientStore = new DataStore<>();
    
    public Patient registerPatient(Patient patient) {
        patientStore.put(patient.getPat_id(), patient);
        return patient;
    }
    
    public List<Patient> getAllPatients() {
        return patientStore.getAll();
    }
}

public class DoctorService {
    private final DataStore<Doctor> doctorStore = new DataStore<>();
    
    public void addDoctor(Doctor doctor) {
        doctorStore.add(String.valueOf(doctor.getDoc_id()), doctor);
    }
    
    public List<Doctor> getAllDoctors() {
        return doctorStore.getAll();
    }
}
```

#### Benefits and Outcomes

1. **Loose Coupling**
   - High-level modules are not tied to low-level implementations
   - Can swap implementations without changing high-level code
   - Improves flexibility

2. **Easier Testing**
   - Can mock dependencies for testing
   - Can inject test doubles
   - Simplifies unit testing

3. **Flexibility**
   - Can change implementations without affecting clients
   - Can add new implementations easily
   - Supports configuration-driven behavior

4. **Maintainability**
   - Changes to low-level modules don't ripple to high-level modules
   - Clear separation of concerns
   - Easier to understand system architecture

5. **Reusability**
   - High-level modules can be reused with different implementations
   - Abstractions can be used in different contexts
   - Promotes code reuse

#### DIP Violations Avoided
- **Avoided**: BillingService directly creating StandardBillingStrategy instances
- **Avoided**: AppointmentService directly depending on ConsoleReminderObserver
- **Avoided**: Services directly depending on HashMap instead of DataStore abstraction
- **Avoided**: High-level business logic depending on low-level persistence details

---

## Other Key Principles

### 6. DRY (Don't Repeat Yourself)

#### Definition
Every piece of knowledge must have a single, unambiguous, authoritative representation within a system.

#### Implementation in MediTrack

##### Reusable Utility Classes
Common functionality is extracted into utility classes to avoid repetition.

**Validator.java**
```java
public class Validator {
    // Reusable validation methods
    public static boolean validatePatient(Patient patient) { /* ... */ }
    public static boolean validateDoctor(Doctor doctor) { /* ... */ }
    public static void validateAppointment(Date date, AppointmentType type, 
                                           double fees, Doctor doctor, Patient patient) { /* ... */ }
    public static boolean isValidName(String name) { /* ... */ }
    public static boolean isValidId(int id) { /* ... */ }
}
```

**Usage Across Services**
```java
// PatientService uses Validator
public Patient registerPatient(Patient patient) throws InvalidDataException {
    if (!Validator.validatePatient(patient)) {
        throw new InvalidDataException("Invalid patient details");
    }
    // ...
}

// AppointmentService uses Validator
public Appointment bookAppointment(Date date, AppointmentType type, 
                                   Doctor doctor, Patient patient) {
    Validator.validateAppointment(date, type, defaultFeeFor(type), doctor, patient);
    // ...
}
```

**CSVUtil.java**
```java
public class CSVUtil {
    // Reusable CSV operations
    public static List<String[]> readCSV(String filePath) { /* ... */ }
    public static void writeCSV(String filePath, List<String[]> data) { /* ... */ }
    public static String toCSVString(List<String[]> data) { /* ... */ }
    public static List<String[]> fromCSVString(String csvString) { /* ... */ }
}
```

**Usage Across Different Entities**
```java
// AppointmentCSVUtil uses CSVUtil
public static void save(String path, List<Appointment> appointments) {
    List<String[]> lines = new ArrayList<>();
    for (Appointment appointment : appointments) {
        lines.add(CSVUtil.splitRow(toRow(appointment)));
    }
    CSVUtil.writeLines(path, lines);
}

// Could be reused for PatientCSVUtil, DoctorCSVUtil, etc.
```

**DateUtil.java**
```java
public class DateUtil {
    // Reusable date operations
    public static String formatDate(Date date) { /* ... */ }
    public static String formatDateTime(Date date) { /* ... */ }
    public static Date parseDateTime(String dateStr) { /* ... */ }
    public static boolean isFuture(Date date) { /* ... */ }
    public static boolean isSameSlot(Date d1, Date d2) { /* ... */ }
}
```

**Usage Across Services**
```java
// AppointmentService uses DateUtil
public Appointment rescheduleAppointment(int appointmentId, Date newDate) {
    if (!DateUtil.isFuture(newDate)) {
        throw new IllegalArgumentException("Date must be in future");
    }
    // ...
}

// AppointmentCSVUtil uses DateUtil
public static String toRow(Appointment appointment) {
    String[] row = new String[]{
        String.valueOf(appointment.getAppointment_id()),
        DateUtil.formatDateTime(appointment.getAppointment_date()),
        // ...
    };
}
```

##### Generic DataStore
Generic DataStore class avoids repetition of storage logic for different entity types.

**DataStore.java**
```java
public class DataStore<T> {
    private final Map<Integer, T> store = new HashMap<>();
    private final Map<String, T> store2 = new ConcurrentHashMap<>();
    private final List<T> listStore = new ArrayList<>();
    
    // Generic methods work for any type
    public void add(String key, T value) { /* ... */ }
    public void put(int id, T entity) { /* ... */ }
    public T get(String key) { /* ... */ }
    public Optional<T> get(int id) { /* ... */ }
    public List<T> getAll() { /* ... */ }
    public List<T> filter(Predicate<T> predicate) { /* ... */ }
}
```

**Usage Across Services**
```java
// PatientService uses DataStore<Patient>
public class PatientService {
    private final DataStore<Patient> patientStore = new DataStore<>();
    // No need to implement storage logic
}

// DoctorService uses DataStore<Doctor>
public class DoctorService {
    private final DataStore<Doctor> doctorStore = new DataStore<>();
    // No need to implement storage logic
}

// AppointmentService uses DataStore<Appointment>
public class AppointmentService {
    private final DataStore<Appointment> appointments = new DataStore<>();
    // No need to implement storage logic
}
```

##### Builder Pattern
Builder pattern avoids repetition of telescoping constructors.

**DoctorBuilder.java**
```java
public class DoctorBuilder extends PersonBuilder {
    private Specialization specialization;
    private double fees;
    
    public DoctorBuilder specialization(Specialization specialization) {
        this.specialization = specialization;
        return this;
    }
    
    public DoctorBuilder fees(double fees) {
        this.fees = fees;
        return this;
    }
    
    @Override
    public Doctor build() {
        Doctor doctor = new Doctor(age, f_name, l_name, gender);
        doctor.specialization = specialization;
        doctor.fees = fees;
        return doctor;
    }
}
```

**Avoided Repetition**
```java
// WITHOUT Builder (repetitive constructors)
public Doctor(int age, String f_name, String l_name, Gender gender) {
    super(age, f_name, l_name, gender);
}
public Doctor(int age, String f_name, String l_name, Gender gender, 
              Specialization specialization) {
    super(age, f_name, l_name, gender);
    this.specialization = specialization;
}
public Doctor(int age, String f_name, String l_name, Gender gender, 
              Specialization specialization, double fees) {
    super(age, f_name, l_name, gender);
    this.specialization = specialization;
    this.fees = fees;
}
// ... more constructors for different combinations

// WITH Builder (no repetition)
Doctor doctor = Doctor.builder()
        .f_name("Arjun")
        .l_name("Mehta")
        .age(45)
        .gender(Gender.MALE)
        .specialization(Specialization.CARDIOLOGY)
        .fees(500.0)
        .build();
```

#### Benefits and Outcomes

1. **Reduced Code Duplication**
   - Common functionality is in one place
   - Changes only need to be made once
   - Reduces maintenance burden

2. **Consistency**
   - Same logic is used everywhere
   - Reduces bugs from inconsistent implementations
   - Ensures uniform behavior

3. **Easier Maintenance**
   - Changes are localized
   - Less code to maintain
   - Easier to find and fix bugs

4. **Improved Readability**
   - Code is more concise
   - Intent is clearer
   - Less boilerplate

5. **Better Testability**
   - Utility methods can be tested once
   - Tests are more focused
   - Reduces test code duplication

---

### 7. KISS (Keep It Simple, Stupid)

#### Definition
Systems should be designed to be as simple as possible. Complexity should be avoided unless necessary.

#### Implementation in MediTrack

##### Simple Entity Structure
Entities are straightforward without unnecessary complexity.

**Person.java**
```java
public abstract class Person implements Cloneable {
    private int p_id;
    private int age;
    private String F_name;
    private String L_name;
    private Gender gender;
    
    // Simple getters and setters
    public int getP_id() { return p_id; }
    public void setP_id(int p_id) { this.p_id = p_id; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    // ... other getters/setters
}
```

##### Straightforward Service Methods
Service methods are simple and focused.

**PatientService.java**
```java
public class PatientService {
    private final DataStore<Patient> patientStore = new DataStore<>();
    
    // Simple, clear methods
    public Patient registerPatient(Patient patient) throws InvalidDataException {
        if (!Validator.validatePatient(patient)) {
            throw new InvalidDataException("Invalid patient details");
        }
        patientStore.put(patient.getPat_id(), patient);
        return patient;
    }
    
    public List<Patient> getAllPatients() {
        return patientStore.getAll();
    }
    
    public Optional<Patient> searchPatient(int id) {
        return patientStore.get(id);
    }
}
```

##### Simple Console UI
UI is straightforward without complex frameworks.

**Main.java**
```java
public class Main {
    public static void main(String[] args) {
        seedInitialDoctors();
        
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> new PatientMenu().run();
                case "2" -> new DoctorMenu().run();
                case "3" -> new AppointmentMenu().run();
                case "4" -> new BillingMenu().run();
                case "0" -> running = false;
                default -> System.out.println("Invalid selection");
            }
        }
    }
}
```

##### Simple Validation
Validation logic is clear and straightforward.

**Validator.java**
```java
public static boolean validatePatient(Patient patient) {
    if (patient == null) {
        return false;
    }
    if (patient.getF_name() == null || patient.getF_name().trim().isEmpty()) {
        return false;
    }
    if (patient.getL_name() == null || patient.getL_name().trim().isEmpty()) {
        return false;
    }
    if (patient.getAge() <= 0 || patient.getAge() > 130) {
        return false;
    }
    return patient.getGender() != null;
}
```

#### Benefits and Outcomes

1. **Easier to Understand**
   - Code is straightforward
   - New developers can quickly understand
   - Reduces learning curve

2. **Easier to Maintain**
   - Simple code is easier to modify
   - Less chance of introducing bugs
   - Easier to debug

3. **Faster Development**
   - Less time spent on complex designs
   - Quicker to implement features
   - Faster iteration

4. **Fewer Bugs**
   - Simpler code has fewer edge cases
   - Easier to test
   - Less cognitive load

5. **Better Performance**
   - Simpler code often runs faster
   - Less overhead
   - More predictable behavior

---

### 8. Encapsulation

#### Definition
Encapsulation is the bundling of data and methods that operate on that data within a single unit (class), and restricting access to the internal representation.

#### Implementation in MediTrack

##### Private Fields with Getters/Setters
All entity fields are private with controlled access.

**Person.java**
```java
public abstract class Person implements Cloneable {
    private int p_id;           // Private
    private int age;            // Private
    private String F_name;      // Private
    private String L_name;      // Private
    private Gender gender;      // Private
    
    // Controlled access through getters
    public int getP_id() { return p_id; }
    public int getAge() { return age; }
    public String getF_name() { return F_name; }
    public String getL_name() { return L_name; }
    public Gender getGender() { return gender; }
    
    // Controlled modification through setters
    public void setP_id(int p_id) { this.p_id = p_id; }
    public void setAge(int age) { this.age = age; }
    public void setF_name(String f_name) { this.F_name = f_name; }
    public void setL_name(String l_name) { this.L_name = l_name; }
    public void setGender(Gender gender) { this.gender = gender; }
}
```

**Doctor.java**
```java
public class Doctor extends Person implements Searchable {
    private int doc_id;              // Private
    private Specialization specialization;  // Private
    private double fees;             // Private
    
    // Controlled access
    public int getDoc_id() { return doc_id; }
    public Specialization getSpecialization() { return specialization; }
    public double getFees() { return fees; }
    
    // Controlled modification
    public void setDoc_id(int doc_id) { this.doc_id = doc_id; }
    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }
    public void setFees(double fees) { this.fees = fees; }
}
```

##### Centralized Validation
Validation is encapsulated in Validator class.

**Validator.java**
```java
public class Validator {
    // Validation logic is encapsulated here
    public static boolean validatePatient(Patient patient) {
        if (patient == null) return false;
        if (patient.getF_name() == null || patient.getF_name().trim().isEmpty()) return false;
        if (patient.getL_name() == null || patient.getL_name().trim().isEmpty()) return false;
        if (patient.getAge() <= 0 || patient.getAge() > 130) return false;
        return patient.getGender() != null;
    }
    
    public static void validateAppointment(Date date, AppointmentType type, 
                                           double fees, Doctor doctor, Patient patient) {
        if (doctor == null) {
            throw new IllegalArgumentException("Appointment must have a doctor.");
        }
        if (patient == null) {
            throw new IllegalArgumentException("Appointment must have a patient.");
        }
        // ... other validations
    }
}
```

**Usage**
```java
// Services use Validator without knowing implementation details
public Patient registerPatient(Patient patient) throws InvalidDataException {
    if (!Validator.validatePatient(patient)) {
        throw new InvalidDataException("Invalid patient details");
    }
    // ...
}
```

##### Private Constructors for Singleton
Singleton pattern uses private constructor to prevent external instantiation.

**IdGenerators.java**
```java
public class IdGenerators {
    // Private constructor prevents external instantiation
    private IdGenerators() {
    }
    
    private static class Holder {
        private static final IdGenerators INSTANCE = new IdGenerators();
    }
    
    // Public access point
    public static IdGenerators getInstance() {
        return Holder.INSTANCE;
    }
}
```

##### Private Helper Methods
Service classes encapsulate helper methods.

**AppointmentService.java**
```java
public class AppointmentService implements Searchable {
    // Public API
    public Appointment bookAppointment(Date date, AppointmentType type, 
                                       Doctor doctor, Patient patient) {
        Validator.validateAppointment(date, type, defaultFeeFor(type), doctor, patient);
        if (isDoctorBusy(doctor, date)) {
            throw new IllegalArgumentException("Doctor already busy");
        }
        // ...
    }
    
    // Private helper methods (encapsulated)
    private boolean isDoctorBusy(Doctor doctor, Date date) {
        return isDoctorBusyExcluding(doctor, date, -1);
    }
    
    private boolean isDoctorBusyExcluding(Doctor doctor, Date date, int excludedId) {
        if (doctor == null || date == null) return false;
        return appointments.values().stream()
                .filter(a -> a.getAppointment_id() != excludedId)
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .filter(a -> a.getDoctor() != null && a.getDoctor().getP_id() == doctor.getP_id())
                .anyMatch(a -> DateUtil.isSameSlot(a.getAppointment_date(), date));
    }
    
    private boolean matchesName(String candidate, String needle) {
        return candidate != null && candidate.toLowerCase().contains(needle);
    }
}
```

#### Benefits and Outcomes

1. **Data Protection**
   - Internal state cannot be modified arbitrarily
   - Prevents invalid states
   - Maintains invariants

2. **Flexibility**
   - Internal implementation can change without affecting clients
   - Can add validation in setters
   - Can change data structures

3. **Controlled Access**
   - Getters/setters provide controlled access
   - Can add logging, validation, or caching
   - Can make fields read-only (no setter)

4. **Easier Maintenance**
   - Changes to internal structure don't affect clients
   - Can refactor internals safely
   - Reduces coupling

5. **Better Abstraction**
   - Clients interact with interface, not implementation
   - Hides complexity
   - Provides clear API

---

### 9. Immutability

#### Definition
An object is immutable if its state cannot be changed after it is created. Immutable objects are thread-safe and have no consistency issues.

#### Implementation in MediTrack

##### BillSummary as Immutable Class
BillSummary is designed to be immutable.

**BillSummary.java**
```java
public final class BillSummary extends Bill implements Cloneable {
    /**
     * The payment associated with this bill summary.
     * Assigned exactly once at construction and never modified afterwards,
     * which makes the summary immutable and thread-safe.
     */
    private final Payment payment;
    
    // Constructor assigns final field once
    public BillSummary(
            Date appointment_date,
            AppointmentStatus status,
            AppointmentType type,
            double appointmentFees,
            Doctor doctor,
            Patient patient,
            double doctor_fees,
            double totalAmount,
            Payment payment,
            BillingStrategey billingStrategey
    ) {
        super(appointment_date, status, type, appointmentFees, doctor, patient, 
              null, doctor_fees, totalAmount, billingStrategey);
        this.payment = payment;  // Assigned once
    }
    
    // No setter for payment - immutable
    public Payment getPayment() {
        return payment;
    }
    
    // processBillPayment returns new payment, doesn't modify state
    public Payment processBillPayment() {
        generateBill();
        Scanner scanner = new Scanner(System.in);
        // ... payment processing ...
        Payment processedPayment = new Payment(this.getTotalAmount(), selectedMethod);
        processedPayment.executePayment();
        return processedPayment;  // Returns new object, doesn't modify this
    }
    
    // Class is final - cannot be subclassed
}
```

##### Enums as Immutable
Enums are inherently immutable.

**AppointmentStatus.java**
```java
public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    RESCHEDULED,
    COMPLETED
}
```

**Specialization.java**
```java
public enum Specialization {
    MBBS,
    CARDIOLOGY,
    DERMATOLOGY,
    NEUROLOGY,
    ORTHOPEDICS,
    PEDIATRICS
}
```

##### Defensive Copying in Setters
Setters create defensive copies to prevent external modification.

**Appointment.java**
```java
public class Appointment implements Cloneable {
    private Date appointment_date;
    
    // Defensive copying in setter
    public void setAppointment_date(Date appointment_date) {
        this.appointment_date = (appointment_date == null) ? null : new Date(appointment_date.getTime());
    }
}
```

##### Immutable Collections
DataStore returns copies of collections to prevent external modification.

**DataStore.java**
```java
public class DataStore<T> {
    private final List<T> listStore = new ArrayList<>();
    
    // Returns a copy, not the internal list
    public List<T> getAll() {
        return new ArrayList<>(listStore);
    }
}
```

#### Benefits and Outcomes

1. **Thread Safety**
   - Immutable objects can be shared across threads without synchronization
   - No race conditions
   - Simplifies concurrent programming

2. **Simpler Code**
   - No need to worry about state changes
   - Easier to reason about
   - Fewer bugs

3. **Cacheable**
   - Immutable objects can be safely cached
   - Can be used as map keys
   - Hash codes don't change

4. **Easier Testing**
   - No need to test state transitions
   - Predictable behavior
   - Simpler test cases

5. **Security**
   - Cannot be modified after creation
   - Prevents tampering
   - Safer to share

---

### 10. Defensive Copying

#### Definition
Defensive copying is creating a copy of an object to prevent the original from being modified by external code.

#### Implementation in MediTrack

##### Date Defensive Copying
Date objects are defensively copied to prevent external modification.

**Appointment.java**
```java
public class Appointment implements Cloneable {
    private Date appointment_date;
    
    // Defensive copy in setter
    public void setAppointment_date(Date appointment_date) {
        this.appointment_date = (appointment_date == null) ? null : new Date(appointment_date.getTime());
    }
    
    // Defensive copy in clone
    @Override
    public Appointment clone() {
        try {
            Appointment copy = (Appointment) super.clone();
            if (this.appointment_date != null) {
                copy.appointment_date = new Date(this.appointment_date.getTime());
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Appointment must be cloneable", e);
        }
    }
}
```

**DateUtil.java**
```java
public class DateUtil {
    // Defensive copying when returning dates
    public static Date parseDateTime(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date date = sdf.parse(dateStr);
        return new Date(date.getTime());  // Return a copy
    }
}
```

##### Collection Defensive Copying
Collections are defensively copied to prevent external modification.

**DataStore.java**
```java
public class DataStore<T> {
    private final List<T> listStore = new ArrayList<>();
    
    // Returns a copy of the list
    public List<T> getAll() {
        return new ArrayList<>(listStore);
    }
    
    // Returns a filtered copy
    public List<T> filter(Predicate<T> predicate) {
        return listStore.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
```

**PatientService.java**
```java
public class PatientService {
    private final DataStore<Patient> patientStore = new DataStore<>();
    
    // Returns a copy of the list
    public List<Patient> getAllPatients() {
        return patientStore.getAll();  // DataStore already returns a copy
    }
}
```

##### Deep Copy in Clone
Clone methods perform deep copy of mutable fields.

**Patient.java**
```java
public class Patient extends Person implements Cloneable, Searchable {
    private String medical_history;
    
    // Deep copy implementation
    @Override
    public Patient clone() {
        Patient cloned = (Patient) super.clone();
        // Create new String to ensure deep copy
        cloned.medical_history = (this.medical_history == null) ? null : new String(this.medical_history);
        return cloned;
    }
}
```

#### Benefits and Outcomes

1. **Encapsulation**
   - Prevents external code from modifying internal state
   - Maintains invariants
   - Protects data integrity

2. **Thread Safety**
   - Copies can be safely shared
   - No race conditions on shared state
   - Simplifies concurrent programming

3. **Predictability**
   - Internal state cannot be changed unexpectedly
   - Behavior is more predictable
   - Easier to debug

4. **Security**
   - Prevents malicious modification
   - Protects sensitive data
   - Safer API

5. **Immutability Support**
   - Enables immutable objects
   - Supports functional programming
   - Reduces side effects

---

### 11. Composition over Inheritance

#### Definition
Favor composition (object composition) over inheritance (class inheritance) for code reuse. Composition provides more flexibility and better encapsulation.

#### Implementation in MediTrack

##### Service Composition
Services are composed of other services and utilities rather than extending them.

**AppointmentService Composed of Multiple Components**
```java
public class AppointmentService implements Searchable {
    // Composed of DataStore (not extending it)
    private final Map<Integer, Appointment> appointments = new LinkedHashMap<>();
    
    // Composed of Observers (not extending them)
    private final List<AppointmentObserver> observers = new ArrayList<>();
    
    // Uses Validator (not extending it)
    public Appointment bookAppointment(Date date, AppointmentType type, 
                                       Doctor doctor, Patient patient) {
        Validator.validateAppointment(date, type, defaultFeeFor(type), doctor, patient);
        // ...
    }
    
    // Uses DateUtil (not extending it)
    public Appointment rescheduleAppointment(int appointmentId, Date newDate) {
        if (!DateUtil.isFuture(newDate)) {
            throw new IllegalArgumentException("Date must be in future");
        }
        // ...
    }
}
```

##### Bill Composition
Bill is composed of Appointment data but doesn't extend it for all functionality.

**Bill.java**
```java
public class Bill extends Appointment implements Cloneable {
    // Bill extends Appointment for data reuse
    // But uses composition for billing strategy
    private BillingStrategey billingStrategey;
    
    // Delegates to strategy (composition)
    public double calculateSubTotal() {
        return billingStrategey.calculateSubTotal(this);
    }
    
    public double calculateTaxAmount() {
        return billingStrategey.calculateTaxAmount(this);
    }
    
    public double calculateTotalAmount() {
        return billingStrategey.calculateTotal(this);
    }
}
```

##### DataStore Composition
DataStore is composed of multiple storage structures.

**DataStore.java**
```java
public class DataStore<T> {
    // Composed of multiple storage structures
    private final Map<Integer, T> store = new HashMap<>();
    private final Map<String, T> store2 = new ConcurrentHashMap<>();
    private final List<T> listStore = new ArrayList<>();
    
    // Uses each structure for different purposes
    public void add(String key, T value) {
        store2.put(key, value);
        try {
            store.put(Integer.parseInt(key), value);
        } catch (NumberFormatException ignored) {
            // Non-numeric keys supported by string-backed store
        }
        upsertList(value);
    }
}
```

##### Observer Composition
Services are composed of observers rather than extending observer functionality.

**AppointmentService.java**
```java
public class AppointmentService implements Searchable {
    // Composed of observers (not extending observer base class)
    private final List<AppointmentObserver> observers = new ArrayList<>();
    
    public void register(AppointmentObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }
    
    public Appointment bookAppointment(Date date, AppointmentType type, 
                                       Doctor doctor, Patient patient) {
        // ... create appointment ...
        observers.forEach(observer -> observer.onBooked(appointment));
        return appointment;
    }
}
```

#### Benefits and Outcomes

1. **Flexibility**
   - Can change composed objects at runtime
   - Can swap implementations easily
   - More dynamic behavior

2. **Loose Coupling**
   - Classes are not tightly coupled through inheritance
   - Easier to modify independently
   - Better separation of concerns

3. **Avoids Class Explosion**
   - Don't need deep inheritance hierarchies
   - Fewer classes
   - Simpler design

4. **Better Encapsulation**
   - Composed objects can be private
   - Internal implementation hidden
   - Better API design

5. **Easier Testing**
   - Can mock composed objects
   - Can inject test doubles
   - Simpler unit tests

---

### 12. Separation of Concerns

#### Definition
Separation of concerns is the process of separating a computer program into distinct features that overlap in functionality as little as possible.

#### Implementation in MediTrack

##### Layered Architecture
Clear separation between different layers.

**Entity Layer** (Data Model)
- `entity/persons/` - Person, Doctor, Patient
- `entity/appointment/` - Appointment, AppointmentStatus, AppointmentType
- `entity/billing/` - Bill, BillSummary, Payment
- Responsibility: Represent data and business entities

**Service Layer** (Business Logic)
- `service/` - PatientService, DoctorService, AppointmentService, BillingService
- Responsibility: Implement business logic and rules

**Util Layer** (Utility Functions)
- `util/` - Validator, DateUtil, CSVUtil, DataStore, AIHelper
- Responsibility: Provide reusable utility functions

**UI Layer** (User Interface)
- `ui/` - PatientMenu, DoctorMenu, AppointmentMenu, BillingMenu
- Responsibility: Handle user interaction and display

**Exception Layer** (Error Handling)
- `exception/` - AppointmentNotFoundException, InvalidDataException, etc.
- Responsibility: Define and handle errors

**Interface Layer** (Contracts)
- `interfaces/` - Searchable, Payable
- Responsibility: Define contracts between components

##### Persistence Separation
Persistence logic is separated from business logic.

**AppointmentCSVUtil.java**
```java
public class AppointmentCSVUtil {
    // Only handles CSV persistence
    public static String toRow(Appointment appointment) { /* ... */ }
    public static void save(String path, List<Appointment> appointments) { /* ... */ }
    public static List<Appointment> load(String path) { /* ... */ }
}
```

**AppointmentService.java**
```java
public class AppointmentService implements Searchable {
    // Business logic only
    public Appointment bookAppointment(Date date, AppointmentType type, 
                                       Doctor doctor, Patient patient) {
        Validator.validateAppointment(date, type, defaultFeeFor(type), doctor, patient);
        // ... business logic ...
    }
    
    // Delegates persistence to CSVUtil
    public int saveToFile() {
        AppointmentCSVUtil.save(storagePath, listAll());
        return appointments.size();
    }
    
    public int loadFromFile() {
        List<Appointment> loaded = AppointmentCSVUtil.load(storagePath);
        appointments.clear();
        loaded.forEach(this::addExisting);
        return loaded.size();
    }
}
```

##### Validation Separation
Validation logic is separated from business logic.

**Validator.java**
```java
public class Validator {
    // Only validation logic
    public static boolean validatePatient(Patient patient) { /* ... */ }
    public static void validateAppointment(Date date, AppointmentType type, 
                                           double fees, Doctor doctor, Patient patient) { /* ... */ }
}
```

**PatientService.java**
```java
public class PatientService {
    // Business logic uses Validator
    public Patient registerPatient(Patient patient) throws InvalidDataException {
        if (!Validator.validatePatient(patient)) {
            throw new InvalidDataException("Invalid patient details");
        }
        // ... business logic ...
    }
}
```

##### UI Separation
UI logic is separated from business logic.

**PatientMenu.java**
```java
public class PatientMenu {
    private final PatientService service;
    
    // Only handles UI interaction
    public void run() {
        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1" -> registerPatient();
                case "2" -> searchPatient();
                // ... other UI operations ...
            }
        }
    }
    
    private void registerPatient() {
        // UI input handling
        System.out.print("Enter first name: ");
        String f_name = scanner.nextLine();
        // ... collect input ...
        
        // Delegate to service
        Patient patient = Patient.builder()
                .f_name(f_name)
                .l_name(l_name)
                .age(age)
                .gender(gender)
                .medical_history(medical_history)
                .build();
        
        try {
            service.registerPatient(patient);
            System.out.println("Patient registered successfully!");
        } catch (InvalidDataException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

#### Benefits and Outcomes

1. **Maintainability**
   - Changes in one layer don't affect others
   - Easier to locate and fix bugs
   - Clear responsibility boundaries

2. **Testability**
   - Each layer can be tested independently
   - Can mock dependencies
   - Simpler unit tests

3. **Reusability**
   - Components can be reused in different contexts
   - Utility classes used across layers
   - Services can be used by different UIs

4. **Parallel Development**
   - Different developers can work on different layers
   - Minimal merge conflicts
   - Clear ownership

5. **Scalability**
   - Can scale layers independently
   - Can replace implementations
   - Supports growth

---

### 13. Tell, Don't Ask

#### Definition
Objects should tell each other what to do, not ask each other about their state. This reduces coupling and improves encapsulation.

#### Implementation in MediTrack

##### Tell Objects to Perform Actions
Services tell entities what to do, rather than asking about their state.

**AppointmentService Tells Appointment to Change Status**
```java
public class AppointmentService implements Searchable {
    // GOOD: Tell the appointment to cancel itself
    public Appointment cancelAppointment(int appointmentId) {
        Appointment appointment = viewAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Appointment already completed");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);  // Tell it to change
        observers.forEach(observer -> observer.onCancelled(appointment));
        return appointment;
    }
    
    // BAD (avoided): Ask about state and make decisions externally
    /*
    public Appointment cancelAppointment(int appointmentId) {
        Appointment appointment = viewAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.PENDING || 
            appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            // External decision making
            appointment.setStatus(AppointmentStatus.CANCELLED);
        }
        return appointment;
    }
    */
}
```

##### Bill Tells Strategy to Calculate
Bill tells strategy to calculate, rather than asking for data and calculating itself.

**Bill.java**
```java
public class Bill extends Appointment implements Cloneable {
    private BillingStrategey billingStrategey;
    
    // GOOD: Tell strategy to calculate
    public double calculateSubTotal() {
        return billingStrategey.calculateSubTotal(this);
    }
    
    public double calculateTaxAmount() {
        return billingStrategey.calculateTaxAmount(this);
    }
    
    public double calculateTotalAmount() {
        return billingStrategey.calculateTotal(this);
    }
    
    // BAD (avoided): Ask for data and calculate externally
    /*
    public double calculateSubTotal() {
        double subtotal = this.getDoctor_fees() + this.getAppointmentFees();
        return subtotal;
    }
    */
}
```

##### Service Tells Validator to Validate
Service tells validator to validate, rather than asking about validity.

**PatientService.java**
```java
public class PatientService {
    // GOOD: Tell validator to validate
    public Patient registerPatient(Patient patient) throws InvalidDataException {
        if (!Validator.validatePatient(patient)) {
            throw new InvalidDataException("Invalid patient details");
        }
        patientStore.put(patient.getPat_id(), patient);
        return patient;
    }
    
    // BAD (avoided): Ask about validity and make decisions
    /*
    public Patient registerPatient(Patient patient) throws InvalidDataException {
        if (patient.getF_name() == null || patient.getF_name().isEmpty()) {
            throw new InvalidDataException("Invalid name");
        }
        if (patient.getAge() <= 0 || patient.getAge() > 130) {
            throw new InvalidDataException("Invalid age");
        }
        // ... more checks ...
        patientStore.put(patient.getPat_id(), patient);
        return patient;
    }
    */
}
```

##### DataStore Tells Predicate to Filter
DataStore tells predicate to evaluate, rather than checking conditions itself.

**DataStore.java**
```java
public class DataStore<T> {
    // GOOD: Tell predicate to evaluate
    public List<T> filter(Predicate<T> predicate) {
        return listStore.stream()
                .filter(predicate)  // Tell predicate to evaluate
                .collect(Collectors.toList());
    }
    
    // Usage
    /*
    List<Doctor> cardiologists = doctorStore.filter(
        doctor -> doctor.getSpecialization() == Specialization.CARDIOLOGY
    );
    */
}
```

#### Benefits and Outcomes

1. **Better Encapsulation**
   - Objects manage their own state
   - External code doesn't need to know internal details
   - Reduces coupling

2. **More Flexible**
   - Objects can change internal implementation
   - External code doesn't break
   - Easier to refactor

3. **Clearer Intent**
   - Code expresses what should happen
   - More readable
   - Self-documenting

4. **Easier Maintenance**
   - Logic is where it belongs
   - Changes are localized
   - Less code duplication

5. **Better Object-Oriented Design**
   - Objects behave like objects
   - Not just data containers
   - Rich behavior

---

## Summary of Design Principles

### Overall Architecture Benefits

1. **Maintainability**
   - Each principle addresses specific maintainability concerns
   - Changes are localized and predictable
   - System is easy to understand and modify

2. **Extensibility**
   - System can grow with new requirements
   - New features can be added without breaking existing code
   - Supports long-term evolution

3. **Testability**
   - Principles support dependency injection and mocking
   - Each component can be tested independently
   - Clear interfaces simplify test setup

4. **Code Quality**
   - Principles guide good design decisions
   - Reduces technical debt
   - Promotes best practices

5. **Team Collaboration**
   - Clear structure helps team coordination
   - Parallel development is easier
   - Code reviews are more effective

### Principle Interactions

The principles work together to create a cohesive architecture:

- **SOLID + DRY**: SOLID principles ensure structure, DRY reduces duplication
- **Encapsulation + Immutability**: Encapsulation protects state, immutability prevents changes
- **Composition over Inheritance + DIP**: Composition provides flexibility, DIP ensures loose coupling
- **Separation of Concerns + SRP**: Separation of concerns at macro level, SRP at micro level
- **Tell, Don't Ask + Encapsulation**: Objects manage their own state, external code tells them what to do

### Learning Outcomes

The application of these principles demonstrates:
- Understanding of when and how to apply each principle
- Ability to balance competing principles
- Knowledge of principle trade-offs and alternatives
- Practical application in a real-world scenario
- Adherence to industry best practices

---

*This documentation provides a comprehensive overview of design principles used in MediTrack. For information on design patterns, see [docs/design-patterns-used.md](docs/design-patterns-used.md).*
