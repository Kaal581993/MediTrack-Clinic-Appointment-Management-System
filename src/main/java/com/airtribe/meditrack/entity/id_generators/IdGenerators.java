package com.airtribe.meditrack.entity.id_generators;

public class IdGenerators {
 private static int p_id=0;
    private static int doc_id=0;
    private static int pat_id=0;
    private static int bill_id=0;
    private static int appointment_id=0;
    private static int payment_id=0;
    private static int transaction_id=0;

    public int PersonIDGenerator(){
        return ++p_id;
    }

    public int PatientIDGenerator(){
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
