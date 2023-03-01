package com.increff.pos.api;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.entity.OrderItemPojo;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.model.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
public class OrderApi extends AbstractApi {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Value("${app.maxDateRange}")
    private Integer maxDateRange;

    public void add(OrderPojo orderPojo, List<OrderItemPojo> orderItemPojos) throws ApiException {
        orderPojo.setOrderStatus(OrderStatus.CREATED);
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

    public OrderPojo getCheck(Integer id) {
        OrderPojo pojo = orderDao.select(id);
        String.format("order with Id : %d dose not exist", id);
        return pojo;
    }

    public List<OrderItemPojo> getOrderItemsByOrderIds(List<Integer> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return new ArrayList<>();
        }
        return orderItemDao.getByOrderIds(orderIds);
    }

    public List<OrderPojo> getByFilter(ZonedDateTime startDate, ZonedDateTime endDate, OrderStatus orderStatus) throws ApiException {
        endDate = endDate.plusDays(1);
        if (!startDate.isBefore(endDate)) throw new ApiException("Start Date must be Before End Date");
        if (ChronoUnit.DAYS.between(startDate, endDate) > maxDateRange)
            throw new ApiException(String.format("start date and end date cannot be more than %d days apart", maxDateRange));

        return orderDao.selectByFilter(startDate, endDate, orderStatus);
    }

    public List<OrderPojo> getByFilter(ZonedDateTime startDate, ZonedDateTime endDate, OrderStatus orderStatus, Integer pageNo, Integer pageSize) throws ApiException {

        endDate = endDate.plusDays(1);
        if (!startDate.isBefore(endDate)) throw new ApiException("Start Date must be Before End Date");
        if (ChronoUnit.DAYS.between(startDate, endDate) > maxDateRange)
            throw new ApiException(String.format("start date and end date cannot be more than %d days apart", maxDateRange));

        return orderDao.selectByFilter(startDate, endDate, orderStatus, pageNo, pageSize);
    }

    public void setStatus(Integer id, OrderStatus invoiced) {
        get(id).setOrderStatus(invoiced);
    }

    private void validate(OrderItemPojo orderItemPojo) throws ApiException {
        checkNull(orderItemPojo.getOrderId(), "OrderId cannot be null");
        checkNull(orderItemPojo.getQuantity(), "Quantity cannot be null");
        checkNull(orderItemPojo.getSellingPrice(), "Selling Price cannot be null");
        checkNull(orderItemPojo.getProductId(), "Product Id cannot be null");
    }
}