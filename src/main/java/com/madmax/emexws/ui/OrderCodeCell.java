package com.madmax.emexws.ui;


import com.madmax.emexws.models.Order;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class OrderCodeCell extends TextFieldTableCell<Order, String> {

    public OrderCodeCell() {
        super(new DefaultStringConverter());
    }

    @Override
    public void commitEdit(String newValue) {
        if (!isEditing()) return;

        ValidateCellEvent<String> event= new ValidateCellEvent<>(newValue);
        Event.fireEvent(getTableColumn(), event);

        super.commitEdit(event.getCellData());
    }
}
