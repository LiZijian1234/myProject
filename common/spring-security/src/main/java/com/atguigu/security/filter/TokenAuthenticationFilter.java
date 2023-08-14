package com.atguigu.security.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.common.util.ResponseUtil;
import com.atguigu.security.custom.LoginUserInfoHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**具体核心组件2；认证解析token滤波器组件：判断请求头是否有token，如果有则认证完成
 * 因为用户登录状态在token中存储在客户端，所以每次请求接口请求头携带token，
 * 后台通过自定义token过滤器拦截解析token完成认证并填充用户信息实体。
 * @author zijianLi
 * @create 2023- 03- 19- 14:59
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private RedisTemplate redisTemplate;

    //通过构造方法完成redis方法的注入
    public TokenAuthenticationFilter(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    /**
     * 判断是否有token，是否是登录状态
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        logger.info("uri:"+request.getRequestURI());
        //如果是登录接口，直接放行，不用查验token
        if("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        //getAuthentication判断请求头里面有没有token，有的话就市登录，没有的话就不是登录返回作为authentication
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(null != authentication) {
            //token不为空，就把值放到上下文对象中
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            //token为空，就直接返回错误信息，使用原生的返回方式
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    /**
     * 这个方法在上面方法里调用了
     * 进入一个页面的时候首先判断请求头里面是否有token，没有判断token的正确性啊
     * 判断到有token之后就把信息
     * @param request
     * @return
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        System.out.println("进入判断请求头是否有token界面");
        String token = request.getHeader("token");
        if(!StringUtils.isEmpty(token)){
            //从token中获取username
            String username = JwtHelper.getUsername(token);
            //判断username是不是空
            if(!StringUtils.isEmpty(username)){
                //此时就把用户信息放到LoginUserInfoHelper里面去
                LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
                LoginUserInfoHelper.setUsername(JwtHelper.getUsername(token));

                //这里通过用户名称从redis中获取到权限的数据
                String authString = (String) redisTemplate.opsForValue().get(username);
                //把redis中的权限String数据转为集合类型：List<SimpleGrantedAuthority>
                if(!StringUtils.isEmpty(authString)){
                    //把redis获取的string转为list集合，key是authority，value是权限string
                    List<Map> mapList = JSON.parseArray(authString, Map.class);
                    //创建集合类型：List<SimpleGrantedAuthority>，
                    // 作为return的new UsernamePasswordAuthenticationToken的第3个参数，作为权限
                    List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
                    for(Map map : mapList){
                        String auth = (String) map.get("authority");
                        authorityList.add(new SimpleGrantedAuthority(auth));
                    }
                    //封装UsernamePasswordAuthenticationToken对象并返回
                    return new UsernamePasswordAuthenticationToken(username,
                            null, authorityList);
                }

                else {
                    return new UsernamePasswordAuthenticationToken(username,
                            null, new ArrayList<>());
                }

            }
        }
        return null;
    }
}
