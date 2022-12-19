package com.hbpu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hbpu.reggie.common.BaseContext;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Setmeal;
import com.hbpu.reggie.entity.ShoppingCart;
import com.hbpu.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> cartListApi(){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        return R.success(shoppingCartService.list(lqw));
    }

    @PostMapping("/add")
    public R<String> addShoppingCart(@RequestBody ShoppingCart shoppingCart){
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shoppingCart.getDishId() != null,ShoppingCart::getDishId,shoppingCart.getDishId());
        lqw.eq(shoppingCart.getSetmealId() != null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
//        lqw.eq(shoppingCart.getDishFlavor() != null,ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());//不同口味的算作不同单子,不可以会让减少的时候不对称，因为减少只发送菜品或套餐id，并不会发送口味。
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(lqw);
        if(shoppingCart1 != null){
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartService.updateById(shoppingCart1);
        }else
            shoppingCartService.save(shoppingCart);
         return R.success("ok");
    }

//    @DeleteMapping("clean")
//    public R<String>

    @PostMapping("/sub")
    public R<String> subShoppingCart(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shoppingCart.getSetmealId() != null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        lqw.eq(shoppingCart.getDishId() != null,ShoppingCart::getDishId,shoppingCart.getDishId());
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCart = shoppingCartService.getOne(lqw);
        if(shoppingCart.getNumber() > 1){
            shoppingCart.setNumber(shoppingCart.getNumber()-1);
            shoppingCartService.updateById(shoppingCart);
        }else
            shoppingCartService.removeById(shoppingCart.getId());
        return R.success("ok");
    }

    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart(){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lqw);
        return R.success("ok");
    }

}
