package com.hbpu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hbpu.reggie.common.BaseContext;
import com.hbpu.reggie.common.CustomExecption;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.*;
import com.hbpu.reggie.mapper.*;
import com.hbpu.reggie.service.OrderDetailService;
import com.hbpu.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    OrderDetailService orderDetailService;

    public R<String> submitOrder(Orders orders){
        //获取当前用户id
        long userId = BaseContext.getCurrentId();
        //查询购物车
        LambdaQueryWrapper<ShoppingCart> lwq = new LambdaQueryWrapper<>();
        lwq.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartMapper.selectList(lwq);
        if(list == null || list.size() == 0){
            throw new CustomExecption("空购物车请求");
        }
        //插入 orders 一条数据，order_detail 多条数据
        User user = userMapper.selectById(userId);
        AddressBook addressBook = addressBookMapper.selectById(orders.getAddressBookId());
        if(addressBook == null) throw new CustomExecption("用户地址有误");

        long id = IdWorker.getId();//生成订单id

        //遍历购物车
        AtomicInteger amount = new AtomicInteger(0);//原子整数类
        List<OrderDetail> list1 = new ArrayList<>();
        for(ShoppingCart shoppingCart : list){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setNumber(shoppingCart.getNumber());
            BeanUtils.copyProperties(shoppingCart,orderDetail,"id","userId");
            list1.add(orderDetail);
            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
        }

        orders.setId(id);
        orders.setNumber(String.valueOf(id));
        orders.setUserId(userId);//下单用户id
        orders.setUserName(user.getName());//下单用户名字
        orders.setConsignee(addressBook.getConsignee());
        orders.setStatus(2);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        ordersMapper.insert(orders);
        orderDetailService.saveBatch(list1);

        //清空购物车
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartMapper.delete(lqw);
        return R.success("ok");
    }

    @Override
    public R<Page> getOrderPage(int page,int pageSize) {
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId,BaseContext.getCurrentId());
        ordersMapper.selectPage(pageInfo,lqw);
        return R.success(pageInfo);
    }

    @Override
    public R<Page> getOrderAllPage(int page, int pageSize, String number, Date beginTime, Date endTime) {
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.like(number != null,Orders::getNumber,number);
        lqw.between(beginTime != null && endTime != null,Orders::getCheckoutTime,beginTime,endTime);
        lqw.orderByDesc(Orders::getCheckoutTime);
        ordersMapper.selectPage(pageInfo,lqw);
        return R.success(pageInfo);
    }

}
