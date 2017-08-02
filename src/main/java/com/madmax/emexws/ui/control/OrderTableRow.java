package com.madmax.emexws.ui.control;

import com.madmax.emexws.models.Order;
import javafx.scene.control.TableRow;

public class OrderTableRow extends TableRow<Order> {

    public OrderTableRow() {
        super();
    }

    @Override
    protected void updateItem(Order item, boolean empty) {
        if (!empty) {
            switch (item.getStatus()) {
                case STATUS_DEFAULT:
                    break;
                case STATUS_SUCCESS:
                    setStyle("-fx-background-color: green");
                case STATUS_FAILED:
                    setStyle("-fx-background-color: red");
            }
        }
    }
}
