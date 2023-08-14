package com.atguigu.security.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.common.util.ResponseUtil;
import com.atguigu.security.custom.CustomUser;
import com.atguigu.vo.system.LoginVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**TokenLoginFilter:登录的filter，判断用户名和密码是否正确，生成token
 * @author zijianLi
 * @create 2023- 03- 19- 14:08
 */

public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {
    private RedisTemplate redisTemplate;



    //构造方法
    public TokenLoginFilter(AuthenticationManager authenticationManager,
                            RedisTemplate redisTemplate){
        //设置authenticationManager的参数
        this.setAuthenticationManager(authenticationManager);
        //设置提交方式
        this.setPostOnly(false);
        //指定登录的接口及提交方式，可以指定任意路径
        this.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/admin/system/index/login","POST"));
        this.redisTemplate = redisTemplate;
    }


    //登录认证过程，获取输入的用户名和密码，调用方法认证
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {

        try {
            //通过流的方式得到当前用户名和密码的信息
            LoginVo loginVo = new ObjectMapper().readValue(request.getInputStream(), LoginVo.class);
            //使用类UsernamePasswordAuthenticationToken来将信息封装成AuthenticationToken对象
            UsernamePasswordAuthenticationToken AuthenticationToken
                    = new UsernamePasswordAuthenticationToken(
                            loginVo.getUsername(), loginVo.getPassword());
            //调用方法完成认证
            return this.getAuthenticationManager().authenticate(AuthenticationToken);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
//        return null;

    }



    /**
     * 认证成功调用方法
     * 获取到token
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {
        //获取当前用户对象
        CustomUser customUser = (CustomUser) authResult.getPrincipal();
        //生成token字符串
        String token = JwtHelper.createToken(customUser.getSysUser().getId(),
                customUser.getSysUser().getUsername());

        //获取当前用户的权限数据，放到Redis里面：key是username ，value是权限数据，value转为json格式
        redisTemplate.opsForValue().set(customUser.getSysUser().getUsername(),
                JSON.toJSONString(customUser.getAuthorities()));
        String authString = (String) redisTemplate.opsForValue().get("lisi");
        System.out.println("redis的权限：");
        System.out.println(authString);

        //返回
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        //现在不是在controller里面，只能通过原生的方式返回，写了一个原生方式返回认证成功的工具类
        ResponseUtil.out(response, Result.ok(map));
    }
    //认证失败调用方法
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        //原生方式返回认证失败的工具类
        ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
    }
}
