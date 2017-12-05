package com.madmax.emexws.ui.control;

import com.madmax.emexws.enums.VerifyErrorEnum;
import com.madmax.emexws.models.Sticker;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import lombok.Getter;
import lombok.NonNull;

public class StickerCodeColumn extends TextFieldTableCell<Sticker, String> {
    private StickerCodeVerifier verifier;

    private static final EventType<?> VERIFY_ANY_EVENT = new EventType<>(Event.ANY, "VERIFY_EVENT");

    public static EventType<VerifyEvent> verifyAnyEvent() {
        return (EventType<VerifyEvent>) VERIFY_ANY_EVENT;
    }

    private static final EventType<?> VERIFY_ERROR_EVENT = new EventType<>(verifyAnyEvent(), "VERIFY_ERROR_EVENT");

    public static EventType<VerifyEvent> verifyErrorEvent() {
        return (EventType<VerifyEvent>) VERIFY_ERROR_EVENT;
    }

    public static class VerifyEvent extends Event {

        public static final EventType<?> ANY = VERIFY_ANY_EVENT;

        @Getter
        private VerifyErrorEnum error;

        public VerifyEvent(EventType<? extends Event> eventType, VerifyErrorEnum error) {
            super(eventType);
            this.error = error;
        }

        public VerifyEvent(Object source, EventTarget target, EventType<? extends Event> eventType, VerifyErrorEnum error) {
            super(source, target, eventType);
            this.error = error;
        }
    }

    public StickerCodeColumn(@NonNull StickerCodeVerifier verifier) {
        super(new DefaultStringConverter());
        this.verifier = verifier;
    }

    @Override
    public void commitEdit(String newValue) {
        if (!isEditing()) return;

        VerifyErrorEnum error = verifier.call(newValue);

        if (error == VerifyErrorEnum.NON_ERROR) {
            super.commitEdit(newValue);
        }
        else {
            VerifyEvent event = new VerifyEvent(verifyErrorEvent(), error);
            Event.fireEvent(getTableColumn(), event);
        }
    }
}
