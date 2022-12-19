package com.hbpu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Setmeal;
import com.hbpu.reggie.entity.SetmealDish;
import com.hbpu.reggie.mapper.SetmealDishMapper;
import com.hbpu.reggie.mapper.SetmealMapper;
import com.hbpu.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
    @Autowired
    SetmealMapper setmealMapper;



}
