package com.hbpu.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})//加了这两个注解的类出现异常都会被捕获到
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] res = ex.getMessage().split("'");
            return R.error(res[1]+"已存在");
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomExecption.class)
    public R<String> exceptionHandler(CustomExecption ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

}
