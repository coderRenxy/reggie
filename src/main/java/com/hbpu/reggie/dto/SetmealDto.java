package com.hbpu.reggie.dto;


import com.hbpu.reggie.entity.Setmeal;
import com.hbpu.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
