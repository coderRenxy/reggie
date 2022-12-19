package com.hbpu.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    R<String> addFoodCategory(Category category);

    R<Page> getCategoryPage(int page, int pageSize);

    R<String> deleteCategory(Long id);

    R<List<Category>> getCategoryList(Category category);
}
