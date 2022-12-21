package com.hbpu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.dto.DishDto;
import com.hbpu.reggie.entity.Category;
import com.hbpu.reggie.entity.Dish;
import com.hbpu.reggie.entity.DishFlavor;
import com.hbpu.reggie.service.CategoryService;
import com.hbpu.reggie.service.DishFlavorService;
import com.hbpu.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/page")
    public R<Page> getDishPage(int page,int pageSize,String name){
        return dishService.getDishPage(page,pageSize,name);
    }

    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("插入成功");
    }

    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        return dishService.updateDish(dishDto);
    }

    @GetMapping("/{id}")
    public R<DishDto> queryDishById(@PathVariable Long id){
        return dishService.queryDishById(id);
    }

    @PostMapping("/status/{status}")
    public R<String> dishStatusByStatus(@RequestParam List<Long> ids,@PathVariable int status){
//        log.info(ids.toString());
        for(long id : ids){
            Dish dish = dishService.getById(id);
            String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
            if(redisTemplate.opsForValue().get(key) != null)
                redisTemplate.delete(key);
            if(dish != null)  dish.setId(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("sdsdf");
    }

    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids){
        return dishService.deleteDish(ids);
    }

    @GetMapping("/list")
    public R<List<DishDto>> queryDishList(Dish dish){
        log.error(dish.toString());
        return dishService.queryDishList(dish);
    }


}
