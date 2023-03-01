package com.increff.pos.dto;

import com.increff.invoiceclient.generator.InvoiceClient;
import com.increff.invoiceclient.model.InvoiceForm;
import com.increff.pos.api.ApiException;
import com.increff.pos.dao.*;
import com.increff.pos.entity.BrandPojo;
import com.increff.pos.entity.InventoryPojo;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.entity.ProductPojo;
import com.increff.pos.model.*;
import com.increff.pos.spring.AbstractUnitTest;
import com.increff.pos.util.TestObjectUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.increff.pos.util.TestObjectUtils.getNewOrderItemForm;
import static com.increff.pos.util.TestObjectUtils.getNewOrderItemPojo;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderDtoTest extends AbstractUnitTest {
    @Mock
    private InvoiceClient invoiceClient;
    @Autowired
    private OrderDto orderDto;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private InvoiceDao invoiceDao;

    @Value("${app.invoiceServerBaseUrl}")
    private String invoiceServerBaseUrl;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private BrandDao brandDao;


    @Test
    public void testAddEmptyList() {
        OrderForm orderForm = new OrderForm();
        try {
            orderDto.add(orderForm);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("order items cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testAddDuplicateBarcode() {
        String duplicateBarcode = "barcode2";
        ArrayList<OrderItemForm> orderItemForms = new ArrayList<>();
        orderItemForms.add(getNewOrderItemForm("barcode1", 1, 10.0));
        orderItemForms.add(getNewOrderItemForm(duplicateBarcode, 1, 10.0));
        orderItemForms.add(getNewOrderItemForm(duplicateBarcode, 1, 10.0));

        OrderForm orderForm = new OrderForm();
        orderForm.setOrderItemForms(orderItemForms);
        try {
            orderDto.add(orderForm);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals(String.format("duplicate barcodes exist \n Erroneous barcodes : [%s]", duplicateBarcode), e.getMessage());
        }
    }

    @Test
    public void testAdd() throws ApiException {
        List<ProductPojo> productPojos = TestObjectUtils.getNewProductPojoList();
        productPojos.forEach(productDao::insert);

        List<InventoryPojo> inventoryPojos = TestObjectUtils.getNewInventoryPojoList(productPojos);
        inventoryPojos.forEach(inventoryDao::insert);

        ArrayList<OrderItemForm> orderItemForms = new ArrayList<>();
        productPojos.forEach(productPojo -> orderItemForms.add(getNewOrderItemForm("  " + productPojo.getBarcode() + "   ", 1, 10.0)));

        OrderForm orderForm = new OrderForm();
        orderForm.setOrderItemForms(orderItemForms);
        orderDto.add(orderForm);

        assertEquals(1, orderDao.selectAll().size());
        assertEquals(3, orderItemDao.selectAll().size());

        productPojos.forEach(productPojo -> assertEquals((Integer) 99998, inventoryDao.select(productPojo.getId()).getQuantity()));
    }

    @Test
    public void testGenerateInvoice() throws IOException, ApiException {
        invoiceClient = mock(InvoiceClient.class);
        when(invoiceClient.getPdfBase64(any(InvoiceForm.class), anyString())).thenReturn(TestObjectUtils.getBasePdf64String());
        ReflectionTestUtils.setField(orderDto, "invoiceClient", invoiceClient);

        OrderPojo orderPojo = new OrderPojo();
        orderDao.insert(orderPojo);

        List<ProductPojo> productPojos = TestObjectUtils.getNewProductPojoList();
        productPojos.forEach(productPojo -> {
            productDao.insert(productPojo);
            orderItemDao.insert(getNewOrderItemPojo(orderPojo.getId(), productPojo.getId(), 1, 1.0));
        });

        orderDto.getInvoice(orderPojo.getId());
        assertEquals(String.format("/home/mark/IdeaProjects/pos/src/main/resources/PdfFiles/invoice_%d.pdf", orderPojo.getId()), invoiceDao.select(orderPojo.getId()).getInvoiceUrl());
    }

    @Test
    public void testGetOrdersByFilter() throws ApiException {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setOrderStatus(OrderStatus.INVOICED);
        orderDao.insert(orderPojo);

        OrderFilterForm filterForm = new OrderFilterForm();
        filterForm.setStartDate(ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.of("UTC")));
        filterForm.setEndDate(filterForm.getStartDate().plusDays(200));
        filterForm.setPageNo(1);
        filterForm.setPageSize(10);

        try {
            orderDto.getOrdersByFilter(filterForm);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("start date and end date cannot be more than 100 days apart", e.getMessage());
        }

        filterForm.setEndDate(filterForm.getStartDate().plusDays(50));
        assertEquals(1, orderDto.getOrdersByFilter(filterForm).size());
    }

    @Test
    public void testGetSalesReport() throws ApiException {
        List<BrandPojo> brandPojos = TestObjectUtils.getNewBrandPojoList();
        brandPojos.forEach(brandDao::insert);
        List<ProductPojo> productPojos = TestObjectUtils.getNewProductPojoList();
        productPojos.get(0).setBrandCategoryId(brandPojos.get(0).getId());
        productPojos.get(1).setBrandCategoryId(brandPojos.get(1).getId());
        productPojos.get(2).setBrandCategoryId(brandPojos.get(2).getId());
        productPojos.forEach(productDao::insert);

        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setOrderStatus(OrderStatus.INVOICED);
        orderDao.insert(orderPojo);

        productPojos.forEach(productPojo -> {
            productDao.insert(productPojo);
            orderItemDao.insert(getNewOrderItemPojo(orderPojo.getId(), productPojo.getId(), 1, 1.0));
        });

        SalesFilterForm filterForm = new SalesFilterForm();
        filterForm.setStartDate(ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.of("UTC")));
        filterForm.setEndDate(filterForm.getStartDate().plusDays(200));

        try {
            orderDto.getSalesReport(filterForm);
            fail("expected ApiException");
        } catch (ApiException e) {
            assertEquals("start date and end date cannot be more than 100 days apart", e.getMessage());
        }

        filterForm.setEndDate(filterForm.getStartDate().plusDays(1));
        assertEquals(3, orderDto.getSalesReport(filterForm).size());

        filterForm.setBrandName(brandPojos.get(0).getName());
        assertEquals(2, orderDto.getSalesReport(filterForm).size());

        filterForm.setCategory(brandPojos.get(0).getCategory());
        assertEquals(1, orderDto.getSalesReport(filterForm).size());

        filterForm.setBrandName(null);
        assertEquals(2, orderDto.getSalesReport(filterForm).size());
    }
}
