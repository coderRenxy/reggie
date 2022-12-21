package com.hbpu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.dto.SetmealDto;
import com.hbpu.reggie.entity.Category;
import com.hbpu.reggie.entity.Dish;
import com.hbpu.reggie.entity.Setmeal;
import com.hbpu.reggie.entity.SetmealDish;
import com.hbpu.reggie.mapper.CategoryMapper;
import com.hbpu.reggie.mapper.SetmealDishMapper;
import com.hbpu.reggie.mapper.SetmealMapper;
import com.hbpu.reggie.service.SetmealDishService;
import com.hbpu.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishService setmealDishService;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    RedisTemplate redisTemplate;

    public boolean deleteCategory(Long id){
        LambdaQueryWrapper<Setmeal> lqw_Setmeal = new LambdaQueryWrapper<>();
        lqw_Setmeal.eq(Setmeal::getCategoryId,id);
        List<Setmeal> list_setmeal = setmealMapper.selectList(lqw_Setmeal);
        for(Setmeal setmeal : list_setmeal)
            if(setmeal.getIsDeleted() == 0)
                return true;
        return false;
    }

    @Transactional
    public R<String> addSetmeal(SetmealDto setmealDto){
        String key = "setmeal_"+setmealDto.getCategoryId()+"_"+setmealDto.getStatus();
        //如果缓存有，需要删除缓存，再 save
        if(redisTemplate.opsForValue().get(key) != null)
            redisTemplate.delete(key);
        //如果缓存没有，只需要 save
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        for(SetmealDish setmealDish : list){//dish插入setmealDish
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(list);
        return R.success("sd");
    }

    public R<Page> getSetmealPage(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null,Setmeal::getName,name);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        setmealMapper.selectPage(pageInfo,lqw);
        Page<SetmealDto> pageDtoInfo = new Page<>(page,pageSize);
        BeanUtils.copyProperties(pageInfo,pageDtoInfo,"records");
        List<Setmeal> listOrigin = pageInfo.getRecords();
        List<SetmealDto> listRes = new ArrayList<>();
        for(Setmeal setmeal : listOrigin){
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            setmealDto.setCategoryName(categoryMapper.selectById(setmeal.getCategoryId()).getName());
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
            setmealDto.setSetmealDishes(setmealDishMapper.selectList(lambdaQueryWrapper));
            listRes.add(setmealDto);
        }
        pageDtoInfo.setRecords(listRes);
        return R.success(pageDtoInfo);
    }

    public R<SetmealDto> querySetmealDtoById(long id){
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = setmealMapper.selectById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setCategoryName(categoryMapper.selectById(setmeal.getCategoryId()).getName());
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        setmealDto.setSetmealDishes(setmealDishMapper.selectList(lambdaQueryWrapper));
        return R.success(setmealDto);
    }

    @Transactional
    public R<String> editSetmealDto(SetmealDto setmealDto){
        String key = "setmeal_"+setmealDto.getCategoryId()+"_"+setmealDto.getStatus();
        //如果缓存有，需要删除缓存，再 update
        if(redisTemplate.opsForValue().get(key) != null)
            redisTemplate.delete(key);
        //如果缓存没有，只需要 update
        setmealMapper.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> lwq = new LambdaQueryWrapper<>();
        long setmealId = setmealDto.getId();
        lwq.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishMapper.delete(lwq);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        for(SetmealDish setmealDish : list){
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(list);
        return R.success("ok");
    }

    public R<String> deleteSetmeal(List<Long> ids){
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper();
        for(long id : ids){
            lqw.eq(SetmealDish::getSetmealId,id);
            //如果缓存有，需要删除缓存
            Setmeal setmeal = setmealMapper.selectById(id);
            String key = "setmeal_"+setmeal.getCategoryId()+"_"+setmeal.getStatus();
            if(redisTemplate.opsForValue().get(key) != null)
                redisTemplate.delete(key);
            setmealDishMapper.delete(lqw);//删除对应套餐下的所有菜品setmeal_dish
        }
        setmealMapper.deleteBatchIds(ids);
        return R.success("sdsd");
    }

    public R<List<Setmeal>> setmealListApi(Setmeal setmeal){
        String key = "setmeal_"+setmeal.getCategoryId()+"_"+setmeal.getStatus();
        //如果 redis 存在该 list，直接返回
        List<Setmeal> list = null;
        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);
        if(list != null)    return R.success(list);
        //redis 不存在则 查询并放入 redis 并返回。
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        lqw.orderByDesc(Setmeal::getUpdateTime);
        list = setmealMapper.selectList(lqw);
        redisTemplate.opsForValue().set(key,list,60, TimeUnit.MINUTES);
        return R.success(list);
    }

}
