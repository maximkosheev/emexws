package com.madmax.emexws.models;

public class Order {
    public enum StatusEnum {STATUS_DEFAULT, STATUS_SUCCESS, STATUS_FAILED};

    private Integer number;
    private String code;
    private StatusEnum status;

    public Order() {
        number = 0;
        code = "";
    }

    public Order(Integer number, String code) {
        this.number = number;
        this.code = code;
        this.status = StatusEnum.STATUS_DEFAULT;
    }

    public Order(String code) {
        this.number = 0;
        this.code = code;
        this.status = StatusEnum.STATUS_DEFAULT;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public boolean isValid() {
        return !code.isEmpty();
    }

    @Override
    public boolean equals(Object order) {
        if (order != null)
            return code.equals(((Order)order).code);
        return false;
    }

    @Override
    public String toString() {
        return "Code: " + code;
    }
}
