package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.api.DailySalesApi;
import com.increff.pos.entity.DailySalesPojo;
import com.increff.pos.model.DailySalesData;
import com.increff.pos.model.DailySalesFilterForm;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DailySalesDto {

    @Autowired
    private DailySalesApi dailySalesApi;

    public List<DailySalesData> getByFilter(DailySalesFilterForm filterForm) throws ApiException {
        ValidationUtil.validate(filterForm);
        return convert(dailySalesApi.getByDateRange(filterForm.getStartDate(), filterForm.getEndDate()));
    }

    private List<DailySalesData> convert(List<DailySalesPojo> dailySalesPojos) {
        ArrayList<DailySalesData> dailySalesDataList = new ArrayList<>();
        dailySalesPojos.forEach(pojo -> {
            DailySalesData salesData = new DailySalesData();
            salesData.setDate(pojo.getDate());
            salesData.setTotalRevenue(pojo.getTotalRevenue());
            salesData.setInvoicedItemsCount(pojo.getInvoicedItemsCount());
            salesData.setInvoicedOrdersCount(pojo.getInvoicedOrdersCount());
            dailySalesDataList.add(salesData);
        });
        return dailySalesDataList;
    }
}
