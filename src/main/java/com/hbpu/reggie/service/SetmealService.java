package com.hbpu.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.dto.DishDto;
import com.hbpu.reggie.dto.SetmealDto;
import com.hbpu.reggie.entity.Dish;
import com.hbpu.reggie.entity.Setmeal;
import com.hbpu.reggie.entity.SetmealDish;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    R<SetmealDto> querySetmealDtoById(long id);

    boolean deleteCategory(Long id);

    R<String> addSetmeal(SetmealDto setmealDto);

    R<Page> getSetmealPage(int page, int pageSize, String name);

    R<String> editSetmealDto(SetmealDto setmealDto);

    R<String> deleteSetmeal(List<Long> ids);

    R<List<Setmeal>> setmealListApi(Setmeal setmeal);
}
