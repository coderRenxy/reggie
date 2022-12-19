package com.hbpu.reggie.controller;

import com.hbpu.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/*
* 文件的上传和下载
* */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //原始文件名
        String originFileName = file.getOriginalFilename();
        String suffix = originFileName.substring(originFileName.lastIndexOf("."));
        //使用UUID重新生成文件名，防止文件名重复覆盖
        String fileName = UUID.randomUUID().toString()+suffix;
        File dir = new File(basePath);
        if(!dir.exists())//不存在时创建
            dir.mkdirs();
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info(file.toString());
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
        //输出流，通过输出流将文件写回浏览器，在浏览器展示图片(响应数据)
        ServletOutputStream outputStream = response.getOutputStream();

        //设置响应数据类型
        response.setContentType("image/jpeg");

        int len = 0;
        byte[] bytes = new byte[1024];
        //fileInputStream.read(bytes)读取数据个数
        while((len = fileInputStream.read(bytes))!=-1){
            //cbuf 数据，off 开始下标，len数据个数
            //write(byte[] cbuf, int off, int len)
            outputStream.write(bytes,0,len);
            //刷新输入流
            outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        fileInputStream.close();
    }

}
