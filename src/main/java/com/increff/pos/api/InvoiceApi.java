package com.increff.pos.api;

import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.entity.InvoicePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

    public List<InvoicePojo> getByDate(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        if (ChronoUnit.DAYS.between(startDate, endDate) > maxDateRange) {
            throw new ApiException(String.format("start date and end date cannot be more than %d days apart", maxDateRange));
        }
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
