package com.anju.springboot;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ZhongZhen
 * @PackageName: com.example.myspringbootm
 * @ClassName: GeneratorApplicationTests
 * @Time: 2023/9/18 20:04
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootTest
public class GeneratorApplicationTests {

    public static void main(String[] args) {

        /*
            特别注意：生成的时间类型均为：“LocalDateTime”格式，需要假如以下注解方可正常使用
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")//用于指定日期/时间类型的属性在序列化为JSON字符串时的格式。
            @JsonDeserialize(using = LocalDateTimeDeserializer.class)//用于指定反序列化时使用的自定义类，LocalDateTimeDeserializer是一个实现了JsonDeserializer接口的类，可以将JSON字符串转换为LocalDateTime对象
            @JsonSerialize(using = LocalDateTimeSerializer.class)//用于指定序列化时使用的自定义类，LocalDateTimeSerializer是一个实现了JsonSerializer接口的类，可以将LocalDateTime对象转换为JSON字符串
        */


            //数据库连接
            String url = "jdbc:mysql://127.0.0.1:3306/anju?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false";//数据库url
            String username = "root";//账号
            String password = "123456";//密码
            //String module = "up";//模块名
            //全局配置参数
            String author = "ZhongZhen";//作者
            //String outputDir = System.getProperty("user.dir")+"\\"+module+"\\src\\main\\java";//指定输出目录
            String outputDir = System.getProperty("user.dir") + "\\src\\main\\java";//指定输出目录

            //包配置参数
            String parent = "com.anju";//父包名
            String moduleName = "springboot";//父包模块名
            String entity = "entity";//Entity 实体类包名
            String mapper = "mapper";//Mapper 包名
            String mapperXml = "mapper.xml";//Mapper XML 包名
            String service = "service";//Service 包名
            String serviceImpl = "service.impl";//Service Impl 包名
            String controller = "controller";//Controller 包名
            //要生成的数据库表
            List<String> tables = new ArrayList<>();
            tables.add("rent_order");

            //开始生成
            FastAutoGenerator.create(url, username, password)
                    //全局配置
                    .globalConfig(builder -> {
                        builder.author(author)
                                .outputDir(outputDir)
                                .commentDate("yyyy-MM-dd");//注释日期
                    })
                    //包配置
                    .packageConfig(builder -> {
                        builder.parent(parent)
                                .moduleName(moduleName)
                                .entity(entity)
                                .mapper(mapper)
                                .xml(mapperXml)
                                .service(service)
                                .serviceImpl(serviceImpl)
                                .controller(controller);
                    })
                    //策略配置
                    .strategyConfig(builder -> {
                        builder.addInclude(tables)
                                //开启生成实体类
                                .entityBuilder()
                                .enableLombok()//开启 lombok 模型
                                .enableTableFieldAnnotation()//开启生成实体时生成字段注解
                                //开启生成mapper
                                .mapperBuilder()
                                .enableBaseResultMap()//启用 BaseResultMap 生成
                                .superClass(BaseMapper.class)//设置父类
                                .enableMapperAnnotation()//开启 @Mapper 注解
                                .formatMapperFileName("%sMapper")//格式化 mapper 文件名称
                                .formatXmlFileName("%sMapper")//格式化 xml 实现类文件名称
                                //开启生成service及impl
                                .serviceBuilder()
                                .formatServiceFileName("%sService")//格式化 service 接口文件名称
                                .formatServiceImplFileName("%sServiceImpl")//格式化 service 实现类文件名称
                                //开启生成controller
                                .controllerBuilder()
                                // 映射路径使用连字符格式，而不是驼峰
                                .enableHyphenStyle()
                                .formatFileName("%sController")//格式化文件名称
                                .enableRestStyle();
                        ;
                    })
                    .templateEngine(new VelocityTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                    //.templateConfig(builder -> builder.controller(""))//关闭生成controller
                    .execute();
        }
    }

