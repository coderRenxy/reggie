package com.hbpu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hbpu.reggie.common.R;
import com.hbpu.reggie.entity.User;
import com.hbpu.reggie.service.UserService;
import com.hbpu.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")//发送邮箱验证码
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取邮箱地址
        String mail = user.getPhone();
        if(StringUtils.isNotEmpty(mail)) {
            //生成验证码
            String code =  ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}",code);
            //调用api
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //保存验证码对比
            //session.setAttribute(mail,code);
            //将验证码保存到 redis 中并设置有效期为5分钟
            redisTemplate.opsForValue().set(mail,code,5, TimeUnit.MINUTES);
            return R.success("手机短信验证码发送成功");
        }
        return null;
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody Map map,HttpSession session){
        //获取手机号
        Object phone = map.get("phone");
        //获取验证码
        String code = map.get("code").toString();
        //从session获取保存的验证码
        //String pp = session.getAttribute(phone).toString();
        String pp = redisTemplate.opsForValue().get(phone).toString();
        //进行验证码比对 页面提交的和生成的
        if (pp != null && pp.equals(code)){//phone就是一个字符串
            LambdaQueryWrapper<User> lwq = new LambdaQueryWrapper<>();
            lwq.eq(User::getPhone,phone.toString());
            User user;
            user = userService.getOne(lwq);
            if(user == null) {
                user = new User();
                user.setPhone(phone.toString());
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //用户登录成功，可以删除该条验证码
            redisTemplate.delete(phone);
            return R.success("成功了");
        }
        return R.error("异常");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        session.removeAttribute("user");
        return R.success("推出成功");
    }
}
