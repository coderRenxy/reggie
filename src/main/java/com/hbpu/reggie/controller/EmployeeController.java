package com.hbpu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Employee;
import com.hbpu.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController  /*相当于 @controller 和 @ResponseBody */
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login") /* 相当于 @RequestMapping 指定 method=post */
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        return employeeService.login(request,employee);
        //注意：单纯返回用户名错误或密码错误都会出现枚举漏洞，别人会暴力破解你的登录，直接返回个登录失败才不会让人知晓究竟是密码错误还是账号就对不上。
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        return employeeService.logout(request);
    }

    @PostMapping("/addEmployee")//也可以不要，只要在前端请求路径也改了就好了，但是同一个路径不能都是同类型的请求（post、get）？
    public R<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        return employeeService.addEmployee(request,employee);
    }

    @GetMapping("/page")
    public R<Page> getEmplyeeList(int page, int pageSize, String name){
//        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);
        return employeeService.getEmplyeeList(page,pageSize,name);
    }

    @PutMapping
    public R<Employee> editEmployeeStatus(@RequestBody Employee employee,HttpServletRequest request){
        return  employeeService.editEmployeeStatus(employee,request);
    }

    @GetMapping("/{id}")
    public  R<Employee> getEmployeeDetail(@PathVariable Long id){
        Employee emp = employeeService.getById(id);
        if(emp != null)
            return R.success(emp);
        else
            return R.error("未知错误");
    }

    @PutMapping("/editEmployee")
    public  R<String> editEmployee(@RequestBody Employee employee,HttpServletRequest request){
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("成功插入");
    }

}
