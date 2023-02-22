package com.increff.pos.dto;

import com.increff.invoiceclient.generator.InvoiceClient;
import com.increff.invoiceclient.model.InvoiceForm;
import com.increff.invoiceclient.model.InvoiceItem;
import com.increff.pos.api.*;
import com.increff.pos.entity.*;
import com.increff.pos.model.*;
import com.increff.pos.util.ListUtils;
import com.increff.pos.util.NormalizationUtil;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderDto extends AbstractDto {

    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InvoiceApi invoiceApi;
    @Autowired
    private BrandApi brandApi;

    //    todo classname should be invoiceclient and function name should be getInvoiceClient
    @Autowired
    private InvoiceClient invoiceClient;
    @Value("${app.invoiceServerBaseUrl}")
    private String invoiceServerBaseUrl;

    private static final Integer MAX_UPLOAD_SIZE = 5000;

    @Transactional(rollbackOn = Exception.class)
    public void add(OrderForm orderForm) throws ApiException {
        ValidationUtil.validate(orderForm);
        NormalizationUtil.normalize(orderForm);

        List<OrderItemForm> orderItemForms = orderForm.getOrderItemForms();
        ListUtils.checkUploadLimit(orderItemForms, MAX_UPLOAD_SIZE);
//        TODO pass the barcodes list created in line 48

        OrderPojo orderPojo = convert(orderForm);

        ArrayList<String> barcodes = new ArrayList<>();
        orderItemForms.forEach(form -> {
            barcodes.add(form.getBarcode());
        });

        ListUtils.checkDuplicates(barcodes, "duplicate barcodes exist \n Erroneous barcodes : ");

//TODO rewrite getBarcodeToProductPojoMap
        Map<String, ProductPojo> barcodeToProductMap = ProductApi.getBarcodeToProductPojoMap(productApi.getByBarcodes(barcodes));
        HashMap<Integer, String> productIdToBarcode = new HashMap<>();
        for (String barcode : barcodeToProductMap.keySet())
            productIdToBarcode.put(barcodeToProductMap.get(barcode).getId(), barcode);

        ArrayList<InventoryAllocationRequest> inventoryAllocationRequests = new ArrayList<>();
        orderItemForms.forEach(orderItemForm -> {
            InventoryAllocationRequest allocationRequest = new InventoryAllocationRequest();
            allocationRequest.setProductId(barcodeToProductMap.get(orderItemForm.getBarcode()).getId());
            allocationRequest.setQuantityToReduce(orderItemForm.getQuantity());
            inventoryAllocationRequests.add(allocationRequest);
        });
        inventoryApi.allocateInventory(inventoryAllocationRequests, productIdToBarcode);

        orderApi.add(orderPojo, convertToOrderItemPojos(orderItemForms));
    }

    public List<OrderItemData> getOrderItems(Integer orderId) throws ApiException {
        checkNullObject(orderApi.get(orderId), String.format("order with Id : %d dose not exist", orderId));
        ArrayList<OrderItemData> orderItemDataList = new ArrayList<>();
        List<OrderItemPojo> orderItemPojos = orderApi.getOrderItemsByOrderIds(Collections.singletonList(orderId));
        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            orderItemDataList.add(convert(orderItemPojo));
        }
        return orderItemDataList;
    }

    public String getInvoice(Integer orderId) throws ApiException, IOException {
        InvoicePojo invoicePojo = invoiceApi.get(orderId);
        if (Objects.isNull(invoicePojo)) {
            generateInvoice(orderId);
            invoicePojo = invoiceApi.get(orderId);
        }
//        TODO move to generateInvoice method
        return encoder(invoicePojo.getInvoiceUrl());
    }

    //TODo make return type LIst<>
    public List<SalesData> getSalesReport(SalesFilterForm filterForm) throws ApiException {
        ValidationUtil.validate(filterForm);
        NormalizationUtil.normalize(filterForm);
        List<InvoicePojo> invoicePojos = invoiceApi.getByDate(filterForm.getStartDate(), filterForm.getEndDate());
        ArrayList<Integer> orderIds = new ArrayList<>();
        for (InvoicePojo invoicePojo : invoicePojos)
            orderIds.add(invoicePojo.getOrderId());

        return getSalesDataByOrderItems(filterForm, orderApi.getOrderItemsByOrderIds(orderIds));
    }

    //TODO make filterform to search form
    public List<OrderData> getOrdersByFilter(OrderFilterForm filterForm) throws ApiException {
        if (Objects.nonNull(filterForm.getOrderId())) {
            return Collections.singletonList(convert(orderApi.get(filterForm.getOrderId())));
        }
        ValidationUtil.validate(filterForm);
        List<OrderPojo> orderPojos = orderApi.getByFilter(filterForm);
        ArrayList<OrderData> orderDataList = new ArrayList<>();
        orderPojos.forEach(orderPojo -> {
            orderDataList.add(convert(orderPojo));
        });
        return orderDataList;
    }

    private void generateInvoice(Integer orderId) throws ApiException, IOException {
        OrderPojo orderPojo = orderApi.get(orderId);
        List<OrderItemData> orderItems = getOrderItems(orderId);

        InvoiceForm invoiceForm = new InvoiceForm();
        invoiceForm.setInvoiceTime(orderPojo.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        invoiceForm.setInvoiceDate(orderPojo.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        invoiceForm.setInvoiceNumber(orderPojo.getId());
        invoiceForm.setInvoiceItems(convert(orderItems));

        String base64 = invoiceClient.getPdfBase64(invoiceForm, invoiceServerBaseUrl);
        InvoicePojo invoicePojo = new InvoicePojo();
        invoicePojo.setOrderId(orderId);
        invoicePojo.setInvoiceUrl(generateInvoicePdf(orderId, base64));
        invoiceApi.add(invoicePojo);

        orderApi.setStatus(orderPojo.getId(), OrderStatus.INVOICED);
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

    private List<SalesData> getSalesDataByOrderItems(SalesFilterForm filterForm, List<OrderItemPojo> orderItemPojos) {
        HashMap<Integer, SalesData> brandIdToSalesData = new HashMap<>();

        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            ProductPojo productPojo = productApi.get(orderItemPojo.getProductId());
            BrandPojo brandPojo = brandApi.get(productPojo.getBrandCategoryId());
            if (!allowBrandCategory(filterForm, brandPojo)) {
                continue;
            }
            Integer key = productPojo.getBrandCategoryId();
            SalesData salesData;
            if (brandIdToSalesData.containsKey(key)) {
                salesData = brandIdToSalesData.get(key);
            } else {
                salesData = new SalesData();
                salesData.setCategory(brandPojo.getCategory());
                salesData.setBrandName(brandPojo.getName());
                salesData.setRevenue(0.0);
                salesData.setQuantity(0);
            }
            salesData.setRevenue(salesData.getRevenue() + orderItemPojo.getQuantity() * productPojo.getMrp());
            salesData.setQuantity(salesData.getQuantity() + orderItemPojo.getQuantity());
            brandIdToSalesData.put(key, salesData);
        }
        ArrayList<SalesData> salesDataList = new ArrayList<>();

        for (Integer brandCategoryId : brandIdToSalesData.keySet())
            salesDataList.add(brandIdToSalesData.get(brandCategoryId));

        return salesDataList;
    }

    private boolean allowBrandCategory(SalesFilterForm filterForm, BrandPojo brandPojo) {
        if ((!StringUtil.isEmpty(filterForm.getBrandName())) && (!filterForm.getBrandName().equals(brandPojo.getName()))) {
            return false;
        }
        return (StringUtil.isEmpty(filterForm.getCategory())) || (filterForm.getCategory().equals(brandPojo.getCategory()));
    }

    //todo do not take string here
    private List<OrderItemPojo> convertToOrderItemPojos(List<OrderItemForm> orderItemForms) throws ApiException {
        ArrayList<OrderItemPojo> orderItemPojos = new ArrayList<>();
        for (OrderItemForm orderItemForm : orderItemForms) {
            ProductPojo productPojo = productApi.getUniqueByBarcode(orderItemForm.getBarcode());
            OrderItemPojo orderItemPojo = new OrderItemPojo();
            orderItemPojo.setQuantity(orderItemForm.getQuantity());
            orderItemPojo.setProductId(productPojo.getId());
            orderItemPojo.setSellingPrice(orderItemForm.getSellingPrice());
            orderItemPojos.add(orderItemPojo);
        }
        return orderItemPojos;
    }

    private OrderItemData convert(OrderItemPojo orderItemPojo) {
        OrderItemData orderItemData = new OrderItemData();
        ProductPojo productPojo = productApi.get(orderItemPojo.getProductId());
        orderItemData.setBarcode(productPojo.getBarcode());
        orderItemData.setQuantity(orderItemPojo.getQuantity());
        orderItemData.setProductName(productPojo.getName());
        orderItemData.setSellingPrice(orderItemPojo.getSellingPrice());
        return orderItemData;
    }

    private OrderData convert(OrderPojo pojo) {
        OrderData orderData = new OrderData();
        orderData.setCreatedAt(pojo.getCreatedAt());
        orderData.setId(pojo.getId());
        orderData.setOrderStatus(pojo.getOrderStatus());
        return orderData;
    }

    private OrderPojo convert(OrderForm orderForm) {
        return new OrderPojo();
    }

    private List<InvoiceItem> convert(List<OrderItemData> orderItems) {
        ArrayList<InvoiceItem> invoiceItems = new ArrayList<>();
        for (OrderItemData orderItemData : orderItems) {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setProductName(orderItemData.getProductName());
            invoiceItem.setBarcode(orderItemData.getBarcode());
            invoiceItem.setQuantity(orderItemData.getQuantity());
            invoiceItem.setUnitPrice(orderItemData.getSellingPrice());
            invoiceItem.setTotal(orderItemData.getSellingPrice() * orderItemData.getQuantity());
            invoiceItems.add(invoiceItem);
        }
        return invoiceItems;
    }

    private String encoder(String filePath) {
        String base64File = "";
        File file = new File(filePath);
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            // Reading a file from file system
            byte fileData[] = new byte[(int) file.length()];
            imageInFile.read(fileData);
            base64File = Base64.getEncoder().encodeToString(fileData);
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the file " + ioe);
        }
        return base64File;
    }
}