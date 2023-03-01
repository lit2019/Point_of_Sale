package com.increff.pos.util;

import com.increff.pos.entity.*;
import com.increff.pos.model.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestObjectUtils {
    public static BrandPojo getNewBrandPojo(String name, String category) {
        BrandPojo pojo = new BrandPojo();
        pojo.setName(name);
        pojo.setCategory(category);
        return pojo;
    }

    public static BrandForm getNewBrandForm(String name, String category) {
        BrandForm form = new BrandForm();
        form.setName(name);
        form.setCategory(category);
        return form;
    }

    public static ProductForm getNewProductForm(String brandName, String category, String productName, String barcode, Double mrp) {
        ProductForm form = new ProductForm();
        form.setProductName(productName);
        form.setBarcode(barcode);
        form.setMrp(mrp);
        form.setBrandName(brandName);
        form.setCategory(category);
        return form;
    }

    public static ProductPojo getNewProductPojo(Integer brandCategoryId, String name, String barcode, Double mrp) {
        ProductPojo pojo = new ProductPojo();
        pojo.setName(name);
        pojo.setBarcode(barcode);
        pojo.setMrp(mrp);
        pojo.setBrandCategoryId(brandCategoryId);
        return pojo;
    }

    public static OrderItemPojo getNewOrderItemPojo(Integer orderId, Integer productId, Integer quantity, Double sellingPrice) {
        OrderItemPojo pojo = new OrderItemPojo();
        pojo.setOrderId(orderId);
        pojo.setProductId(productId);
        pojo.setQuantity(quantity);
        pojo.setSellingPrice(sellingPrice);
        return pojo;
    }

    public static OrderItemForm getNewOrderItemForm(String barcode, Integer quantity, Double sellingPrice) {
        OrderItemForm form = new OrderItemForm();
        form.setBarcode(barcode);
        form.setQuantity(quantity);
        form.setSellingPrice(sellingPrice);
        return form;
    }

    public static List<ProductPojo> getNewProductPojoList() {
        ArrayList<ProductPojo> productPojoList = new ArrayList<>();
        productPojoList.add(getNewProductPojo(1, "product1", "barcode1", 100.0));
        productPojoList.add(getNewProductPojo(1, "product2", "barcode2", 100.0));
        productPojoList.add(getNewProductPojo(1, "product3", "barcode3", 100.0));
        return productPojoList;
    }

    public static List<InventoryPojo> getNewInventoryPojoList(List<ProductPojo> productPojoList) {
        ArrayList<InventoryPojo> inventoryPojoList = new ArrayList<>();
        productPojoList.forEach(productPojo -> {
            InventoryPojo inventoryPojo = getNewInventoryPojo(productPojo.getId(), 99999);
            inventoryPojoList.add(inventoryPojo);
        });
        return inventoryPojoList;
    }

    public static String getBasePdf64String() {
        return "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agozIDAgb2JqCjI0NzIKZW5kb2JqCjQgMCBvYmoKWy9JQ0NCYXNlZCAyIDAgUl0KZW5kb2JqCjUgMCBvYmoKPDwKICAvVHlwZSAvTWV0YWRhdGEKICAvU3VidHlwZSAvWE1MCiAgL0xlbmd0aCA2IDAgUgo+PgpzdHJlYW0KPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz48eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIj4KPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KPHJkZjpEZXNjcmlwdGlvbiB4bWxuczpkYz0iaHR0cDovL3B1cmwub3JnL2RjL2VsZW1lbnRzLzEuMS8iIHJkZjphYm91dD0iIj4KPGRjOmZvcm1hdD5hcHBsaWNhdGlvbi9wZGY8L2RjOmZvcm1hdD4KPGRjOmxhbmd1YWdlPngtdW5rbm93bjwvZGM6bGFuZ3VhZ2U+CjxkYzpkYXRlPjIwMjMtMDItMTZUMTc6NDY6NTMrMDU6MzA8L2RjOmRhdGU+CjwvcmRmOkRlc2NyaXB0aW9uPgo8cmRmOkRlc2NyaXB0aW9uIHhtbG5zOnBkZj0iaHR0cDovL25zLmFkb2JlLmNvbS9wZGYvMS4zLyIgcmRmOmFib3V0PSIiPgo8cGRmOlByb2R1Y2VyPkFwYWNoZSBGT1AgVmVyc2lvbiAyLjc8L3BkZjpQcm9kdWNlcj4KPHBkZjpQREZWZXJzaW9uPjEuNDwvcGRmOlBERlZlcnNpb24+CjwvcmRmOkRlc2NyaXB0aW9uPgo8cmRmOkRlc2NyaXB0aW9uIHhtbG5zOnhtcD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLyIgcmRmOmFib3V0PSIiPgo8eG1wOkNyZWF0b3JUb29sPkFwYWNoZSBGT1AgVmVyc2lvbiAyLjc8L3htcDpDcmVhdG9yVG9vbD4KPHhtcDpNZXRhZGF0YURhdGU+MjAyMy0wMi0xNlQxNzo0Njo1MyswNTozMDwveG1wOk1ldGFkYXRhRGF0ZT4KPHhtcDpDcmVhdGVEYXRlPjIwMjMtMDItMTZUMTc6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VHJpbUJveCBbMCAwIDU5NS4yNzUgODQxLjg4OV0KICAvUGFyZW50IDExIDAgUgogIC9Db250ZW50cyA3IDAgUgo+PgplbmRvYmoKMTIgMCBvYmoKPDwKICAvVHlwZSAvRm9udAogIC9TdWJ0eXBlIC9UeXBlMQogIC9CYXNlRm9udCAvSGVsdmV0aWNhCiAgL0VuY29kaW5nIC9XaW5BbnNpRW5jb2RpbmcKPj4KZW5kb2JqCjEzIDAgb2JqCjw8CiAgL1R5cGUgL0ZvbnQKICAvU3VidHlwZSAvVHlwZTEKICAvQmFzZUZvbnQgL0hlbHZldGljYS1Cb2xkCiAgL0VuY29kaW5nIC9XaW5BbnNpRW5jb2RpbmcKPj4KZW5kb2JqCjExIDAgb2JqCjw8IC9UeXBlIC9QYWdlcwovQ291bnQgMQovS2lkcyBbOSAwIFIgXSA+PgplbmRvYmoKMTQgMCBvYmoKPDwKICAvVHlwZSAvQ2F0YWxvZwogIC9QYWdlcyAxMSAwIFIKICAvTGFuZyAoeC11bmtub3duKQogIC9NZXRhZGF0YSA1IDAgUgogIC9QYWdlTGFiZWxzIDE1IDAgUgo+PgplbmRvYmoKMTAgMCBvYmoKPDwKICAvRm9udCA8PCAvRjEgMTIgMCBSIC9GMyAxMyAwIFIgPj4KICAvUHJvY1NldCBbL1BERiAvSW1hZ2VCIC9JbWFnZUMgL1RleHRdCiAgL0NvbG9yU3BhY2UgPDwgL0RlZmF1bHRSR0IgNCAwIFIgPj4KPj4KZW5kb2JqCjE1IDAgb2JqCjw8IC9OdW1zIFswIDw8IC9TIC9EID4+XSA+PgplbmRvYmoKeHJlZgowIDE2CjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAwMDAxNSAwMDAwMCBuIAowMDAwMDAwMTQ1IDAwMDAwIG4gCjAwMDAwMDI3MDIgMDAwMDAgbiAKMDAwMDAwMjcyMiAwMDAwMCBuIAowMDAwMDAyNzU1IDAwMDAwIG4gCjAwMDAwMDM3MDkgMDAwMDAgbiAKMDAwMDAwMzcyOCAwMDAwMCBuIAowMDAwMDA1MjMxIDAwMDAwIG4gCjAwMDAwMDUyNTEgMDAwMDAgbiAKMDAwMDAwNTg2NSAwMDAwMCBuIAowMDAwMDA1NjkyIDAwMDAwIG4gCjAwMDAwMDU0NzUgMDAwMDAgbiAKMDAwMDAwNTU4MSAwMDAwMCBuIAowMDAwMDA1NzUxIDAwMDAwIG4gCjAwMDAwMDYwMDEgMDAwMDAgbiAKdHJhaWxlcgo8PAogIC9Sb290IDE0IDAgUgogIC9JbmZvIDEgMCBSCiAgL0lEIFs8NkQwNDQzMTUwQkZEMEY2NjVCOTM2QUIzOTFGNzE0RkQ+IDw2RDA0NDMxNTBCRkQwRjY2NUI5MzZBQjM5MUY3MTRGRD5dCiAgL1NpemUgMTYKPj4Kc3RhcnR4cmVmCjYwNDUKJSVFT0YK";
    }

    public static ProductSearchForm getNewProductSearchForm(String brand, String category, String barcode) {
        ProductSearchForm form = new ProductSearchForm();
        form.setBrandName(brand);
        form.setCategory(category);
        form.setBarcode(barcode);
        form.setPageNo(1);
        form.setPageSize(1000);
        return form;
    }

    public static InventoryPojo getNewInventoryPojo(Integer productId, Integer quantity) {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(quantity);
        return pojo;
    }

    public static InventoryAllocationRequest getNewInventoryAllocationRequest(Integer id, Integer quantityToReduce) {
        InventoryAllocationRequest request = new InventoryAllocationRequest();
        request.setProductId(id);
        request.setQuantityToReduce(quantityToReduce);
        return request;
    }

    public static List<BrandPojo> getNewBrandPojoList() {
        List<BrandPojo> brandPojos = new ArrayList<>();
        brandPojos.add(getNewBrandPojo("brand1", "category1"));
        brandPojos.add(getNewBrandPojo("brand1", "category2"));
        brandPojos.add(getNewBrandPojo("brand2", "category1"));
        return brandPojos;
    }

    public static InvoicePojo getNewInvoicePojo(Integer id) {
        InvoicePojo pojo = new InvoicePojo();
        pojo.setOrderId(id);
        pojo.setInvoiceUrl("testUrl");
        return pojo;
    }

    public static InventoryForm getNewInventoryForm(String barcode, int quantity) {
        InventoryForm form = new InventoryForm();
        form.setBarcode(barcode);
        form.setQuantity(quantity);
        return form;
    }


    public static List<OrderItemPojo> getNewOrderItemPojoList(Integer orderId) {
        List<OrderItemPojo> orderItemPojos = new ArrayList<>();
        orderItemPojos.add(getNewOrderItemPojo(orderId, 1, 1, 1.0));
        orderItemPojos.add(getNewOrderItemPojo(orderId, 2, 2, 2.0));
        orderItemPojos.add(getNewOrderItemPojo(orderId, 3, 3, 3.0));
        return orderItemPojos;
    }

    public static DailySalesPojo getNewDailySalesPojo(Double revenue, Integer orderCount, Integer itemCount, ZonedDateTime date) {
        DailySalesPojo pojo = new DailySalesPojo();
        pojo.setTotalRevenue(revenue);
        pojo.setInvoicedOrdersCount(orderCount);
        pojo.setInvoicedItemsCount(itemCount);
        pojo.setDate(date);
        return pojo;
    }
}
