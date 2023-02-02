package com.increff.pos.api;

import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.entity.InvoicePojo;
import com.increff.pos.model.SalesFilterForm;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

import static com.increff.pos.util.DateTimeUtil.getDateByString;

@Service
@Transactional(rollbackOn = ApiException.class)
public class InvoiceApi extends AbstractApi<InvoicePojo> {

    @Autowired
    private InvoiceDao dao;

    public void add(InvoicePojo invoicePojo) {
        dao.insert(invoicePojo);
    }

    public List<InvoicePojo> get(SalesFilterForm filterForm) throws ApiException {
        normalize(filterForm);
        return dao.selectByDate(getDateByString(filterForm.getStartDate()), getDateByString(filterForm.getEndDate()));
    }

    private void normalize(SalesFilterForm filterForm) {
        if (Objects.nonNull(filterForm.getBrandName())) {
            filterForm.setBrandName(StringUtil.normaliseText(filterForm.getBrandName()));
        }
        if (Objects.nonNull(filterForm.getCategory())) {
            filterForm.setCategory(StringUtil.normaliseText(filterForm.getCategory()));
        }
        if (StringUtil.isEmpty(filterForm.getStartDate())) {
            filterForm.setStartDate("2023-01-31");
        }
        if (StringUtil.isEmpty(filterForm.getEndDate())) {
            filterForm.setEndDate("9023-01-31");
        }
    }

}
