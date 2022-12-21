package com.hbpu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.dto.DishDto;
import com.hbpu.reggie.dto.DishDto1;
import com.hbpu.reggie.dto.SetmealDto;
import com.hbpu.reggie.entity.Dish;
import com.hbpu.reggie.entity.Setmeal;
import com.hbpu.reggie.entity.SetmealDish;
import com.hbpu.reggie.service.DishService;
import com.hbpu.reggie.service.SetmealDishService;
import com.hbpu.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealDishService setmealDishService;
    @Autowired
    SetmealService setmealService;
    @Autowired
    DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/page")
    public R<Page> getSetmealPage(int page,int pageSize,String name){
        return setmealService.getSetmealPage(page,pageSize,name);
    }

    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto){
        return setmealService.addSetmeal(setmealDto);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> querySetmealDtoById(@PathVariable long id){
        return setmealService.querySetmealDtoById(id);
    }

    @PutMapping
    public R<String> editSetmealDto(@RequestBody SetmealDto setmealDto){
        return setmealService.editSetmealDto(setmealDto);
    }

    @PostMapping("/status/{status}")
    public R<String> SetmealStatusByStatus(@RequestParam List<Long> ids,@PathVariable int status){
        log.info(ids.toString());
        for(long id : ids){
            Setmeal setmeal = setmealService.getById(id);
            String key = "setmeal_"+setmeal.getCategoryId()+"_"+status;
            if(redisTemplate.opsForValue().get(key) != null)
                redisTemplate.delete(key);
            if(setmeal == null)    setmeal.setId(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("sdsdf");
    }

    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids){
        return setmealService.deleteSetmeal(ids);
    }

    @GetMapping("/list")
    public R<List<Setmeal>> setmealListApi(Setmeal setmeal){
        return  setmealService.setmealListApi(setmeal);
    }

    @GetMapping("/dish/{id}")
    public R<List<DishDto1>> setMealDishDetailsApi(@PathVariable long id){
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<DishDto1> dishes = new ArrayList<>();
        List<SetmealDish> setmealDishes = setmealDishService.list(lqw);
        for(SetmealDish setmealDish : setmealDishes){
            Dish dish = dishService.getById(setmealDish.getDishId());
            DishDto1 dishDto1 = new DishDto1();
            BeanUtils.copyProperties(dish,dishDto1);
            dishDto1.setCopies(setmealDish.getCopies());
            dishes.add(dishDto1);
        }
        return R.success(dishes);
    }
}
