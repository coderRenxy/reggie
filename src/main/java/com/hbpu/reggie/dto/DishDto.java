package com.hbpu.reggie.dto;

import com.hbpu.reggie.entity.Dish;
import com.hbpu.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data//1、@Data可以为类提供读写功能,从而不用写get、set方法。 2、会为类提供 equals()、hashCode()、toString() 方法。
public class DishDto extends Dish {

    private String categoryName;

    private Integer copies;

    private List<DishFlavor>  flavors = new ArrayList<>();
}
