package com.atguigu.process.controller.api;

import com.alibaba.fastjson.JSON;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.wechat.BindPhoneVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/** 微信授权登录的controler
 *  首先调用authorize方法，设置路径userInfoUrl可以获得微信信息和授权成功后跳转的路径
 *  第一次肯定没有授权，所以调用userInfo方法，得到openid，查数据库，openid存在就返回带token的跳转的路径
 *  如果没绑定手机号的话，就调用bindPhone更新手机号
 * @author zijianLi
 * @create 2023- 03- 29- 20:47
 */
@Controller
//加的是Controller而不是像其他controller的RestController，这样的话写的就可以使用重定向，进行页面跳转
//此时在方法中加入ResponseBody，这个方法就可以返回json数据
//RestController的意思是写的方法可以返回json数据
@RequestMapping("/admin/wechat")
@CrossOrigin
public class WechatController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")//这个可以获取配置文件的userInfoUrl
    private String userInfoUrl;

    /**
     * 微信登录的授权的接口，当点击别的路径的时候需要授权，此时就调用这个接口。
     * 调用这个接口时会自动跳转到userInfoUrl路径，即
     * http://workoa.free.idcfengye.com/admin/wechat/userInfo
     * @param returnUrl 是点击的别的路径
     * @param request
     * @return
     */
    @GetMapping("/authorize")
    public String authorize(
            @RequestParam("returnUrl") String returnUrl,
            HttpServletRequest request)  {
        //buildAuthorizationUrl:参数1：能够获取微信信息的路径，即wechat.userInfoUrl：userInfoUrl
        //第二个参数；固定值，授权类型。
//        WxConsts.OAuth2Scope.SNSAPI_USERINFO;
        //第3个参数：原来的路径returnUrl，要把returnUrl中的guiguoa转为#，因为#作为returnUrl参数传递
        //可能会出现问题,传递的参数一般需要编码一下
        String redirectURL = null;
        try {
            redirectURL = wxMpService.getOAuth2Service().buildAuthorizationUrl(userInfoUrl,
                    WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                    URLEncoder.encode(returnUrl.replace("guiguoa", "#"),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:" + redirectURL;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws Exception {
        //获取access-Token
        WxOAuth2AccessToken accessToken =
                wxMpService.getOAuth2Service().getAccessToken(code);
        //根据access-Token得到openid
        String openId = accessToken.getOpenId();
        System.out.println("openid: "+ openId);

        //获取微信用户信息
        WxOAuth2UserInfo wxMpUser =
                wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        System.out.println("【微信网页授权】wxMpUser={}"+ JSON.toJSONString(wxMpUser));

        //根据openid查询用户表，
        SysUser sysUser = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId, openId));
        String token = "";
        if(null != sysUser) {
            //sysUser存在，此时就创建一个token
            token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        }
        //把token和openId都作为url的参数重定向
        if(returnUrl.indexOf("?") == -1) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }


    /**
     * 绑定手机号的时候调用这个方法。
     * 根据前端传回的手机号来更新数据库
     * @param bindPhoneVo
     * @return
     */
    @ApiOperation(value = "微信账号绑定手机")
    @PostMapping("bindPhone")
    @ResponseBody//加了这个的话这个方法就可以返回json数据
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        //1. 根据手机号来查询数据库.eq()
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, bindPhoneVo.getPhone());
        SysUser sysUser = sysUserService.getOne(wrapper);
        //2.如果sysUser存在就更新openid
        if (sysUser!=null){
            sysUser.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(sysUser);
            //生成token，返回给前端，因为此时用户正在绑定手机号，还没登录上
            String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
            return Result.ok(token);
        }else {
            //sysUser不存在，说明没有这个手机号，此时就需要联系管理员在后台添加上这个手机号
            return Result.fail("手机号不存在，请联系管理员添加");
        }
    }

}
