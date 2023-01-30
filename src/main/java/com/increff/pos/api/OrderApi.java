package com.increff.pos.api;

import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.entity.InvoicePojo;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.model.InvoiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderApi extends AbstractApi<OrderPojo> {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private InvoiceDao invoiceDao;

    public void getPdfBase64(Integer orderId, InvoiceData invoiceData) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiUrl = "http://localhost:8000/fop/api/invoice";
        RestTemplate RestTemplate = new RestTemplate();
        ResponseEntity<String> apiResponse = RestTemplate.postForEntity(apiUrl, invoiceData, String.class);
        String base64 = apiResponse.getBody();
        InvoicePojo invoicePojo = new InvoicePojo();
        invoicePojo.setOrderId(orderId);
        invoicePojo.setInvoiceLink(generateInvoicePdf(orderId, base64));
        invoiceDao.insert(invoicePojo);
    }

    public void add(OrderPojo orderPojo) {
        orderDao.insert(orderPojo);
    }


    public List<OrderPojo> get() {
        return orderDao.selectAll();
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
}
