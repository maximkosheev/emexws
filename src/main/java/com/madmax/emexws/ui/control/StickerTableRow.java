package com.madmax.emexws.ui.control;

import com.madmax.emexws.models.Sticker;
import javafx.scene.control.TableRow;

public class StickerTableRow extends TableRow<Sticker> {

    public StickerTableRow() {
        super();
    }

    @Override
    protected void updateItem(Sticker item, boolean empty) {
        if (!empty) {
            if (item.getErrorCode() < 0) {
                setStyle("-fx-background-color: red");
            }
            else if (item.getErrorCode() > Sticker.LOCAL_ERROR) {
                setStyle("-fx-background-color: orange");
            }
            else if (item.getErrorCode() == 0){
                setStyle("-fx-background-color: green");
            }
        }
    }
}
