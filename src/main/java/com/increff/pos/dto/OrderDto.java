package com.increff.pos.dto;

import com.increff.pos.api.*;
import com.increff.pos.entity.*;
import com.increff.pos.model.*;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.increff.pos.util.ListUtils.checkNonEmptyList;
import static com.increff.pos.util.ValidatorUtil.validate;

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
    @Autowired
    private BrandApi brandApi;

    public OrderData add(OrderForm orderForm) throws ApiException, IOException {
        validateOrder(orderForm);
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

        return convert(orderPojo);
    }

    public List<OrderItemData> getOrderItems(Integer orderId) throws ApiException {
        checkNullObject(orderApi.get(orderId), "order with given Id dose not exist");
        ArrayList<OrderItemData> orderItemDatas = new ArrayList<>();
        List<OrderItemPojo> orderItemPojos = orderItemApi.get(Collections.singletonList(orderId));
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
        getPdfBase64(orderId, invoiceData);
    }

    public InvoicePojo getInvoice(Integer orderId) throws ApiException, IOException {
        InvoicePojo invoicePojo = invoiceApi.get(orderId);
        if (Objects.isNull(invoicePojo)) {
            generateInvoice(orderId);
        }
        return invoicePojo;
    }

    public ArrayList<SalesData> getSalesReport(SalesFilterForm filterForm) throws ApiException {
        List<InvoicePojo> invoicePojos = invoiceApi.get(filterForm);
        ArrayList<Integer> orderIds = new ArrayList<>();
        for (InvoicePojo invoicePojo : invoicePojos) {
            orderIds.add(invoicePojo.getOrderId());
        }
        return getSalesDataByOrderItemPojo(filterForm, orderItemApi.get(orderIds));
    }

    private void getPdfBase64(Integer orderId, InvoiceData invoiceData) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiUrl = "http://localhost:8000/fop/api/invoice";
        RestTemplate RestTemplate = new RestTemplate();
        ResponseEntity<String> apiResponse = RestTemplate.postForEntity(apiUrl, invoiceData, String.class);
        String base64 = apiResponse.getBody();
        InvoicePojo invoicePojo = new InvoicePojo();
        invoicePojo.setOrderId(orderId);
        invoicePojo.setInvoiceLink(generateInvoicePdf(orderId, base64));
        invoiceApi.add(invoicePojo);
    }

    private static String generateInvoicePdf(Integer orderId, String base64) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64.getBytes());
        File pdfDir = new File("src/main/resources/PdfFiles");
        pdfDir.mkdirs();
        String pdfFileName = "invoice_" + orderId + ".pdf";
        File pdfFile = new File(pdfDir, pdfFileName);
        FileOutputStream fos = new FileOutputStream(pdfFile);
        fos.write(decodedBytes);
        fos.flush();
        fos.close();
        return pdfFile.getAbsolutePath();
    }

    private ArrayList<SalesData> getSalesDataByOrderItemPojo(SalesFilterForm filterForm, List<OrderItemPojo> orderItemPojos) throws ApiException {
        HashMap<String, SalesData> brandCategoryToData = new HashMap<>();

        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            ProductPojo productPojo = productApi.get(orderItemPojo.getProductId());
            BrandPojo brandPojo = brandApi.get(productPojo.getBrandCategoryId());
            if (!allowBrandCategory(filterForm, brandPojo)) {
                continue;
            }
            String brandName = brandPojo.getName();
            String category = brandPojo.getCategory();
            String key = brandName + "_" + category;
            SalesData salesData = brandCategoryToData.getOrDefault(key, new SalesData(0, 0.0, brandName, category));
            salesData.setRevenue(salesData.getRevenue() + orderItemPojo.getQuantity() * productPojo.getMrp());
            salesData.setQuantity(salesData.getQuantity() + orderItemPojo.getQuantity());
            brandCategoryToData.put(key, salesData);
        }
        ArrayList<SalesData> salesDatas = new ArrayList<>();

        for (String brandCategory : brandCategoryToData.keySet()) {
            salesDatas.add(brandCategoryToData.get(brandCategory));
        }
        return salesDatas;
    }

    private boolean allowBrandCategory(SalesFilterForm filterForm, BrandPojo brandPojo) {
        if ((!StringUtil.isEmpty(filterForm.getBrandName())) && (!filterForm.getBrandName().equals(brandPojo.getName()))) {
            return false;
        }
        if ((!StringUtil.isEmpty(filterForm.getCategory())) && (!filterForm.getCategory().equals(brandPojo.getCategory()))) {
            return false;
        }
        return true;
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
            invoiceItem.setUnitPrice(orderItemData.getSellingPrice() / orderItemData.getQuantity());
            invoiceItem.setTotal(orderItemData.getSellingPrice());
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
        orderItemPojo.setSellingPrice(orderItemForm.getSellingPrice());
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
        orderItemData.setSellingPrice(orderItemPojo.getSellingPrice());
        return orderItemData;
    }

    private OrderData convert(OrderPojo pojo) {
        OrderData orderData = new OrderData();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy, HH:mm:ss z", Locale.ENGLISH);
        orderData.setCreatedAt(pojo.getCreatedAt().format(df));
        orderData.setId(pojo.getId());
        return orderData;
    }

    protected void validateOrder(OrderForm orderForm) throws ApiException {
        for (OrderItemForm orderItemForm : orderForm.getOrderItemForms()) {
            validate(orderItemForm);
        }
    }
}
