package com.madmax.emexws.ui.control;

import com.madmax.emexws.models.Sticker;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class StickerTableView extends TableView<Sticker> {
    public StickerTableView() {
        super();
        setRowFactory(call -> new StickerTableRow());
        addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode() == KeyCode.ENTER && getEditingCell() == null)
                event.consume();
        });
    }

    public void ensureLastRowVisible() {
        try {
            VirtualFlow vf = (VirtualFlow)((TableViewSkin)getSkin()).getChildren().get(1);
            int rowsCount = getItems().size();
            int visibleRowsCount = vf.getLastVisibleCell().getIndex() - vf.getFirstVisibleCell().getIndex();
            IndexedCell cellToBeFirst = vf.getCell(rowsCount - visibleRowsCount);
            scrollTo(cellToBeFirst.getIndex());
        }
        catch (NullPointerException error) {
            //Do noting
        }
    }
}
