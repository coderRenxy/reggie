package com.hbpu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.dto.DishDto;
import com.hbpu.reggie.entity.Dish;
import com.hbpu.reggie.entity.DishFlavor;
import com.hbpu.reggie.mapper.CategoryMapper;
import com.hbpu.reggie.mapper.DishFlavorMapper;
import com.hbpu.reggie.mapper.DishMapper;
import com.hbpu.reggie.service.DishFlavorService;
import com.hbpu.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishMapper dishMapper;

    @Autowired
    DishFlavorMapper dishFlavorMapper;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    RedisTemplate redisTemplate;

    public boolean deleteCategory(Long id){
        LambdaQueryWrapper<Dish> lqw_dish = new LambdaQueryWrapper<>();
        lqw_dish.eq(Dish::getCategoryId,id);
        List<Dish> list_dish = dishMapper.selectList(lqw_dish);
        for(Dish dish : list_dish)
            if(dish.getIsDeleted() == 0)
                return true;
        return false;
    }



    public R<Page> getDishPage(int page, int pageSize,String name){
        Page pageInfo = new Page(page,pageSize);
        Page pageFlavorInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Dish> lwq = new LambdaQueryWrapper<>();
        lwq.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        lwq.orderByAsc(Dish::getUpdateTime);
        dishMapper.selectPage(pageInfo,lwq);
        //对象拷贝--分页信息拷贝
        BeanUtils.copyProperties(pageInfo,pageFlavorInfo,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new ArrayList<>();

        for(Dish dish : records){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            dishDto.setCategoryName(categoryMapper.selectById(dish.getCategoryId()).getName());
            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DishFlavor::getDishId,dish.getId());
            dishDto.setFlavors(dishFlavorMapper.selectList(lqw));
            list.add(dishDto);
        }
        pageFlavorInfo.setRecords(list);
        return R.success(pageFlavorInfo);
    }

    @Transactional
    public R<String> saveWithFlavor(DishDto dishDto){
        String key = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        //如果缓存有，需要删除缓存，再 save
        if(redisTemplate.opsForValue().get(key) != null)
            redisTemplate.delete(key);
        //如果缓存没有，只需要 save
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> list = dishDto.getFlavors();
        for(DishFlavor ll : list){
            ll.setDishId(dishId);
        }
        if(list != null && list.size() != 0)
            dishFlavorService.saveBatch(list);
        return R.success("茶");
    }

    @Transactional
    public R<String> updateDish(DishDto dishDto){
        String key = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        //如果缓存有，需要删除缓存，再 update
        if(redisTemplate.opsForValue().get(key) != null)
            redisTemplate.delete(key);
        //如果缓存没有，只需要 update
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish,"categoryName","copies","flavors");
        this.updateById(dish);
        Long dishId = dishDto.getId();
        List<DishFlavor> list = dishDto.getFlavors();//现在的新口味
        //根据 dishId 查口味并 删除
        List<Long> ids = new ArrayList<>();
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> temp = dishFlavorMapper.selectList(lqw);
        for(DishFlavor dishFlavor : temp)
            ids.add(dishFlavor.getId());
        for (DishFlavor dishFlavor : list) {
            dishFlavor.setDishId(dishId);
        }
        if(ids != null && ids.size() != 0)
            dishFlavorMapper.deleteBatchIds(ids);
        if(list != null && list.size() != 0)
            dishFlavorService.saveBatch(list);
        return R.success("成功");
    }

    public R<DishDto> queryDishById(Long id){
        DishDto dishDto = new DishDto();
        Dish dish = dishMapper.selectById(id);
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dish.getId());
        dishDto.setFlavors(dishFlavorMapper.selectList(lqw));
        dishDto.setCategoryName(categoryMapper.selectById(dish.getCategoryId()).getName());
        return R.success(dishDto);
    }

    @Transactional
    public R<String> deleteDish(List<Long> ids){
        List<Long> list = new ArrayList<>();
        LambdaQueryWrapper<DishFlavor> lwq = new LambdaQueryWrapper<>();
        for(long id : ids){
            lwq.eq(DishFlavor::getDishId,id);
            //如果缓存有，需要删除缓存
            Dish dish = dishMapper.selectById(id);
            String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
            if(redisTemplate.opsForValue().get(key) != null)
                redisTemplate.delete(key);
            dishFlavorMapper.delete(lwq);
        }
        this.removeByIds(ids);
        return R.success("成");
    }

    public R<List<DishDto>> queryDishList(Dish dish){
//        long categoryId = dish.getCategoryId();
//        String name = dish.getName();
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //如果 redis 存在该 list，直接返回
        List<DishDto> dishDtoList = null;
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishDtoList != null)    return R.success(dishDtoList);
        //redis 不存在则 查询并放入 redis 并返回。
        LambdaQueryWrapper<Dish> lwq = new LambdaQueryWrapper<>();
        lwq.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        lwq.like(dish.getName() != null,Dish::getName,dish.getName());
        lwq.eq(Dish::getStatus,1);
        lwq.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishMapper.selectList(lwq);
        dishDtoList = new ArrayList<>();
        for (Dish dd : dishList){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dd,dishDto);
            LambdaQueryWrapper<DishFlavor> ll = new LambdaQueryWrapper<>();
            ll.eq(DishFlavor::getDishId,dd.getId());
            dishDto.setFlavors(dishFlavorMapper.selectList(ll));
            //dishDto.setCategoryName(categoryMapper.selectById(dd.getCategoryId()).getName());
            dishDtoList.add(dishDto);
        }
        redisTemplate.opsForValue().set(key,dishDtoList);
        return R.success(dishDtoList);
    }


}
