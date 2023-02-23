package com.increff.pos.api;

import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.entity.InvoicePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class InvoiceApi extends AbstractApi {

    @Autowired
    private InvoiceDao dao;
    @Value("${app.maxDateRange}")
    private Integer maxDateRange;

    public void add(InvoicePojo invoicePojo) throws ApiException {
        validate(invoicePojo);
        dao.insert(invoicePojo);
    }

    public InvoicePojo get(Integer orderId) {
        return dao.select(orderId);
    }

    private void validate(InvoicePojo invoicePojo) throws ApiException {
        checkNull(invoicePojo.getOrderId(), "order id cannot cannot be null");
        checkNull(invoicePojo.getInvoiceUrl(), "invoice url cannot cannot be null");
    }
}
