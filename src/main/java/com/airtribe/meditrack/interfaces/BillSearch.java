package com.airtribe.meditrack.interfaces;

import com.airtribe.meditrack.entity.billing.Bill;

import java.util.List;

public interface BillSearch {

    public void BillSearchByID(int bill_id);
    public List<Bill> BillSearchByPatientID(int patient_id);
}
