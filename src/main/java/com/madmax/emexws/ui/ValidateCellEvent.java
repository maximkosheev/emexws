package com.madmax.emexws.ui;

import javafx.event.Event;
import javafx.event.EventType;

public class ValidateCellEvent<T> extends Event {

    private static final EventType<?> VALIDATE_ANY = new EventType<>(Event.ANY, "TABLE_COLUMN_VALIDATE");
    public static <T> EventType<ValidateCellEvent<T>> validateCellEvent() {
        return (EventType<ValidateCellEvent<T>>)VALIDATE_ANY;
    }

    private T cellData;

    public ValidateCellEvent(T cellData) {
        super(VALIDATE_ANY);
        this.cellData = cellData;
    }

    public T getCellData() {
        return cellData;
    }

    public void setCellData(T cellData) {
        this.cellData = cellData;
    }
}
