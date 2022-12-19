package com.hbpu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.Employee;
import com.hbpu.reggie.mapper.EmployeeMapper;
import com.hbpu.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
    //要额外自己实现的方法？

    @Autowired
    private EmployeeMapper employeeMapper;

    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /* @RequestBody 将 json对象转化为 Java对象 ， request 请求到id存到 session 一份*/

        /**
         * 1、将页面提交的密码password进行md5加密处理
         * 2、根据页面提交的用户名username查询数据库
         * 3、如果没有查询到则返回登灵失败结果
         * 4、密码比对，如果不一致则返回登录失败结果
         * 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         * 6、登录成功，将员工id存入Session并返回登录成功结果
         */

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());//Employee::getUsername就相当于创建一个Employee对象并调用其getUsername方法
        Employee emp =  employeeMapper.selectOne(queryWrapper);

        //3、如果没有查询到则返回登灵失败结果
        if(emp == null){
            return R.error("账号错误");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!password.equals(emp.getPassword())){
            return R.error("密码错误");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
        //注意：单纯返回用户名错误或密码错误都会出现枚举漏洞，别人会暴力破解你的登录，直接返回个登录失败才不会让人知晓究竟是密码错误还是账号就对不上。
    }

    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登灵员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    public R<String> addEmployee(HttpServletRequest request,Employee employee){
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //Employee::getUsername就相当于创建一个Employee对象并调用其getUsername方法
//        int a =  employeeMapper.selectCount(queryWrapper);
//        log.info("插入之前：{}",a);
        employee.setStatus(1);
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser((Long)(request.getSession().getAttribute("employee")));
//        employee.setUpdateUser((Long)(request.getSession().getAttribute("employee")));
        employeeMapper.insert(employee);
//        int b = employeeMapper.selectCount(queryWrapper);
//        log.info("插入之后：{}",b);
//        if(a == b)
//            return R.error("新增失败");
//        else
            return R.success("新增成功");
    }

    public R<Page> getEmplyeeList(int page, int pageSize, String name){
//        List<Employee> list = employeeMapper.selectList();
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构建条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //添加查询条件
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);//条件为真 才添加条件(Employee::getName 代表 Employee的 name 字段)
        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeMapper.selectPage(pageInfo,lqw);
        return R.success(pageInfo);
    }

    public R<Employee> editEmployeeStatus(Employee employee,HttpServletRequest request){
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeMapper.updateById(employee);
        return R.success(employee);
    }

}
