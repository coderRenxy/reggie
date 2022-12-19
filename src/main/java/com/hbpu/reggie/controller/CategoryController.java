package com.hbpu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Category;
import com.hbpu.reggie.entity.Dish;
import com.hbpu.reggie.entity.Setmeal;
import com.hbpu.reggie.service.CategoryService;
import com.hbpu.reggie.service.DishService;
import com.hbpu.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;

    @PostMapping
    public R<String> addFoodCategory(@RequestBody Category category){
        log.info(category.toString());
        return categoryService.addFoodCategory(category);
    }

    @GetMapping("/page")
    public R<Page> getCategoryPage(int page,int pageSize){
        return categoryService.getCategoryPage(page,pageSize);
    }

    @PutMapping
    public R<String>  editCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> deleteCategory(Long id){//直接发送过来的参数直接按照顺序定义在这，路径里得要@PathVariable
        log.info("删除分类id：{}",id);
        return categoryService.deleteCategory(id);
//        if(dishService.deleteCategory(id) || setmealService.deleteCategory(id)
//            return R.error("删除失败");
//        else{
//            categoryService.removeById(id);
//            return R.success("删除成功");
//        }
    }

    @GetMapping("/list")
    public R<List<Category>> getCategoryList(Category category){
        return categoryService.getCategoryList(category);
    }

}
