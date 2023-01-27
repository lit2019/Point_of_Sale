package com.increff.pos.api;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.model.InvoiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderApi {
    @Autowired
    private OrderDao dao;

    public static <T> String getPDFBase64(InvoiceData invoiceData) throws ApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiUrl = "http://localhost:8000/fop/api/invoice";
        RestTemplate RestTemplate = new RestTemplate();
        ResponseEntity<String> apiResponse = RestTemplate.postForEntity(apiUrl, invoiceData, String.class);
        String base64 = apiResponse.getBody();
        return base64;
    }

    public void add(OrderPojo orderPojo) {
        dao.insert(orderPojo);
    }

    public OrderPojo get(Integer orderId) {
        return dao.select(orderId);
    }

    public List<OrderPojo> get() {
        return dao.selectAll();
    }

    public void getInvoice(Integer orderId) {

    }
}
