//package com.increff.pos.dao;
//
//import com.increff.pos.entity.ProductPojo;
//import com.increff.pos.spring.AbstractUnitTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//
//import static com.increff.pos.util.TestObjectUtils.getNewProductPojo;
//import static org.junit.Assert.assertEquals;
//
//public class ProductDaoTest extends AbstractUnitTest {
//
//    @Autowired
//    private ProductDao dao;
//
//    @Test
//    public void testSelectByBarcodes() {
//        dao.insert(getNewProductPojo(3, "name1", "barcode1", 12.0));
//        dao.insert(getNewProductPojo(3, "name2", "barcode2", 12.0));
//        dao.insert(getNewProductPojo(3, "name3", "barcode3", 12.0));
//
//        List<String> barcodes = Arrays.asList("barcode1", "barcode2", "barcode3");
//        List<ProductPojo> productPojos = dao.selectByBarcodes(barcodes);
//        assertEquals(3, productPojos.size());
//
//        HashSet<String> barcodeSet = new HashSet<>(barcodes);
//        for (ProductPojo pojo : productPojos) {
//            assertEquals(true, barcodeSet.contains(pojo.getBarcode()));
//        }
//    }
//}