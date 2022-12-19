package com.hbpu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbpu.reggie.common.BaseContext;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Orders;
import com.hbpu.reggie.entity.ShoppingCart;
import com.hbpu.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController  {
    @Autowired
    OrdersService ordersService;

    @PostMapping("submit")
    public R<String> submitOrder(@RequestBody Orders orders){//备注，地址id，支付方式
        return ordersService.submitOrder(orders);
    }

    @GetMapping("/userPage")
    public R<Page> getOrderPage(int page,int pageSize){
        return ordersService.getOrderPage(page,pageSize);
    }

    @GetMapping("/page")
    public R<Page> getOrderAllPage(int page, int pageSize, String number, @DateTimeFormat(pattern="yyyy-MM-dd") Date beginTime, @DateTimeFormat(pattern="yyyy-MM-dd") Date endTime){
        return ordersService.getOrderAllPage(page,pageSize,number,beginTime,endTime);
    }

    @PutMapping
    public R<String> editOrderDetail(@RequestBody Orders orders){
        orders = ordersService.getById(orders.getId());
        orders.setStatus(orders.getStatus());
        ordersService.updateById(orders);
         return R.success("ok");
    }
}
