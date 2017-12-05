package com.madmax.emexws.models;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Invoice {
    /** Номера ячеек в файле INVOICE. Внимание! нумерация начинается с 0. СЦУКИ! */
    public final int REFERENCE_CELL_ID = 26;
    public final int GLOBALID_CELL_ID = 27;
    private final int CUSTOMER_CELL_ID = 28;

    private final int MAX_ITEMS_COUNT = 1500;

    private static Invoice ourInstance = new Invoice();

    private List<InvoiceItem> items;

    public static Invoice getInstance() {
        return ourInstance;
    }

    private Invoice() {
        items = new ArrayList<>();
    }

    public long getGlobalIdBySubId(long subId) {
        for (InvoiceItem item : items) {
            if (item.getSubId() == subId)
                return item.getGlobalId();
        }
        return 0;
    }

    public String getCustomerBySubId(long subId) {
        for (InvoiceItem item : items) {
            if (item.getSubId() == subId)
                return item.getCustomer();
        }
        return "";
    }

    public void load(File file) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIter = sheet.rowIterator();
            Row row = rowIter.next();

            try {
                while (rowIter.hasNext()) {
                    row = rowIter.next();
                    Cell firstCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    // рассматриваем только те строки, в которых первая ячейка есть число
                    if (firstCell.getCellTypeEnum() == CellType.NUMERIC) {
                        if (items.size() < MAX_ITEMS_COUNT) {
                            InvoiceItem item = new InvoiceItem();
                            item.setReference(row.getCell(REFERENCE_CELL_ID).getStringCellValue());
                            item.setGlobalId((long) row.getCell(GLOBALID_CELL_ID).getNumericCellValue());
                            item.setCustomer(row.getCell(CUSTOMER_CELL_ID).getStringCellValue());
                            if (item.getSubId() == 0L || item.getGlobalId() == 0L)
                                throw new NumberFormatException();
                            items.add(item);
                        }
                        else {
                            throw new Exception("Превышен лимит памяти, используемой для хранения счетов-фактуры\nТребуется перезагрузка приложения!");
                        }
                    }
                }
            }
            catch (Exception e) {
                throw new Exception("Ошибка при чтении счета-фактуры: строка: " + (row.getRowNum() + 1) + "\n" + "(" + e.getMessage() + ")");
            }
        }
        catch (Exception e) {
            items.clear();
            throw new IOException(e.getMessage());
        }
    }

    public int size() {
        return items.size();
    }
}
