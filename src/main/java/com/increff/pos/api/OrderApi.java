package com.increff.pos.api;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.entity.OrderItemPojo;
import com.increff.pos.entity.OrderPojo;
import com.increff.pos.model.OrderFilterForm;
import com.increff.pos.model.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
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
//        TODO make seperate methods in each api for checking upload limit
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

    public List<OrderItemPojo> getOrderItemsByOrderIds(List<Integer> orderIds) {
//        TODO return empty list if input is empty
        if (CollectionUtils.isEmpty(orderIds)) {
            return new ArrayList<>();
        }
        return orderItemDao.getByOrderIds(orderIds);
    }

    public List<OrderPojo> getByFilter(OrderFilterForm filterForm) throws ApiException {
//        TODO move endDate.plusdays() logic to ui
        if (ChronoUnit.DAYS.between(filterForm.getStartDate(), filterForm.getEndDate()) > maxDateRange)
            throw new ApiException(String.format("start date and end date cannot be more than %d days apart", maxDateRange));

        return orderDao.selectByFilter(filterForm.getStartDate(), filterForm.getEndDate(), filterForm.getOrderStatus());
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
