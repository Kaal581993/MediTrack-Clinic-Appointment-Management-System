package com.airtribe.meditrack.entity.idgenerators;

public class IdGenerators {
    private int p_id = 0;
    private int doc_id = 0;
    private int pat_id = 0;
    private int bill_id = 0;
    private int appointment_id = 0;
    private int payment_id = 0;
    private int transaction_id = 0;

    // Singleton: private constructor prevents external instantiation.
    private IdGenerators() {}

    /**
     * Singleton accessor. Uses the initialization-on-demand holder idiom,
     * which is thread-safe, lazy, and avoids synchronization overhead.
     * The public API (getInstance()) is unchanged so existing callers keep working.
     */
    private static class Holder {
        private static final IdGenerators INSTANCE = new IdGenerators();
    }

    public static IdGenerators getInstance() {
        return Holder.INSTANCE;
    }

    public int PersonIDGenerator() {
        return ++p_id;
    }

    public int PatientIDGenerator() {
        return ++pat_id;
    }

    public int DocIdGenerator() {
        return ++doc_id;
    }

    public int NewAppointmentIdGenerator() {
        return ++appointment_id;
    }

    public int BillIdGenerator() {
        return ++bill_id;
    }

    public int generatePaymentID() {
        return ++payment_id;
    }

    public int generateTransactionID() {
        return ++transaction_id;
    }
}
