package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.entity.billing.BillSummary;
import com.airtribe.meditrack.entity.billing.Payment;
import com.airtribe.meditrack.entity.billing.PaymentMethods;
import com.airtribe.meditrack.entity.billing.PaymentStatus;
import com.airtribe.meditrack.entity.factory.BillFactory;
import com.airtribe.meditrack.entity.factory.BillFactoryImpl;
import com.airtribe.meditrack.exception.BillNotFoundException;
import com.airtribe.meditrack.strategey.billiing.BillingStrategey;
import com.airtribe.meditrack.util.DataStore;

import java.util.List;
import java.util.Optional;

public class BillingService {
    private final DataStore<Bill> billStore;
    private final DataStore<BillSummary> billSummaryStore;
    private final BillFactory billFactory;

    public BillingService() {
        this.billStore = new DataStore<>();
        this.billSummaryStore = new DataStore<>();
        this.billFactory = new BillFactoryImpl();
    }

    public Bill createBill(Appointment appointment, BillingStrategey billingStrategey) {
        Bill bill = new Bill.BillBuilder()
                .appointment_date(appointment.getAppointment_date())
                .status(appointment.getStatus())
                .type(appointment.getType())
                .appointmentFees(appointment.getAppointmentFees())
                .doctor(appointment.getDoctor())
                .patient(appointment.getPatient())
                .doctor_fees(0.0)
                .totalAmount(0.0)
                .billingStrategey(billingStrategey)
                .build();
        billStore.put(bill.getBill_id(), bill);
        return bill;
    }

    public Bill createStandardBill(Appointment appointment) {
        Bill bill = billFactory.createStandardBill(appointment);
        billStore.put(bill.getBill_id(), bill);
        return bill;
    }

    public Bill createEmergencyBill(Appointment appointment) {
        Bill bill = billFactory.createEmergencyBill(appointment);
        billStore.put(bill.getBill_id(), bill);
        return bill;
    }

    public Payment processPayment(int billId, PaymentMethods paymentMethod) {
        Optional<Bill> billOpt = billStore.get(billId);
        if (billOpt.isEmpty()) {
            throw new BillNotFoundException("Bill not found with ID: " + billId);
        }

        Bill bill = billOpt.get();
        Payment payment = new Payment(bill.calculateTotalAmount(), paymentMethod);
        payment.executePayment();

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            BillSummary summary = new BillSummary.BillSummaryBuilder()
                    .appointment_date(bill.getAppointment_date())
                    .status(bill.getStatus())
                    .type(bill.getType())
                    .appointmentFees(bill.getAppointmentFees())
                    .doctor(bill.getDoctor())
                    .patient(bill.getPatient())
                    .doctor_fees(bill.getDoctor_fees())
                    .totalAmount(bill.calculateTotalAmount())
                    .payment(payment)
                    .billingStrategey(bill.getBillingStrategey())
                    .build();
            billSummaryStore.put(billId, summary);
        }

        return payment;
    }

    public Optional<Bill> getBillById(int billId) {
        return billStore.get(billId);
    }

    public List<Bill> getAllBills() {
        return billStore.getAll();
    }

    public List<Bill> getBillsByPatient(int patientId) {
        return billStore.getAll().stream()
                .filter(bill -> bill.getPatient().getP_id() == patientId)
                .toList();
    }

    public List<Bill> getBillsByDoctor(int doctorId) {
        return billStore.getAll().stream()
                .filter(bill -> bill.getDoctor().getP_id() == doctorId)
                .toList();
    }

    public boolean cancelBill(int billId) {
        Optional<Bill> billOpt = billStore.get(billId);
        if (billOpt.isEmpty()) {
            return false;
        }

        Bill bill = billOpt.get();
        bill.setStatus(com.airtribe.meditrack.entity.appointment.AppointmentStatus.CANCELLED);
        return true;
    }

    public void generateBillReport() {
        List<Bill> bills = getAllBills();
        if (bills.isEmpty()) {
            System.out.println("No bills to generate report.");
            return;
        }

        System.out.println("\n=== BILLING REPORT ===");
        System.out.println("Total Bills: " + bills.size());
        
        double totalRevenue = bills.stream()
                .mapToDouble(Bill::getTotalAmount)
                .sum();
        
        System.out.println("Total Revenue: $" + totalRevenue);
        
        System.out.println("\n--- Bills by Doctor ---");
        bills.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        bill -> bill.getDoctor().getF_name() + " " + bill.getDoctor().getL_name()
                ))
                .forEach((doctorName, doctorBills) -> {
                    double doctorRevenue = doctorBills.stream()
                            .mapToDouble(Bill::getTotalAmount)
                            .sum();
                    System.out.println(doctorName + ": " + doctorBills.size() + " bills, Revenue: $" + doctorRevenue);
                });
        
        System.out.println("\n--- Bills by Status ---");
        bills.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        bill -> bill.getStatus().toString()
                ))
                .forEach((status, statusBills) -> {
                    System.out.println(status + ": " + statusBills.size());
                });
        
        System.out.println("========================\n");
    }

    public Optional<BillSummary> getBillSummary(int billId) {
        return billSummaryStore.get(billId);
    }

    public List<BillSummary> getAllBillSummaries() {
        return billSummaryStore.getAll();
    }
}
