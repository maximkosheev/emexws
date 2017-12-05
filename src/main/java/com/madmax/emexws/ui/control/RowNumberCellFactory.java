package com.madmax.emexws.ui.control;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class RowNumberCellFactory<T, E> implements Callback<TableColumn<T, E>, TableCell<T, E>> {
    @Override
    public TableCell<T, E> call(TableColumn<T, E> param) {
        return new TableCell<T, E>() {
            @Override
            protected void updateItem(E item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
                else {
                    setText("");
                }
            }
        };
    }
}
