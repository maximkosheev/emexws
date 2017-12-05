package com.madmax.emexws.ui.controllers;

import com.madmax.emexws.Main;
import com.madmax.emexws.SpringFXMLLoader;
import com.madmax.emexws.config.AppConfig;
import com.madmax.emexws.exceptions.StickerCodeInputException;
import com.madmax.emexws.models.Invoice;
import com.madmax.emexws.models.Sticker;
import com.madmax.emexws.models.User;
import com.madmax.emexws.ui.control.RowNumberCellFactory;
import com.madmax.emexws.ui.control.StickerCodeColumn;
import com.madmax.emexws.ui.control.StickerCodeVerifier;
import com.madmax.emexws.ui.control.StickerTableView;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.axis2.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.emex.ws.EmExInmotionStub;
import ru.emex.ws.EmExInmotionStub.ArrayOfSetInmotionStateByGlobalIdInputItem;
import ru.emex.ws.EmExInmotionStub.ArrayOfSetInmotionStateByGlobalIdOutputItem;
import ru.emex.ws.EmExInmotionStub.SetInmotionStateByGlobalId;
import ru.emex.ws.EmExInmotionStub.SetInmotionStateByGlobalIdResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MainFrame extends AbstractController implements Initializable {
    @FXML Tab tabReceipt;
    @FXML Tab tabDisposal;
    @FXML TabPane tabPane;
    @FXML TextField edtReceiptCode;
    @FXML TextField edtDisposalCode;
    @FXML Button btnReceiptCode;
    @FXML Button btnDisposalCode;
    @FXML StickerTableView tblReceiptItems;
    @FXML StickerTableView tblDisposalItems;
    @FXML Button btnClearReceiptItems;
    @FXML Button btnClearDisposalItems;
    @FXML Button btnReceiptAll;
    @FXML Button btnDisposalAll;
    @FXML Label lblLogin;
    @FXML Label lblTotal;
    @FXML Label lblProcessed;
    @FXML Label lblInvoice;
    @FXML TableColumn<Sticker, Integer> columnReceiptNN;
    @FXML TableColumn<Sticker, String> columnReceiptStickerCode;
    @FXML TableColumn<Sticker, String> columnReceiptCustomer;
    @FXML TableColumn<Sticker, Integer> columnReceiptStickerCount;
    @FXML TableColumn<Sticker, Integer> columnReceiptStickerSubId;
    @FXML TableColumn<Sticker, Integer> columnReceiptStickerGlobalId;
    @FXML TableColumn<Sticker, Long> columnReceiptStickerPackageId;
    @FXML TableColumn<Sticker, String> columnReceiptStatusText;

    @FXML TableColumn<Sticker, Integer> columnDisposalNN;
    @FXML TableColumn<Sticker, String> columnDisposalCode;

    private TextField edtCode;
    private StickerTableView tblStickers;

    private final float[] receiptColumnWidthPercents = {0.04f, 0.13f, 0.24f, 0.06f, 0.1f, 0.1f, 0.04f, 0.29f};

    private class StickerItemsChangeListener implements ListChangeListener<Sticker> {

        @Override
        public void onChanged(Change<? extends Sticker> c) {
            updateTotalStickersCountStatusItem();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue == tabReceipt) onReceiptTabSelected();
                else if (newValue == tabDisposal) onDisposalTabSelected();
            }
        );
        columnReceiptNN.setCellFactory(new RowNumberCellFactory<>());
        columnReceiptStickerCode.setCellFactory(cell -> new StickerCodeColumn(new StickerCodeVerifier(tblReceiptItems.getItems())));
        columnReceiptStickerCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        columnReceiptStickerCode.setOnEditCommit((TableColumn.CellEditEvent<Sticker, String> event) -> {
            TablePosition<Sticker, String> pos = event.getTablePosition();
            ObservableList<Sticker> items = event.getTableView().getItems();
            Sticker sticker = items.get(pos.getRow());
            sticker.setCode(event.getNewValue());
            tblReceiptItems.refresh();
        });
        columnReceiptStickerCode.addEventHandler(StickerCodeColumn.verifyErrorEvent(), (event) -> {
            switch (event.getError()) {
                case FORMAT_ERROR:
                    showMessageBox("Неверный формат кода", Alert.AlertType.ERROR);
                    break;
                case UNIQUE_ERROR:
                    showMessageBox("Такой код уже введен", Alert.AlertType.ERROR);
                    break;
            }
        });
        columnReceiptCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        columnReceiptStickerCount.setCellValueFactory(new PropertyValueFactory<>("count"));
        columnReceiptStickerSubId.setCellValueFactory(new PropertyValueFactory<>("subId"));
        columnReceiptStickerGlobalId.setCellValueFactory(new PropertyValueFactory<>("globalId"));
        columnReceiptStickerPackageId.setCellValueFactory(new PropertyValueFactory<>("packageId"));
        columnReceiptStatusText.setCellValueFactory(new PropertyValueFactory<>("errorMessage"));

        columnDisposalNN.setCellFactory(new RowNumberCellFactory<>());
        columnDisposalCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        tblReceiptItems.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        StickerItemsChangeListener stickerItemsChangeListener = new StickerItemsChangeListener();
        tblReceiptItems.getItems().addListener(stickerItemsChangeListener);
        tblDisposalItems.getItems().addListener(stickerItemsChangeListener);
        Platform.runLater(() -> customResize(tblReceiptItems));
    }

    /**
     * Расчет ширины колонок
     * @param view
     */
    private void customResize(TableView<?> view) {

        AtomicLong width = new AtomicLong();
        view.getColumns().forEach(col -> {
            width.addAndGet((long) col.getWidth());
        });
        double tableWidth = view.getWidth();

        if (tableWidth > width.get()) {
            for (int nI = 0; nI < receiptColumnWidthPercents.length; nI++) {
                view.getColumns().get(nI).setPrefWidth(tableWidth * receiptColumnWidthPercents[nI]);
            }
        }
    }

    private void onReceiptTabSelected() {
        edtCode = edtReceiptCode;
        tblStickers = tblReceiptItems;
    }

    private void onDisposalTabSelected() {
        edtCode = edtDisposalCode;
        tblStickers = tblDisposalItems;
    }

    private void updateLoginStatusItem() {
        Long login = user.getLogin();
        if (user.getLogin() == 0)
            lblLogin.setText("Вход не выполнен");
        else
            lblLogin.setText(login.toString());
    }

    private void updateTotalStickersCountStatusItem() {
        lblTotal.setText(String.valueOf(tblStickers.getItems().size()));
    }

    private void updateProcessedStickersCountStatusItem() {
        Integer value = Integer.parseInt(lblProcessed.getText());
        updateProcessedStickersCountStatusItem(value + 1);
    }

    private void updateProcessedStickersCountStatusItem(int value) {
        lblProcessed.setText(String.valueOf(value));
    }

    private void updateInvoiceStatusItem() {
        lblInvoice.setText(String.valueOf(Invoice.getInstance().size()));
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        Scene scene = new Scene((Parent)getView(),1100, 768);
        String appCss = getClass().getResource("/application.css").toExternalForm();
        scene.getStylesheets().add(appCss);
        getStage().setScene(scene);
        getStage().setTitle(Main.APPLICATION_NAME);
        onReceiptTabSelected();
        updateLoginStatusItem();
        updateTotalStickersCountStatusItem();
        updateProcessedStickersCountStatusItem(0);
        updateInvoiceStatusItem();
    }

    /**
     * Отображает сообщение в виде диалога
     * @param message - сообщение, которое нужно отобразить
     * @param type - тип диалога (Information, Error, Warning etc)
     */
    private void showMessageBox(String message, Alert.AlertType type) {
        Alert dlg = new Alert(Alert.AlertType.INFORMATION);

        switch (type) {
            case ERROR: {
                dlg.setTitle("Ошибка");
                dlg.setHeaderText("В процессе работы возникла исключительная ситуация");
                break;
            }
            case INFORMATION: {
                dlg.setTitle("Информация");
                dlg.setHeaderText("Сообщение");
                break;
            }
        }
        dlg.setContentText(message);
        dlg.showAndWait();
    }

    /**
     * Проверяет валидность введеного кода
     * @param code - код стикера
     * @throws RuntimeException выбрасывается в случае, если проверка завершилась с ошибкой
     */
    private void validateStickerCode(String code) throws StickerCodeInputException {
        if (!Sticker.validateCodeFormat(code))
            throw new StickerCodeInputException("Неверный формат кода");
        if (tblStickers.getItems().contains(new Sticker(code)))
            throw new StickerCodeInputException("Такой код уже введен");
    }

    /**
     * Выполняет загрузку штрих кодов из файла csv
     * @param event
     */
    @FXML
    @SuppressWarnings("unused")
    void onActLoadCodesFromCSVFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть CSV файл");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV File", "*.csv")
        );
        File file = fileChooser.showOpenDialog(getStage());
        if (file == null) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String csvLine;

            while((csvLine = reader.readLine()) != null) {
                System.out.println(csvLine);
            }
        }
        catch (IOException e) {
            showMessageBox(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Autowired
    private Properties properties;

    /**
     * Выполняет загрузку файла счет-фактуры
     * @param event
     */
    @FXML
    @SuppressWarnings("unused")
    void onActLoadInvoice(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Открыть файл счет-фактуры");
        try {
            File lastInvoicePath = new File(properties.getProperty(AppConfig.LAST_INVOICE_PATH_PROPERTY_NAME));
            if (!lastInvoicePath.isDirectory())
                throw new SecurityException();
            fileChooser.setInitialDirectory(lastInvoicePath);
        }
        catch (SecurityException e) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home", "C:\\")));
        }
        finally {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel File", "*.xls")
            );
        }

        try {
            File file = fileChooser.showOpenDialog(getStage());
            // пользователь отказался от загрузки
            if (file == null) return;
            // загружаем счет-фактуры
            Invoice.getInstance().load(file);
            // сохраняем последнее место загрузки
            properties.setProperty(AppConfig.LAST_INVOICE_PATH_PROPERTY_NAME, file.getParent());
            showMessageBox("Счет-фактуры успешно загружен!", Alert.AlertType.INFORMATION);
            updateInvoiceStatusItem();
        }
        catch (IOException e) {
            showMessageBox(e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void pushSticker(String code) {
        try {
            validateStickerCode(code);

            Sticker sticker = new Sticker(code);
            tblStickers.getItems().add(sticker);
            edtCode.clear();
            edtCode.requestFocus();
            tblStickers.ensureLastRowVisible();
            Platform.runLater(() -> {
                tblStickers.refresh();
            });
        }
        catch (Exception e) {
            showMessageBox(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    void onReceiptCode(ActionEvent e) {
        pushSticker(edtCode.getText());
    }

    @FXML
    void onKeyPressed(KeyEvent event) {
        // если в данный момент данные в таблице не редактируются
        if (tblStickers.getEditingCell() == null) {
            // перемещаем курсор в поле ввода кода
            edtReceiptCode.requestFocus();
        }

        if (event.getCode() == KeyCode.ENTER) {
            pushSticker(edtCode.getText());
        }
    }

    @FXML
    @SuppressWarnings("unused")
    void onDisposalCode(ActionEvent e) {
    }

    @Autowired
    private User user;

    /**
     * Отправляет запрос к web-службе EmExInmotion emex.ru
     * @param items - массив элементов SetInmotionStateByGlobalIdInputItem
     * @return SetInmotionStateByGlobalIdResult - ответ сервера
     * @throws RemoteException - в случае если возникла исключительная ситуация
     */
    private ArrayOfSetInmotionStateByGlobalIdOutputItem setInmotionStateByGlobalId(ArrayOfSetInmotionStateByGlobalIdInputItem items) throws RemoteException {
        // формируем параметры вызова метода службы emex.ru
        SetInmotionStateByGlobalId request = new SetInmotionStateByGlobalId();
        request.setLogin(user.getLogin());
        request.setPassword(user.getPassword());
        request.setSetInmotionStateItems(items);

        // выполняем вызов метода службы emex.ru
        EmExInmotionStub stub = new EmExInmotionStub();
        // какой-то хак, но это позволяет избежать исключительной ситуации при парсинге ответа
        stub._getServiceClient().getOptions().setSoapVersionURI(Constants.URI_SOAP11_ENV);
        SetInmotionStateByGlobalIdResponse response = stub.setInmotionStateByGlobalId(request);
        return response.getSetInmotionStateByGlobalIdResult();
    }

    /**
     * Возвращает стикер по его subId
     * @param subId - SubId стикера
     * @return
     */
    private Sticker getStickerBySubId(long subId) {
        for (Sticker sticker : tblStickers.getItems()) {
            if (sticker.getSubId() == subId)
                return sticker;
        }
        return null;
    }

    /**
     * Возвращает стикер по его globalId
     * @param globalId - GlobalId стикера
     * @return
     */
    private Sticker getStickerByGlobalId(long globalId) {
        for (Sticker sticker : tblStickers.getItems()) {
            if (sticker.getGlobalId() == globalId)
                return sticker;
        }
        return null;
    }

    /**
     * Выполняет отправку запроса на изменение статуса "Товар принят"
     * по всем введеным стикерам
     * @param event
     */
    @FXML
    @SuppressWarnings("unused")
    void onReceiptAll(ActionEvent event) {
        final int itemsPerPackage = 10;

        // формируем список групп элементов для отправки
        List<ArrayOfSetInmotionStateByGlobalIdInputItem> listOfArrayOfInputItems = new ArrayList<>();
        int nItemsToSendCount = 0;

        // формируем группу элементов для отправки в рамках одного запроса
        ArrayOfSetInmotionStateByGlobalIdInputItem itemsToSend = new ArrayOfSetInmotionStateByGlobalIdInputItem();
        for (int nItemIndex = 0; nItemIndex < tblStickers.getItems().size(); nItemIndex++) {
            if (nItemsToSendCount < itemsPerPackage) {
                itemsToSend.addSetInmotionStateByGlobalIdInputItem(tblStickers.getItems().get(nItemIndex)
                        .getInputItem(EmExInmotionStub.SetInmotionStateEnum.RecReg));
                nItemsToSendCount += 1;
            }
            // добавили в группу максимальное кол-во стикеров
            else {
                listOfArrayOfInputItems.add(itemsToSend);
                itemsToSend = new ArrayOfSetInmotionStateByGlobalIdInputItem();
                nItemsToSendCount = 0;
            }
        }
        if (itemsToSend.getSetInmotionStateByGlobalIdInputItem().length > 0) {
            listOfArrayOfInputItems.add(itemsToSend);
        }

        // отправляем каждую группу списков на обработку веб-службе
        updateProcessedStickersCountStatusItem(0);
        listOfArrayOfInputItems.forEach(group -> {
            try {
                // оправка запроса на установку статусов и анализ результата
                EmExInmotionStub.SetInmotionStateByGlobalIdOutputItem[] response = setInmotionStateByGlobalId(group)
                        .getSetInmotionStateByGlobalIdOutputItem();

                // разбираем ответ сервера
                for (EmExInmotionStub.SetInmotionStateByGlobalIdOutputItem item : response) {
                    Sticker sticker = getStickerByGlobalId(item.getGlobalId());
                    if (sticker != null) {
                        sticker.setErrorCode(item.getErrorCode());
                        sticker.setErrorMessage(item.getErrorMessage());
                        updateProcessedStickersCountStatusItem();
                        System.out.println(item.getGlobalId() + " : " + item.getErrorCode() + ":" + item.getErrorMessage());
                    }
                }
            }
            catch (RemoteException e) {
                showMessageBox(e.getClass().getName() + ":" + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
        tblStickers.refresh();
    }

    @FXML
    @SuppressWarnings("unused")
    void onAuthenticate(ActionEvent event) {
        Stage stage = new Stage();
        LoginFrame loginFrame = (LoginFrame) SpringFXMLLoader.load("ui/frmLogin.fxml");
        loginFrame.setStage(stage);
        stage.showAndWait();
        updateLoginStatusItem();
    }

    @FXML
    @SuppressWarnings("unused")
    void onClear(ActionEvent event) {
        tblStickers.getItems().clear();
    }

    @FXML
    void onActClose(ActionEvent event) {
        getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    @SuppressWarnings("unused")
    void onActAbout(ActionEvent event) {
        Alert aboutDlg = new Alert(Alert.AlertType.INFORMATION);
        aboutDlg.setTitle("О программе");
        aboutDlg.setHeaderText(null);
        String content = Main.APPLICATION_NAME + "\n";
        content += "Версия: 1.0.0\n";
        content += "Разработано по заказу представительства EMEX.RU г.Озерске\n";
        content += "Разработчик - Кощеев Максим (maximkosheev@gmail.com)";
        aboutDlg.setContentText(content);
        aboutDlg.showAndWait();
    }
}
