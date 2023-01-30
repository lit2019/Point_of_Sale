package com.increff.pos.dto;

import com.increff.pos.api.*;
import com.increff.pos.entity.*;
import com.increff.pos.model.*;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.increff.pos.util.ListUtils.checkNonEmptyList;

@Service
public class OrderDto extends AbstractDto<OrderForm> {

    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private OrderItemApi orderItemApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private OrderItemDto orderItemDto;
    @Autowired
    private InvoiceApi invoiceApi;

    public void add(OrderForm orderForm) throws ApiException, IOException {
        validate(orderForm);
        normalize(orderForm);
        List<OrderItemForm> orderItemsList = orderForm.getOrderItemForms();

        checkDuplicateBarcode(orderItemsList);
        checkExistingInventory(orderItemsList);

        OrderPojo orderPojo = convert(orderForm);
        orderApi.add(orderPojo);

        Integer orderId = orderPojo.getId();
        for (OrderItemForm orderItemForm : orderItemsList) {
            orderItemApi.add(convert(orderId, orderItemForm));
        }

        generateInvoice(orderId);
    }

    public List<OrderItemData> getOrderItems(Integer orderId) throws ApiException {
        checkNullObject(orderApi.get(orderId), "order with given Id dose not exist");
        ArrayList<OrderItemData> orderItemDatas = new ArrayList<>();
        List<OrderItemPojo> orderItemPojos = orderItemApi.get(orderId);
        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            orderItemDatas.add(convert(orderItemPojo));
        }
        return orderItemDatas;
    }

    public void addItem(Integer orderId, OrderItemForm orderItemForm) throws ApiException {
        if (Objects.isNull(orderApi.get(orderId))) {
            throw new ApiException(String.format("order with given id : %d dose not exist", orderId));
        }
        orderItemApi.add(convert(orderId, orderItemForm));
    }

    public void updateItem(Integer id, OrderItemForm orderItemForm) {
        orderItemApi.update(id, orderItemForm);
    }

    public List<OrderData> getOrderItems() {
        List<OrderPojo> pojos = orderApi.get();
        ArrayList<OrderData> datas = new ArrayList<>();
        for (OrderPojo pojo : pojos) {
            datas.add(convert(pojo));
        }
        return datas;
    }

    public void generateInvoice(Integer orderId) throws ApiException, IOException {
        OrderPojo orderPojo = orderApi.get(orderId);
        List<OrderItemData> orderItems = getOrderItems(orderId);
        InvoiceData invoiceData = getInvoiceByOrderPojo(orderPojo);
        invoiceData.setLineItems(convert(orderItems));
        orderApi.getPdfBase64(orderId, invoiceData);
    }

    public InvoicePojo getInvoice(Integer orderId) throws ApiException {
        InvoicePojo invoicePojo = invoiceApi.get(orderId);
        return invoicePojo;
    }

    private InvoiceData getInvoiceByOrderPojo(OrderPojo orderPojo) {
        InvoiceData invoiceData = new InvoiceData();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
        invoiceData.setInvoiceDate(orderPojo.getCreatedAt().format(df));
        df = DateTimeFormatter.ofPattern("HH:mm:ss z", Locale.ENGLISH);
        invoiceData.setInvoiceTime(orderPojo.getCreatedAt().format(df));
        invoiceData.setInvoiceNumber(orderPojo.getId());
        return invoiceData;
    }

    private List<InvoiceItem> convert(List<OrderItemData> orderItems) {
        ArrayList<InvoiceItem> invoiceItems = new ArrayList<>();
        for (OrderItemData orderItemData : orderItems) {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setProductName(orderItemData.getProductName());
            invoiceItem.setBarcode(orderItemData.getBarcode());
            invoiceItem.setQuantity(orderItemData.getQuantity());
            invoiceItem.setUnitPrice(orderItemData.getMrp());
            invoiceItem.setTotal(orderItemData.getMrp() * orderItemData.getQuantity());
            invoiceItems.add(invoiceItem);
        }
        return invoiceItems;
    }

    private void checkExistingInventory(List<OrderItemForm> orderItemsList) throws ApiException {
        ArrayList<String> barcodesWithoutInventory = new ArrayList<>();
        for (OrderItemForm orderItemForm : orderItemsList) {
            InventoryPojo inventoryPojo = inventoryApi.getByBarcode(orderItemForm.getBarcode());
            if (Objects.isNull(inventoryPojo) || inventoryPojo.getQuantity() < orderItemForm.getQuantity()) {
                barcodesWithoutInventory.add(orderItemForm.getBarcode());
            }
        }
        checkNonEmptyList(barcodesWithoutInventory, "product(s) with given barcode(s) are not available : " + barcodesWithoutInventory);
    }

    private void checkDuplicateBarcode(List<OrderItemForm> orderItemsList) throws ApiException {
        HashSet<String> barcodeSet = new HashSet<>();
        ArrayList<String> duplicates = new ArrayList<>();
        for (OrderItemForm orderItemForm : orderItemsList) {
            String key = orderItemForm.getBarcode();
            if (barcodeSet.contains(key)) {
                duplicates.add(key);
            } else {
                barcodeSet.add(key);
            }
        }
        checkNonEmptyList(duplicates, "duplicate barcodes exist : " + duplicates.toString());

    }

    private OrderItemPojo convert(Integer orderId, OrderItemForm orderItemForm) {
        ProductPojo productPojo = productApi.getByBarcode(orderItemForm.getBarcode());
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setOrderId(orderId);
        orderItemPojo.setQuantity(orderItemForm.getQuantity());
        orderItemPojo.setProductId(productPojo.getId());
        orderItemPojo.setSellingPrice(productPojo.getMrp() * orderItemForm.getQuantity());
        return orderItemPojo;
    }

    private OrderPojo convert(OrderForm orderForm) {
        OrderPojo orderPojo = new OrderPojo();
        Instant instant = Instant.now();
        return orderPojo;
    }

    private void normalize(OrderForm orderForm) {
        List<OrderItemForm> orderItemsList = orderForm.getOrderItemForms();

        orderItemsList.forEach((orderItemForm) -> {
            orderItemForm.setBarcode(StringUtil.normaliseText(orderItemForm.getBarcode()));
        });
    }

    private OrderItemData convert(OrderItemPojo orderItemPojo) throws ApiException {
        OrderItemData orderItemData = new OrderItemData();
        ProductPojo productPojo = productApi.get(orderItemPojo.getProductId());
        orderItemData.setBarcode(productPojo.getBarcode());
        orderItemData.setQuantity(orderItemPojo.getQuantity());
        orderItemData.setId(orderItemPojo.getId());
        orderItemData.setProductName(productPojo.getName());
        orderItemData.setMrp(productPojo.getMrp());
        return orderItemData;
    }

    private OrderData convert(OrderPojo pojo) {
        OrderData orderData = new OrderData();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy, HH:mm:ss z", Locale.ENGLISH);
        orderData.setCreatedAt(pojo.getCreatedAt().format(df));
        orderData.setId(pojo.getId());
        return orderData;
    }

    @Override
    protected void validate(OrderForm orderForm) throws ApiException {
        super.validate(orderForm);

        for (OrderItemForm orderItemForm : orderForm.getOrderItemForms()) {
            orderItemDto.validate(orderItemForm);
        }

    }
}
