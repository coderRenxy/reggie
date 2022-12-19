package com.hbpu.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hbpu.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component//没加这个注解在 @Autowired 进 serviceImpl 时会报错。
public interface DishMapper extends BaseMapper<Dish> {
}
