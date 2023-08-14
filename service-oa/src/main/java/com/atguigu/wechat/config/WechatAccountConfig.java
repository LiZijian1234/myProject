package com.atguigu.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zijianLi
 * @create 2023- 03- 28- 21:38
 */

@Data
@Component
@ConfigurationProperties(prefix = "wechat")//这个注解可以读取配置文件的内容
public class WechatAccountConfig {
    private String mpAppId;

    private String mpAppSecret;
}
