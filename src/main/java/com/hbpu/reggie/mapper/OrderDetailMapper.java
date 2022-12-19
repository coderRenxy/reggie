package com.hbpu.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hbpu.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}
