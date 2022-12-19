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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        for(long id : ids){
            LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper();
            lqw.eq(SetmealDish::getSetmealId,id);
            setmealDishMapper.delete(lqw);//删除对应套餐下的所有菜品setmeal_dish
        }
        setmealMapper.deleteBatchIds(ids);
        return R.success("sdsd");
    }

    public R<List<Setmeal>> setmealListApi(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        lqw.orderByDesc(Setmeal::getUpdateTime);
        return R.success(setmealMapper.selectList(lqw));
    }

}
