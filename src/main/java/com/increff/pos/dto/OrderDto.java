package com.increff.pos.dto;

import com.increff.pos.api.*;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.OrderItemPojo;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderForm;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void add(OrderForm orderForm) throws ApiException {
        validate(orderForm);
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
        return orderPojo;
    }

    private void normalize(OrderForm orderForm) {
        List<OrderItemForm> orderItemsList = orderForm.getOrderItemForms();

        orderItemsList.forEach((orderItemForm) -> {
            orderItemForm.setBarcode(StringUtil.normaliseText(orderItemForm.getBarcode()));
        });
    }

    @Override
    protected void validate(OrderForm orderForm) throws ApiException {
        super.validate(orderForm);

        for (OrderItemForm orderItemForm : orderForm.getOrderItemForms()) {
            orderItemDto.validate(orderItemForm);
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
        ProductPojo productPojo = productApi.get(orderItemPojo.getProductId());
        orderItemData.setBarcode(productPojo.getBarcode());
        orderItemData.setQuantity(orderItemPojo.getQuantity());
        orderItemData.setId(orderItemPojo.getId());
        orderItemData.setProductName(productPojo.getName());
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

    public List<OrderData> get() {
        List<OrderPojo> pojos = orderApi.get();
        ArrayList<OrderData> datas = new ArrayList<>();
        for (OrderPojo pojo : pojos) {
            datas.add(convert(pojo));
        }
        return datas;
    }

    private OrderData convert(OrderPojo pojo) {
        OrderData orderData = new OrderData();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy, HH:mm:ss z", Locale.ENGLISH);
        orderData.setCreatedAt(pojo.getCreatedAt().format(df));
        orderData.setId(pojo.getId());
        return orderData;
    }
}
