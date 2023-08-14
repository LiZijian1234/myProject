package com.atguigu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zijianLi
 * @create 2023- 03- 13- 19:50
 */
//Spring Boot 启动类 扫描 Mapper 文件夹：
@SpringBootApplication
//@ComponentScan("com.atguigu")//来扫描包括service-util和service-oa在内的com.atguigu.
//启动类就在com.atguigu包下，所以默认就扫描这个路径下的所有组件，不需要再写ComponentScan("com.atguigu")了
//@MapperScan("com.atguigu.*.mapper")
public class ServiceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}
