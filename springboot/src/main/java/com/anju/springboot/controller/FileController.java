package com.anju.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.anju.springboot.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;


/**
 * @Author: ZhongZhen
 * @PackageName: com.anju.springboot.controller
 * @ClassName: FileController
 * @Time: 2023/9/20 23:26
 * @Description: TODO
 * @Version: 1.0
 */
@Log4j2
@RestController
@CrossOrigin
public class FileController {

    @Value("${anju.path}") // 在 application.properties 中配置上传文件的存储路径
    private String path;

    /**
     * 上传文件
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        log.info("开始上传图片");
        String fileName = null;
        try {
            //文件后缀名
            String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            //上传路径保存设置

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
        return Result.success(fileName);
    }

    /**
     * 获取文件
     */
    @GetMapping("/files/{flag}")
    public void avatarPath(@PathVariable String flag, HttpServletResponse response) {
        if (!FileUtil.isDirectory(path)) {
            FileUtil.mkdir(path);
        }
        OutputStream os;
        List<String> fileNames = FileUtil.listFileNames(path);
        String avatar = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        try {
            if (StrUtil.isNotEmpty(avatar)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(avatar, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(path + avatar);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }
}
