package com.madmax.emexws.ui.control;

import com.madmax.emexws.enums.VerifyErrorEnum;
import com.madmax.emexws.models.Sticker;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import lombok.NonNull;

public class StickerCodeVerifier implements Callback<String, VerifyErrorEnum> {
    private ObservableList<Sticker> items;

    public StickerCodeVerifier(@NonNull ObservableList<Sticker> items) {
        this.items = items;
    }

    @Override
    public VerifyErrorEnum call(String code) {
        if (!Sticker.validateCodeFormat(code))
            return VerifyErrorEnum.FORMAT_ERROR;
        if (items.contains(new Sticker(code)))
            return VerifyErrorEnum.UNIQUE_ERROR;
        return VerifyErrorEnum.NON_ERROR;
    }
}
