package com.hbpu.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {//可以有多个额外的实现类
    public R<Employee> login(HttpServletRequest request,Employee employee);
    public R<String> logout(HttpServletRequest request);
    public R<String> addEmployee(HttpServletRequest request,Employee employee);
    public R<Page> getEmplyeeList(int page, int pageSize, String name);
    public R<Employee> editEmployeeStatus(Employee employee,HttpServletRequest request);

}
