package com.madmax.emexws.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import ru.emex.ws.EmExInmotionStub.SetInmotionStateByGlobalIdInputItem;
import ru.emex.ws.EmExInmotionStub.SetInmotionStateEnum;

@NoArgsConstructor
public class Sticker {
    // стикер считан корректно, но еще не обрабатывался сервером (формально не является ошибкой)
    public static final int PROCESS_PENDING = 1;
    // Ошибки обраборки стикера локального характера (до обработки сервером)
    public static final int LOCAL_ERROR = 100;
    // не верный формат штрих кода
    public static final int CODE_FORMAT_ERROR = LOCAL_ERROR + 1;
    // не найден globalId
    public static final int GLOBALID_NOTFOUND_ERROR = LOCAL_ERROR + 2;

    /** Поле "количество деталей в упаковке", указанное на стикере */
    @Getter private int count;
    /** Поле "subId", указанное на стикере */
    @Getter private long subId;
    /** поле globalId, полученное из файла INVOCE */
    @Getter private long globalId;
    /** штрих код, считанный со стикера.
     * Иммет слелующий формат *CCCCSSSSSS/III, где
     * CCCC - поле количество деталей в упаковке. Поле шириной 4 символа
     * SSSSSS - поле subId. Поле произвольной ширины
     * III - идентификатор упоковки. Поле произвольной ширины
     **/
    /** идентификатор упаковки */
    @Getter private long packageId;
    @Getter private String code;
    @Getter private String customer;
    /** Результат регистрации данного стикера в системе emex.ru */
    @Getter @Setter private int errorCode;
    /** Сообщение об ошибке */
    @Getter @Setter private String errorMessage;

    public Sticker(String code) {
        setCode(code);
    }

    private static String normalizeCode(String code) {
        return code.replace('.', '/');
    }

    public void setCode(@NonNull String code) {
        try {
            this.code = normalizeCode(code);
            errorCode = PROCESS_PENDING;
            count = Integer.parseInt(this.code.substring(1, 5));
            subId = Long.parseLong(this.code.substring(5, this.code.indexOf("/")));
            globalId = Invoice.getInstance().getGlobalIdBySubId(this.subId);
            customer = Invoice.getInstance().getCustomerBySubId(this.subId);
            if (globalId == 0) {
                errorCode = GLOBALID_NOTFOUND_ERROR;
                errorMessage = "Счет-фактура не загружена или товар не найден";
            }
            packageId = Long.parseLong(this.code.substring(this.code.indexOf("/") + 1));
        }
        catch (NumberFormatException e) {
            count = 0;
            subId = 0L;
            globalId = 0L;
            customer = "";
            packageId = 0L;
            errorCode = CODE_FORMAT_ERROR;
            errorMessage = "Не верный формат штрих кода";
        }
    }

    public static boolean validateCodeFormat(String code) {
        return normalizeCode(code).matches("^\\*\\d{5,12}/\\d+$");
    }

    /**
     * Возвращает структуру данных в соответствии с описанием веб-службы EmExInmotion
     * @return
     */
    public SetInmotionStateByGlobalIdInputItem getInputItem(SetInmotionStateEnum state) {
        SetInmotionStateByGlobalIdInputItem item = new SetInmotionStateByGlobalIdInputItem();
        item.setGlobalId(globalId);
        item.setCount(count);
        item.setState(state);
        return item;
    }

    @Override
    public boolean equals(Object order) {
        if (order != null)
            return code.equals(((Sticker)order).code);
        return false;
    }

    @Override
    public String toString() {
        return "Code: " + code;
    }
}
