package com.hbpu.reggie.dto;

import com.hbpu.reggie.entity.Dish;
import lombok.Data;

import java.io.Serializable;

@Data
public class DishDto1 extends Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer copies;


}
