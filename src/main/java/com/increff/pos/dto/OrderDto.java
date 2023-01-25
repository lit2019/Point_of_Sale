package com.increff.pos.dto;

import com.increff.pos.api.*;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.OrderItemPojo;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.OrderForm;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class OrderDto extends AbstractDto {

    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private OrderItemApi orderItemApi;

    @Autowired
    private ProductApi productApi;

    public void add(OrderForm orderForm) throws ApiException {
        checkNull(orderForm);
        normalize(orderForm);
        List<OrderItemForm> orderItemsList = orderForm.getOrderItemForms();

        checkDuplicateBarcode(orderItemsList);
        checkExistingInventory(orderItemsList);

        OrderPojo orderPojo = convert(orderForm);
        orderApi.add(orderPojo);

        Integer orderId = orderPojo.getId();
        for (OrderItemForm orderItemForm : orderItemsList) {
            add(convert(orderId, orderItemForm));
        }
    }

    private void checkExistingInventory(List<OrderItemForm> orderItemsList) throws ApiException {
        ArrayList<String> barcodesWithoutInventory = new ArrayList<>();
        for (Integer i = 0; i < orderItemsList.size(); i++) {
            OrderItemForm orderItemForm = orderItemsList.get(i);
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

    private void add(OrderItemPojo orderItemPojo) throws ApiException {
        orderItemApi.add(orderItemPojo);
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
        orderPojo.setTime(instant.toEpochMilli());
        return orderPojo;
    }

    private void normalize(OrderForm orderForm) {
        List<OrderItemForm> orderItemsList = orderForm.getOrderItemForms();

        for (OrderItemForm orderItemForm : orderItemsList) {
            orderItemForm.setBarcode(StringUtil.normaliseText(orderItemForm.getBarcode()));
        }
    }

    private void checkNull(OrderForm orderForm) throws ApiException {
        if (Objects.isNull(orderForm)) {
            throw new ApiException("Order form cannot be null");
        }
        if (Objects.isNull(orderForm.getOrderItemForms())) {
            throw new ApiException("Order items cannot be null");
        }
        String errorMessage = "";
        List<OrderItemForm> orderItemsList = orderForm.getOrderItemForms();
        for (Integer i = 0; i < orderItemsList.size(); i++) {
            OrderItemForm orderItemForm = orderItemsList.get(i);
            if (Objects.isNull(orderItemForm)) {
                errorMessage += String.format("null row in %d", i + 1);
            }
        }
        if (!(errorMessage.equals(""))) {
            throw new ApiException("null order items exist \n" + errorMessage);
        }

    }

    public List<OrderItemData> get(Integer orderId) throws ApiException {
        ArrayList<OrderItemData> orderItemDatas = new ArrayList<>();
        List<OrderItemPojo> orderItemPojos = orderItemApi.get(orderId);
        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            orderItemDatas.add(convert(orderItemPojo));
        }
        return orderItemDatas;
    }

    private OrderItemData convert(OrderItemPojo orderItemPojo) throws ApiException {
        OrderItemData orderItemData = new OrderItemData();
        orderItemData.setBarcode(productApi.get(orderItemPojo.getProductId()).getBarcode());
        orderItemData.setQuantity(orderItemPojo.getQuantity());
        orderItemData.setId(orderItemPojo.getId());
        return orderItemData;
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
}
