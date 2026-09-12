# Design Patterns Used in MediTrack

This document provides an in-depth analysis of the design patterns implemented in the MediTrack Clinic Appointment Management System, explaining how and where each pattern is used and the benefits it provides.

---

## Table of Contents

1. [Singleton Pattern](#1-singleton-pattern)
2. [Factory Pattern](#2-factory-pattern)
3. [Strategy Pattern](#3-strategy-pattern)
4. [Observer Pattern](#4-observer-pattern)
5. [Builder Pattern](#5-builder-pattern)
6. [Template Method Pattern](#6-template-method-pattern)

---

## 1. Singleton Pattern

### Definition
The Singleton Pattern ensures that a class has only one instance and provides a global point of access to that instance.

### Implementation in MediTrack

#### Location
`src/main/java/com/airtribe/meditrack/entity/idgenerators/IdGenerators.java`

#### Code Structure
```java
public class IdGenerators {
    private IdGenerators() {
        // Private constructor prevents external instantiation
    }
    
    private static class Holder {
        private static final IdGenerators INSTANCE = new IdGenerators();
    }
    
    public static IdGenerators getInstance() {
        return Holder.INSTANCE;
    }
    
    // ID generation methods
    public int PersonIDGenerator() { /* ... */ }
    public int PatientIDGenerator() { /* ... */ }
    public int DocIdGenerator() { /* ... */ }
    public int NewAppointmentIdGenerator() { /* ... */ }
    public int BillIdGenerator() { /* ... */ }
    public int generatePaymentID() { /* ... */ }
    public int generateTransactionID() { /* ... */ }
}
```

#### Implementation Details
- **Initialization-on-demand holder idiom**: Uses a static inner class to hold the singleton instance
- **Thread-safe**: The JVM guarantees thread-safe initialization of static classes
- **Lazy initialization**: The instance is created only when `getInstance()` is first called
- **Private constructor**: Prevents external instantiation via `new`

#### Usage in MediTrack
```java
// In Person.java
public Person(int age, String f_name, String l_name, Gender gender) {
    this.p_id = IdGenerators.getInstance().PersonIDGenerator();
    // ...
}

// In Doctor.java
public Doctor(int age, String f_name, String l_name, Gender gender) {
    super(age, f_name, l_name, gender);
    this.doc_id = IdGenerators.getInstance().DocIdGenerator();
    // ...
}

// In Appointment.java
public Appointment(Date appointment_date, AppointmentStatus status, 
                  AppointmentType type, double appointmentFees,
                  Doctor doctor, Patient patient) {
    this.appointment_id = IdGenerators.getInstance().NewAppointmentIdGenerator();
    // ...
}
```

#### Benefits and Outcomes

1. **Consistent ID Generation**
   - All entities receive unique IDs from a single source
   - Prevents ID collisions across different parts of the application
   - Ensures sequential ID generation (1, 2, 3, ...)

2. **Memory Efficiency**
   - Only one instance exists regardless of how many times it's accessed
   - Reduces memory footprint compared to creating multiple instances

3. **Thread Safety**
   - The holder idiom provides thread-safe lazy initialization without synchronization overhead
   - Multiple threads can safely call `getInstance()` concurrently

4. **Centralized Control**
   - All ID generation logic is in one place
   - Easy to modify ID generation strategy (e.g., switch to UUIDs)
   - Simplifies testing and debugging of ID-related issues

5. **Global Access**
   - Any part of the application can access ID generation without passing references
   - Simplifies constructor calls in entity classes

#### Alternative Approaches Considered
- **Eager initialization**: Would create instance at class loading time (not lazy)
- **Synchronized method**: Would add performance overhead on every call
- **Double-checked locking**: Complex and error-prone in older Java versions

---

## 2. Factory Pattern

### Definition
The Factory Pattern defines an interface for creating objects but lets subclasses decide which class to instantiate. It delegates object creation to subclasses.

### Implementation in MediTrack

#### Locations
- `src/main/java/com/airtribe/meditrack/entity/factory/BillFactory.java` (Interface)
- `src/main/java/com/airtribe/meditrack/entity/factory/BillFactoryImpl.java` (Implementation)
- `src/main/java/com/airtribe/meditrack/entity/factory/DoctorFactory.java` (Interface)
- `src/main/java/com/airtribe/meditrack/entity/factory/DoctorFactoryImpl.java` (Implementation)

#### BillFactory Interface
```java
public interface BillFactory {
    Bill createBill(Appointment appointment, BillingStrategey billingStrategey);
    Bill createStandardBill(Appointment appointment);
    Bill createEmergencyBill(Appointment appointment);
}
```

#### BillFactoryImpl Implementation
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

    @Override
    public Bill createStandardBill(Appointment appointment) {
        return createBill(appointment, new StandardBillingStrategy());
    }

    @Override
    public Bill createEmergencyBill(Appointment appointment) {
        return createBill(appointment, new EmergencyBillingStrategy());
    }
}
```

#### Usage in MediTrack
```java
// In BillingService.java
public Bill generateBill(Appointment appointment, BillingStrategey strategy) {
    BillFactory factory = new BillFactoryImpl();
    Bill bill = factory.createBill(appointment, strategy);
    bill.generateBill();
    return bill;
}

public Bill generateStandardBill(Appointment appointment) {
    BillFactory factory = new BillFactoryImpl();
    return factory.createStandardBill(appointment);
}
```

#### Benefits and Outcomes

1. **Decoupled Object Creation**
   - Client code (BillingService) doesn't need to know how to construct Bill objects
   - Changes to Bill constructor don't affect client code
   - Reduces coupling between service layer and entity layer

2. **Centralized Construction Logic**
   - All Bill creation logic is in one place
   - Consistent initialization across the application
   - Easy to add validation or preprocessing before object creation

3. **Flexibility in Object Types**
   - Can easily add new bill types (e.g., InsuranceBill, DiscountBill)
   - Factory methods can select appropriate implementations based on parameters
   - Supports different billing strategies without changing client code

4. **Encapsulation of Complexity**
   - Complex construction logic (setting multiple fields, validation) is hidden
   - Client code gets simple, readable method calls
   - Reduces error-prone manual construction

5. **Testability**
   - Can mock factory for testing
   - Can inject different factory implementations
   - Simplifies unit testing of service classes

#### Extended Benefits with Strategy Pattern
The BillFactory works in conjunction with the Strategy Pattern:
- `createStandardBill()` uses StandardBillingStrategy
- `createEmergencyBill()` uses EmergencyBillingStrategy
- This combination allows for flexible billing logic without code duplication

---

## 3. Strategy Pattern

### Definition
The Strategy Pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable. It lets the algorithm vary independently from clients that use it.

### Implementation in MediTrack

#### Locations
- `src/main/java/com/airtribe/meditrack/strategey/billiing/BillingStrategey.java` (Interface)
- `src/main/java/com/airtribe/meditrack/strategey/billiing/impl/StandardBillingStrategy.java`
- `src/main/java/com/airtribe/meditrack/strategey/billiing/impl/EmergencyBillingStrategy.java`

#### BillingStrategey Interface
```java
public interface BillingStrategey {
    double calculateSubTotal(Bill bill);
    double calculateTaxAmount(Bill bill);
    double calculateTotal(Bill bill);
}
```

#### StandardBillingStrategy Implementation
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

#### EmergencyBillingStrategy Implementation
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

#### Usage in MediTrack
```java
// In Bill.java
public class Bill extends Appointment implements Cloneable {
    private BillingStrategey billingStrategey;
    
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

// In BillingService.java
public Bill generateBill(Appointment appointment, BillingStrategey strategy) {
    Bill bill = factory.createBill(appointment, strategy);
    bill.generateBill();
    return bill;
}
```

#### Benefits and Outcomes

1. **Open/Closed Principle**
   - Can add new billing strategies without modifying existing code
   - Example: Could add `InsuranceBillingStrategy`, `DiscountBillingStrategy`
   - Existing strategies remain unchanged

2. **Runtime Flexibility**
   - Strategy can be selected at runtime based on appointment type
   - Different appointments can use different billing calculations
   - Easy to switch strategies based on business rules

3. **Elimination of Conditional Logic**
   - No complex if-else or switch statements for billing logic
   - Each strategy is self-contained
   - Reduces code complexity and improves readability

4. **Testability**
   - Each strategy can be tested independently
   - Easy to mock strategies for testing
   - Clear separation of concerns

5. **Maintainability**
   - Billing logic is isolated in strategy classes
   - Changes to tax rates or calculations are localized specific strategies
   - Easy to understand and modify individual strategies

#### Extension Possibilities
The Strategy Pattern can be extended to support:
- **InsuranceBillingStrategy**: Handles insurance coverage calculations
- **DiscountBillingStrategy**: Applies promotional discounts
- **SeniorCitizenBillingStrategy**: Special rates for elderly patients
- **BulkAppointmentBillingStrategy**: Discounts for multiple appointments

---

## 4. Observer Pattern

### Definition
The Observer Pattern defines a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.

### Implementation in MediTrack

#### Locations
- `src/main/java/com/airtribe/meditrack/entity/appointment/AppointmentObserver.java` (Interface)
- `src/main/java/com/airtribe/meditrack/entity/appointment/ConsoleReminderObserver.java` (Implementation)
- `src/main/java/com/airtribe/meditrack/service/AppointmentService.java` (Subject)

#### AppointmentObserver Interface
```java
public interface AppointmentObserver {
    default void onBooked(Appointment appointment) {
    }

    default void onCancelled(Appointment appointment) {
    }

    default void onRescheduled(Appointment appointment) {
    }

    default void onCompleted(Appointment appointment) {
    }
}
```

#### ConsoleReminderObserver Implementation
```java
public class ConsoleReminderObserver implements AppointmentObserver {
    @Override
    public void onBooked(Appointment appointment) {
        System.out.println("[Console Reminder]: Appointment #" + 
            appointment.getAppointment_id() + " booked for " + 
            DateUtil.formatDate(appointment.getAppointment_date()));
    }

    @Override
    public void onCancelled(Appointment appointment) {
        System.out.println("[Console Reminder]: Appointment #" + 
            appointment.getAppointment_id() + " cancelled");
    }

    @Override
    public void onRescheduled(Appointment appointment) {
        System.out.println("[Console Reminder]: Appointment #" + 
            appointment.getAppointment_id() + " rescheduled to " + 
            DateUtil.formatDate(appointment.getAppointment_date()));
    }

    @Override
    public void onCompleted(Appointment appointment) {
        System.out.println("[Console Reminder]: Appointment #" + 
            appointment.getAppointment_id() + " completed");
    }
}
```

#### AppointmentService (Subject)
```java
public class AppointmentService implements Searchable {
    private final List<AppointmentObserver> observers = new ArrayList<>();
    
    public void register(AppointmentObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }
    
    public boolean unregister(AppointmentObserver observer) {
        return observers.remove(observer);
    }
    
    public int observerCount() {
        return observers.size();
    }
    
    public Appointment bookAppointment(Date date, AppointmentType type, 
                                       Doctor doctor, Patient patient) {
        // ... validation and creation ...
        Appointment appointment = new Appointment.AppointmentBuilder()
                .appointment_date(date)
                .status(AppointmentStatus.PENDING)
                .type(type)
                .appointmentFees(fees)
                .doctor(doctor)
                .patient(patient)
                .build();
        
        appointments.put(appointment.getAppointment_id(), appointment);
        observers.forEach(observer -> observer.onBooked(appointment));
        return appointment;
    }
    
    public Appointment cancelAppointment(int appointmentId) {
        Appointment appointment = viewAppointment(appointmentId);
        // ... validation ...
        appointment.setStatus(AppointmentStatus.CANCELLED);
        observers.forEach(observer -> observer.onCancelled(appointment));
        return appointment;
    }
}
```

#### Usage in MediTrack
```java
// In AppointmentMenu.java
public AppointmentMenu(AppointmentService service, Scanner scanner) {
    this.service = service;
    this.scanner = scanner;
    this.service.register(new ConsoleReminderObserver());
}

// In AppointmentTestRunner.java
service.register(new AppointmentObserver() {
    @Override public void onBooked(Appointment a) { counts[0]++; }
    @Override public void onCancelled(Appointment a) { counts[1]++; }
    @Override public void onRescheduled(Appointment a) { counts[2]++; }
    @Override public void onCompleted(Appointment a) { counts[3]++; }
});
```

#### Benefits and Outcomes

1. **Loose Coupling**
   - Subject (AppointmentService) doesn't need to know concrete observer types
   - Observers can be added/removed without modifying subject code
   - Easy to add new observer types (EmailObserver, SMSObserver, etc.)

2. **Dynamic Relationships**
   - Observers can be registered/unregistered at runtime
   - Different observers can be active in different contexts
   - Supports multiple observers for the same subject

3. **Broadcast Communication**
   - One event triggers updates to all interested observers
   - No need for subject to maintain individual observer references
   - Simplifies notification logic

4. **Extensibility**
   - Easy to add new notification channels without changing existing code
   - Example: Could add `EmailReminderObserver`, `SMSReminderObserver`
   - Each observer handles its own notification logic

5. **Testing Support**
   - Test observers can verify notification behavior
   - Can mock observers for testing service logic
   - Observer pattern used in test runner to count events

#### Extension Possibilities
The Observer Pattern can be extended to support:
- **EmailReminderObserver**: Send email notifications
- **SMSReminderObserver**: Send SMS reminders
- **DatabaseLoggerObserver**: Log appointment changes to database
- **AnalyticsObserver**: Track appointment metrics for analytics
- **NotificationServiceObserver**: Integrate with external notification services

---

## 5. Builder Pattern

### Definition
The Builder Pattern separates the construction of a complex object from its representation, allowing the same construction process to create different representations.

### Implementation in MediTrack

#### Locations
All entity classes implement the Builder Pattern:
- `src/main/java/com/airtribe/meditrack/entity/persons/Person.java` (PersonBuilder)
- `src/main/java/com/airtribe/meditrack/entity/persons/Doctor.java` (DoctorBuilder)
- `src/main/java/com/airtribe/meditrack/entity/persons/Patient.java` (PatientBuilder)
- `src/main/java/com/airtribe/meditrack/entity/appointment/Appointment.java` (AppointmentBuilder)
- `src/main/java/com/airtribe/meditrack/entity/billing/Bill.java` (BillBuilder)
- `src/main/java/com/airtribe/meditrack/entity/billing/BillSummary.java` (BillSummaryBuilder)

#### PersonBuilder (Abstract Base)
```java
public abstract class Person {
    public static abstract class PersonBuilder {
        protected int age;
        protected String f_name;
        protected String l_name;
        protected Gender gender;

        public PersonBuilder age(int age) {
            this.age = age;
            return this;
        }

        public PersonBuilder f_name(String f_name) {
            this.f_name = f_name;
            return this;
        }

        public PersonBuilder l_name(String l_name) {
            this.l_name = l_name;
            return this;
        }

        public PersonBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public abstract Person build();
    }
}
```

#### DoctorBuilder (Concrete Implementation)
```java
public class Doctor extends Person {
    public static class DoctorBuilder extends PersonBuilder {
        private Specialization specialization;
        private double fees;

        @Override
        public DoctorBuilder age(int age) {
            this.age = age;
            return this;
        }

        @Override
        public DoctorBuilder f_name(String f_name) {
            this.f_name = f_name;
            return this;
        }

        @Override
        public DoctorBuilder l_name(String l_name) {
            this.l_name = l_name;
            return this;
        }

        @Override
        public DoctorBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

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
    
    public static DoctorBuilder builder() {
        return new DoctorBuilder();
    }
}
```

#### Usage in MediTrack
```java
// Creating a Doctor with Builder
Doctor doctor = Doctor.builder()
        .f_name("Arjun")
        .l_name("Mehta")
        .age(45)
        .gender(Gender.MALE)
        .specialization(Specialization.CARDIOLOGY)
        .fees(500.0)
        .build();

// Creating a Patient with Builder
Patient patient = Patient.builder()
        .f_name("John")
        .l_name("Doe")
        .age(35)
        .gender(Gender.MALE)
        .medical_history("Hypertension")
        .build();

// Creating an Appointment with Builder
Appointment appointment = new Appointment.AppointmentBuilder()
        .appointment_date(new Date())
        .status(AppointmentStatus.PENDING)
        .type(AppointmentType.INITIAL)
        .appointmentFees(500.0)
        .doctor(doctor)
        .patient(patient)
        .build();
```

#### Benefits and Outcomes

1. **Readable Code**
   - Fluent interface makes object construction self-documenting
   - Parameter names are explicit in method calls
   - Easier to understand than positional constructor arguments

2. **Optional Parameters**
   - Can set only the fields that are needed
   - No need for multiple constructors with different parameter combinations
   - Eliminates telescoping constructor problem

3. **Immutable Objects**
   - Builder can construct immutable objects
   - Object state is set once during construction
   - Thread-safe after construction

4. **Validation in One Place**
   - Validation logic can be centralized in the `build()` method
   - Ensures object is in valid state before returning
   - Example: BillBuilder validates billing strategy is not null

5. **Complex Construction Logic**
   - Can perform complex calculations before object creation
   - Can set derived fields based on other fields
   - Example: DoctorBuilder sets specialization and fees after calling parent constructor

6. **Inheritance Support**
   - Abstract PersonBuilder provides common fluent setters
   - Concrete builders (DoctorBuilder, PatientBuilder) extend and add specific fields
   - Maintains type safety with covariant return types

#### Comparison with Alternatives

**Without Builder (Telescoping Constructors):**
```java
// Confusing - what do these parameters mean?
Doctor doctor = new Doctor("Arjun", "Mehta", 45, Gender.MALE, 
                          Specialization.CARDIOLOGY, 500.0);
```

**With Builder:**
```java
// Clear and self-documenting
Doctor doctor = Doctor.builder()
        .f_name("Arjun")
        .l_name("Mehta")
        .age(45)
        .gender(Gender.MALE)
        .specialization(Specialization.CARDIOLOGY)
        .fees(500.0)
        .build();
```

---

## 6. Template Method Pattern

### Definition
The Template Method Pattern defines the skeleton of an algorithm in a method, deferring some steps to subclasses. It lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure.

### Implementation in MediTrack

#### Locations
- `src/main/java/com/airtribe/meditrack/entity/appointment/AppointmentObserver.java` (Default methods)
- `src/main/java/com/airtribe/meditrack/interfaces/Searchable.java` (Default methods)
- `src/main/java/com/airtribe/meditrack/interfaces/Payable.java` (Default methods)

#### AppointmentObserver with Default Methods
```java
public interface AppointmentObserver {
    default void onBooked(Appointment appointment) {
        // Default implementation: do nothing
    }

    default void onCancelled(Appointment appointment) {
        // Default implementation: do nothing
    }

    default void onRescheduled(Appointment appointment) {
        // Default implementation: do nothing
    }

    default void onCompleted(Appointment appointment) {
        // Default implementation: do nothing
    }
}
```

#### Searchable with Default Methods
```java
public interface Searchable {
    boolean matches(String searchTerm);
    String getSearchKey();
    boolean matchesId(int id);
    boolean matchesName(String name);

    // Template method: default implementation
    default boolean exists(int id) {
        return matchesId(id);
    }

    // Template method: default implementation
    default int count() {
        return 1;
    }
}
```

#### Usage in MediTrack
```java
// Concrete observer can override only needed methods
public class ConsoleReminderObserver implements AppointmentObserver {
    @Override
    public void onBooked(Appointment appointment) {
        System.out.println("[Reminder]: Appointment booked");
    }

    // Other methods use default (do nothing)
}

// Service implements Searchable with custom count()
public class AppointmentService implements Searchable {
    @Override
    public int count() {
        return appointments.size(); // Override default
    }
    
    @Override
    public boolean matchesId(int id) {
        return appointments.containsKey(id);
    }
    
    // exists() uses default implementation
}
```

#### Benefits and Outcomes

1. **Code Reuse**
   - Default implementations provide common behavior
   - Subclasses only need to override what's different
   - Reduces code duplication

2. **Backward Compatibility**
   - Can add new methods to interfaces without breaking existing implementations
   - Default implementations provide sensible defaults
   - Existing classes continue to work

3. **Flexibility**
   - Implementations can choose which methods to override
   - Can provide partial implementations
   - Easy to create simple implementations

4. **Clear Contract**
   - Interface defines the contract
   - Default methods provide reference implementations
   - Subclasses understand expected behavior

5. **Evolution**
   - Can add functionality to interfaces over time
   - Example: Could add `onNoShow(Appointment)` to AppointmentObserver
   - Existing observers would still work (use default)

#### Template Method in Service Layer

The AppointmentService also demonstrates template method concepts:
```java
public Appointment bookAppointment(Date date, AppointmentType type, 
                                   Doctor doctor, Patient patient) {
    // Template method skeleton
    Validator.validateAppointment(date, type, defaultFeeFor(type), doctor, patient);
    if (isDoctorBusy(doctor, date)) {
        throw new IllegalArgumentException("Doctor already busy");
    }
    Appointment appointment = createAppointment(date,类型, fees, doctor, patient);
    notifyObservers(appointment);
    return appointment;
}
```

The skeleton is defined, but specific steps can be customized:
- Validation can be extended
- Appointment creation can use different strategies
- Notification can be customized

---

## Summary of Design Pattern Benefits

### Overall Architecture Benefits

1. **Maintainability**
   - Each pattern addresses a specific concern
   - Changes are localized to specific classes
   - Easy to understand and modify

2. **Extensibility**
   - New features can be added without modifying existing code
   - Open/Closed principle is consistently applied
   - System can evolve with new requirements

3. **Testability**
   - Patterns support dependency injection and mocking
   - Each component can be tested independently
   - Clear interfaces simplify test setup

4. **Code Reuse**
   - Common patterns are reused across entities
   - Utility classes provide reusable functionality
   - Reduces code duplication

5. **Readability**
   - Intent is clear from pattern usage
   - Self-documenting code structure
   - Easier for new developers to understand

### Pattern Interactions

The patterns work together to create a cohesive architecture:

- **Singleton + Factory**: Singleton provides ID generation for Factory-created objects
- **Factory + Strategy**: Factory selects appropriate Strategy for object creation
- **Observer + Template Method**: Observers use default methods for flexibility
- **Builder + Factory**: Builder pattern used within Factory for complex construction
- **Strategy + Template Method**: Strategy interfaces use default methods

### Learning Outcomes

The implementation of these patterns demonstrates:
- Understanding of when to apply each pattern
- Ability to combine patterns effectively
- Knowledge of pattern trade-offs and alternatives
- Practical application in a real-world scenario
- Adherence to SOLID principles through pattern usage

---

*This documentation provides a comprehensive overview of design patterns used in MediTrack. For information on design principles, see [docs/design-principles-used.md](docs/design-principles-used.md).*
