package com.increff.pos.api;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.entity.OrderItemPojo;
import com.increff.pos.entity.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = Exception.class)
public class OrderApi extends AbstractApi {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;

    public void add(OrderPojo orderPojo, List<OrderItemPojo> orderItemPojos) throws ApiException {
        UploadLimit.checkSize(orderItemPojos.size());
        orderPojo.setInvoicedStatus(false);
        orderDao.insert(orderPojo);

        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            orderItemPojo.setOrderId(orderPojo.getId());
            validate(orderItemPojo);
            orderItemDao.insert(orderItemPojo);
        }
    }

    public OrderPojo get(Integer id) {
        return orderDao.select(id);
    }

    public List<OrderItemPojo> getOrderItemsByOrderIds(List<Integer> orderIds) {
        return orderItemDao.getByOrderIds(orderIds);
    }

    public OrderPojo getCheck(Integer orderId) throws ApiException {
        OrderPojo pojo = get(orderId);
        if (Objects.isNull(pojo)) {
            throw new ApiException(String.format("order with given id:%s dose not exist", orderId));
        }
        return pojo;
    }

    public List<OrderPojo> getByDate(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        if (ChronoUnit.DAYS.between(startDate, endDate) > 100) {
            throw new ApiException("start date and end date cannot be more than 100 days apart");
        }
        return orderDao.selectByDate(startDate, endDate);
    }

    private void validate(OrderItemPojo orderItemPojo) throws ApiException {
        checkNull(orderItemPojo.getOrderId(), "OrderId cannot be null");
        checkNull(orderItemPojo.getQuantity(), "Quantity cannot be null");
        checkNull(orderItemPojo.getSellingPrice(), "Selling Price cannot be null");
        checkNull(orderItemPojo.getProductId(), "Product Id cannot be null");
    }
}
