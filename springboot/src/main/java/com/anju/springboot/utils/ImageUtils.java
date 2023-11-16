package com.anju.springboot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.UUID;

public class ImageUtils {

    @Value("${anju.path}") // 在 application.properties 中配置上传文件的存储路径
    private static String path;

    /**
     * 客户端发送过来的图片到服务器，并且返回图片的名称
     * @return
     */
    public static String saveImage(MultipartFile multipartFile, HttpServletRequest request) {
        String fileName = null;
        try {
            //文件后缀名
            String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");

            //如果路径不存在，就创建一个
            File realPath = new File(path);
            if(!realPath.exists()){
                realPath.mkdir();
            }
            multipartFile.transferTo(new File(path+"/"+uuid+fileSuffix));
            fileName = uuid+fileSuffix;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
