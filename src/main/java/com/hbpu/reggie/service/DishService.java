package com.hbpu.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.dto.DishDto;
import com.hbpu.reggie.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {
    boolean deleteCategory(Long id);

    R<Page> getDishPage(int page, int pageSize,String name);


    R<String> saveWithFlavor(DishDto dishDto);

    R<String> updateDish(DishDto dishDto);

    R<DishDto> queryDishById(Long id);

    R<String> deleteDish(List<Long> ids);

    R<List<DishDto>> queryDishList(Dish dish);
}
