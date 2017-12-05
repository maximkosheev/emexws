package com.madmax.emexws.models;

import lombok.Getter;
import lombok.Setter;

public class InvoiceItem {
    @Getter private String reference;
    @Getter private long subId;
    @Getter @Setter long globalId;
    @Getter @Setter String customer;

    public void setReference(String reference) {
        this.reference = reference;
        subId = Long.parseLong(reference.split("=")[0]);
    }
}
