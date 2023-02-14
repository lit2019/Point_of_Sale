package com.increff.pos.dto;

import com.increff.invoiceclient.generator.InvoiceGenerator;
import com.increff.invoiceclient.model.InvoiceForm;
import com.increff.invoiceclient.model.InvoiceItem;
import com.increff.pos.api.*;
import com.increff.pos.entity.*;
import com.increff.pos.model.*;
import com.increff.pos.util.ListUtils;
import com.increff.pos.util.NormalizationUtil;
import com.increff.pos.util.StringUtil;
import com.increff.pos.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
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
    private InvoiceGenerator invoiceGenerator;

    public void add(OrderForm orderForm) throws ApiException, IOException {
        ValidatorUtil.validate(orderForm);
        NormalizationUtil.normalize(orderForm);

        List<OrderItemForm> orderItemForms = orderForm.getOrderItemForms();
        checkDuplicateBarcode(orderItemForms);

        OrderPojo orderPojo = convert(orderForm);

        ArrayList<String> barcodes = new ArrayList<>();
        orderItemForms.forEach(form -> {
            barcodes.add(form.getBarcode());
        });

        HashMap<String, ProductPojo> barcodeToProductMap = productApi.getBarcodeToProductPojoMap(barcodes);
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
        checkNullObject(orderApi.get(orderId), "order with given Id dose not exist");
        ArrayList<OrderItemData> orderItemDataList = new ArrayList<>();
        List<OrderItemPojo> orderItemPojos = orderApi.getOrderItemsByOrderIds(Collections.singletonList(orderId));
        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            orderItemDataList.add(convert(orderItemPojo));
        }
        return orderItemDataList;
    }

    public void generateInvoice(Integer orderId) throws ApiException, IOException {
        OrderPojo orderPojo = orderApi.get(orderId);
        List<OrderItemData> orderItems = getOrderItems(orderId);
        InvoiceForm invoiceForm = getInvoiceByOrderPojo(orderPojo);
        invoiceForm.setInvoiceItems(convert(orderItems));
        getPdfBase64(orderId, invoiceForm);
    }

    public String getInvoice(Integer orderId) throws ApiException, IOException {
        OrderPojo orderPojo = orderApi.getCheck(orderId);
        InvoicePojo invoicePojo = invoiceApi.get(orderId);
        if (Objects.isNull(invoicePojo)) {
            generateInvoice(orderId);
        }
        orderPojo.setInvoicedStatus(true);
        return encoder(invoicePojo.getInvoiceUrl());
    }

    public ArrayList<SalesData> getSalesReport(SalesFilterForm filterForm) {
        NormalizationUtil.normalize(filterForm);
        List<InvoicePojo> invoicePojos = invoiceApi.getByDate(filterForm.getStartDate(), filterForm.getEndDate());
        ArrayList<Integer> orderIds = new ArrayList<>();
        for (InvoicePojo invoicePojo : invoicePojos) {
            orderIds.add(invoicePojo.getOrderId());
        }
        return getSalesDataByOrderItemPojo(filterForm, orderApi.getOrderItemsByOrderIds(orderIds));
    }

    public List<OrderData> getOrdersByFilter(OrderFilterForm filterForm) throws ApiException {
        ValidatorUtil.validate(filterForm);
        List<OrderPojo> orderPojos = orderApi.getByDate(filterForm.getStartDate(), filterForm.getEndDate());
        ArrayList<OrderData> orderDataList = new ArrayList<>();
        orderPojos.forEach(orderPojo -> {
            orderDataList.add(convert(orderPojo));
        });
        return orderDataList;
    }

    private void addOrderItems(Integer orderId, List<OrderItemForm> orderItemForms) throws ApiException {

    }

    private void getPdfBase64(Integer orderId, InvoiceForm invoiceForm) throws IOException, ApiException {
        String base64 = invoiceGenerator.getPdfBase64(orderId, invoiceForm);
        InvoicePojo invoicePojo = new InvoicePojo();
        invoicePojo.setOrderId(orderId);
        invoicePojo.setInvoiceUrl(generateInvoicePdf(orderId, base64));
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

    private ArrayList<SalesData> getSalesDataByOrderItemPojo(SalesFilterForm filterForm, List<OrderItemPojo> orderItemPojos) {
        HashMap<Integer, SalesData> brandCategoryToData = new HashMap<>();

        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            ProductPojo productPojo = productApi.get(orderItemPojo.getProductId());
            BrandPojo brandPojo = brandApi.get(productPojo.getBrandCategoryId());
            if (!allowBrandCategory(filterForm, brandPojo)) {
                continue;
            }
            Integer key = productPojo.getBrandCategoryId();
            SalesData salesData;
            if (brandCategoryToData.containsKey(key)) {
                salesData = brandCategoryToData.get(key);
            } else {
                salesData = new SalesData();
                salesData.setCategory(brandPojo.getCategory());
                salesData.setBrandName(brandPojo.getName());
                salesData.setRevenue(0.0);
                salesData.setQuantity(0);
            }
            salesData.setRevenue(salesData.getRevenue() + orderItemPojo.getQuantity() * productPojo.getMrp());
            salesData.setQuantity(salesData.getQuantity() + orderItemPojo.getQuantity());
            brandCategoryToData.put(key, salesData);
        }
        ArrayList<SalesData> salesDataList = new ArrayList<>();

        for (Integer brandCategoryId : brandCategoryToData.keySet())
            salesDataList.add(brandCategoryToData.get(brandCategoryId));

        return salesDataList;
    }

    private boolean allowBrandCategory(SalesFilterForm filterForm, BrandPojo brandPojo) {
        if ((!StringUtil.isEmpty(filterForm.getBrandName())) && (!filterForm.getBrandName().equals(brandPojo.getName()))) {
            return false;
        }
        return (StringUtil.isEmpty(filterForm.getCategory())) || (filterForm.getCategory().equals(brandPojo.getCategory()));
    }

    private InvoiceForm getInvoiceByOrderPojo(OrderPojo orderPojo) {
        InvoiceForm invoiceForm = new InvoiceForm();
        invoiceForm.setInvoiceTime(orderPojo.getCreatedAt().toString());
        invoiceForm.setInvoiceNumber(orderPojo.getId());
        return invoiceForm;
    }

    private void checkDuplicateBarcode(List<OrderItemForm> orderItemForms) throws ApiException {
        ArrayList<String> barcodes = new ArrayList<>();
        orderItemForms.forEach((form) -> {
            barcodes.add(form.getBarcode());
        });
        ListUtils.checkDuplicates(barcodes, "duplicate barcodes exist \n Erroneous barcodes : ");

    }

    private List<OrderItemPojo> convertToOrderItemPojos(List<OrderItemForm> orderItemForms) {
        ArrayList<OrderItemPojo> orderItemPojos = new ArrayList<>();
        orderItemForms.forEach(orderItemForm -> {
            ProductPojo productPojo = productApi.getByBarcode(orderItemForm.getBarcode());
            OrderItemPojo orderItemPojo = new OrderItemPojo();
            orderItemPojo.setQuantity(orderItemForm.getQuantity());
            orderItemPojo.setProductId(productPojo.getId());
            orderItemPojo.setSellingPrice(orderItemForm.getSellingPrice());
            orderItemPojos.add(orderItemPojo);
        });
        return orderItemPojos;
    }

    private OrderItemData convert(OrderItemPojo orderItemPojo) throws ApiException {
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