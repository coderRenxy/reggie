package com.hbpu.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hbpu.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
