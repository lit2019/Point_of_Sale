package com.increff.pos.api;

import com.increff.pos.dao.InvoiceDao;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.entity.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderApi extends AbstractApi<OrderPojo> {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private InvoiceDao invoiceDao;


    public void add(OrderPojo orderPojo) {
        orderDao.insert(orderPojo);
    }


    public List<OrderPojo> get() {
        return orderDao.selectAll();
    }


}
