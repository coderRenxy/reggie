package com.hbpu.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Orders;

import java.time.format.DateTimeFormatter;
import java.util.Date;

public interface OrdersService extends IService<Orders> {
    R<String> submitOrder(Orders orders);

    R<Page> getOrderPage(int page,int pageSize);

    R<Page> getOrderAllPage(int page, int pageSize, String number, Date beginTime, Date endTime);
}
