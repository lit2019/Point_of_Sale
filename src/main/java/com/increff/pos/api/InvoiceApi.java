package com.increff.pos.api;

import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.entity.InvoicePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
public class InvoiceApi extends AbstractApi {

    @Autowired
    private InvoiceDao dao;

    public void add(InvoicePojo invoicePojo) throws ApiException {
        validate(invoicePojo);
        dao.insert(invoicePojo);
    }

    public List<InvoicePojo> getByDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        return dao.selectByDate(startDate, endDate);
    }

    public InvoicePojo get(Integer orderId) {
        return dao.select(orderId);
    }

    private void validate(InvoicePojo invoicePojo) throws ApiException {
        checkNull(invoicePojo.getOrderId(), "order id cannot cannot be null");
        checkNull(invoicePojo.getInvoiceUrl(), "invoice url cannot cannot be null");
    }
}
