package com.hbpu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hbpu.reggie.common.CustomExecption;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Category;
import com.hbpu.reggie.entity.Dish;
import com.hbpu.reggie.entity.Setmeal;
import com.hbpu.reggie.mapper.CategoryMapper;
import com.hbpu.reggie.service.CategoryService;
import com.hbpu.reggie.service.DishService;
import com.hbpu.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    SetmealService setmealService;
    @Autowired
    DishService dishService;

    public R<String> addFoodCategory(Category category){
        categoryMapper.insert(category);
        return R.success("新增菜品分类成功");
    }

    public R<Page> getCategoryPage(int page, int pageSize){
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort);
        Page pageInfo = new Page(page,pageSize);
        categoryMapper.selectPage(pageInfo,lqw);
        return R.success(pageInfo);
    }

    public R<String> deleteCategory(Long id){
        LambdaQueryWrapper<Dish> lqw_dish = new LambdaQueryWrapper<>();
        lqw_dish.eq(Dish::getCategoryId,id);
        LambdaQueryWrapper<Setmeal> lqw_Setmeal = new LambdaQueryWrapper<>();
        lqw_Setmeal.eq(Setmeal::getCategoryId,id);
        //关联了菜品
        if(dishService.count(lqw_dish) > 0){
            throw new CustomExecption("当下关联了菜品，不能删除");
        }
        //关联了套餐
        if(setmealService.count(lqw_Setmeal) > 0){
            throw new CustomExecption("当下关联了套餐，不能删除");
        }
        //正常删除
        super.removeById(id);
        return R.success("删除成功");
    }

    public R<List<Category>> getCategoryList(Category category){
        LambdaQueryWrapper<Category> lwq = new LambdaQueryWrapper();
        lwq.eq(category.getType() != 0,Category::getType,category.getType());
        lwq.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryMapper.selectList(lwq);
        return R.success(list);
    }
}
