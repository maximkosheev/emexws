package com.madmax.emexws.ui;

import com.madmax.emexws.Main;
import com.madmax.emexws.models.Order;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.axis2.AxisFault;
import ru.emex.ws.EmExServiceStub;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class MainFrame implements Initializable {
    @FXML
    TextField edtReceiptCode;
    @FXML
    TextField edtDisposalCode;
    @FXML
    Button btnReceiptCode;
    @FXML
    Button btnDisposalCode;
    @FXML
    TableView<Order> tblReceiptItems;
    @FXML
    TableView<Order> tblDisposalItems;
    @FXML
    Button btnClearReceiptItems;
    @FXML
    Button btnClearDisposalItems;
    @FXML
    Button btnReceiptAll;
    @FXML
    Button btnDisposalAll;
    @FXML
    private TableColumn<Order, Integer> columnReceiptNN;
    @FXML
    private TableColumn<Order, String> columnReceiptCode;
    @FXML
    private TableColumn<Order, Boolean> columnReceiptStatus;
    @FXML
    private TableColumn<Order, Integer> columnDisposalNN;
    @FXML
    private TableColumn<Order, String> columnDisposalCode;
    @FXML
    private TableColumn<Order, Boolean> columnDisposalStatus;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnReceiptNN.setCellValueFactory(new PropertyValueFactory<>("number"));
        columnReceiptCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        columnReceiptCode.setCellFactory(call -> new OrderCodeCell());
        columnReceiptCode.setOnEditCommit((TableColumn.CellEditEvent<Order, String> event) -> {
            TablePosition<Order, String> pos = event.getTablePosition();
            ObservableList<Order> items = event.getTableView().getItems();
            Order order = items.get(pos.getRow());
            order.setCode(event.getNewValue());
        });
        columnReceiptCode.addEventHandler(ValidateCellEvent.validateCellEvent(), (ValidateCellEvent<String> event) -> {
            if (!validateOrderCode(event.getCellData()))
                event.setCellData("#ERROR");
        });
        columnReceiptStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        columnDisposalNN.setCellValueFactory(new PropertyValueFactory<>("number"));
        columnDisposalCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        columnDisposalStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private boolean validateOrderCode(String code) {
        try {
            if (code.isEmpty())
                throw new Exception("Код не может быть пустым");
            if (tblReceiptItems.getItems().contains(new Order(0, code)))
                throw new Exception("Такой код уже введен");
            return true;
        }
        catch (Exception e) {
            CriticalMessageBox dlg = new CriticalMessageBox(e.getMessage());
            dlg.showAndWait();
            return false;
        }
    }

    @FXML
    void onReceiptCode(ActionEvent e) {
        Order order = new Order(tblReceiptItems.getItems().size() + 1, edtReceiptCode.getText());

        if (!validateOrderCode(order.getCode()))
            return;

        tblReceiptItems.getItems().add(order);
        ensureLastOrderVisible(tblReceiptItems);
    }

    private void ensureLastOrderVisible(TableView view) {
        try {
            VirtualFlow vf = (VirtualFlow)((TableViewSkin)view.getSkin()).getChildren().get(1);
            int rowsCount = view.getItems().size();
            int visibleRowsCount = vf.getLastVisibleCell().getIndex() - vf.getFirstVisibleCell().getIndex();
            IndexedCell cellToBeFirst = vf.getCell(rowsCount - visibleRowsCount);
            view.scrollTo(cellToBeFirst.getIndex());
        }
        catch (NullPointerException error) {
            //Do noting
        }
    }

    @FXML
    void onDisposalCode(ActionEvent e) {
        Order order = new Order(tblDisposalItems.getItems().size() + 1, edtDisposalCode.getText());

        if (!order.isValid() || tblDisposalItems.getItems().contains(order))
            return;

        tblDisposalItems.getItems().add(order);
    }

    @FXML
    void onReceiptAll(ActionEvent e) {
        try {
            EmExServiceStub stub = new EmExServiceStub();
            EmExServiceStub.TestConnect testConnect = new EmExServiceStub.TestConnect();
            testConnect.setS("Hi");
            EmExServiceStub.TestConnectResponse response = stub.testConnect(testConnect);
            System.out.println(response.getTestConnectResult());
        }
        catch (AxisFault error) {
            error.printStackTrace();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    @FXML
    void onActClose(ActionEvent e) {
        System.out.println("MainFrame::onActClose");
    }

    @FXML
    void onActAbout(ActionEvent e) {
        Alert aboutDlg = new Alert(Alert.AlertType.INFORMATION);
        aboutDlg.setTitle("О программе");
        aboutDlg.setHeaderText(null);
        String content = Main.APPLICATION_NAME + "\n";
        content += "Версия: 1.0.0\n";
        content += "Разработано по заказу - Представитель EMEX.RU г.Озерск\n";
        content += "Разработчик - Кощеев Максим (maximkosheev@gmail.com)";
        aboutDlg.setContentText(content);
        aboutDlg.showAndWait();
    }
}
