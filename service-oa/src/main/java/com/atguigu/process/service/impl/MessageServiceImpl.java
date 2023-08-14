package com.atguigu.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.system.SysUser;
import com.atguigu.process.service.MessageService;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.security.custom.LoginUserInfoHelper;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author zijianLi
 * @create 2023- 03- 29- 22:53
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private OaProcessService oaProcessService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private WxMpService wxMpService;
    /**
     * 给要审批任务的人推送消息，提醒推送
     * @param processId 流程id
     * @param userId 要推送给谁的id
     * @param taskId 当前的任务id
     */
    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) {
        //根据这些id把数据查出来
        //查询流程信息
        Process process = oaProcessService.getById(processId);
        //根据userid查询人的信息
        SysUser sysUser = sysUserService.getById(userId);
        //查询审批模板信息
        ProcessTemplate processTemplate =
                oaProcessTemplateService.getById(process.getProcessTemplateId());
        //获取这个流程实例的提交人的信息，、
        //process的userid属性就是提交人的id
        SysUser submitSysUser = sysUserService.getById(process.getUserId());

        String openId = sysUser.getOpenId();
        if (StringUtils.isEmpty(openId)){
            //为了测试就让这个openid等于自己的了，添加默认值
            openId = "oJiuA58t8GT_s_37np_rGOPUFo-s";
        }
        //设置消息发送信息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)//里面填的推送的openid值
                //微信公众号后台创建模板信息的id值
                .templateId("Nv7j4qsb2wUgzc6GygzjI9FuEhV7aUK7iZQiPoNDRSs")
                //点击消息，跳转的地址,是前端公众号页面的地址，9090的
                .url("http://workoasecond.vipgz1.91tunnel.com/#/show/" + processId + "/" + taskId)
                .build();
        //下面设置推送消息模板的参数的值
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first",
                submitSysUser.getName()+"提交了"+processTemplate.getName()+
                        "审批申请，请注意查看", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1",
                process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2",
                new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"),
                "#272727"));
        templateMessage.addData(new WxMpTemplateData("content",
                content.toString(), "#272727"));
        //进行最终消息发送
        try {
            String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            System.out.println(msg);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) {
        Process process = oaProcessService.getById(processId);
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());
        SysUser sysUser = sysUserService.getById(userId);
        SysUser currentSysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        String openid = sysUser.getOpenId();
        if(StringUtils.isEmpty(openid)) {
            openid = "omwf25izKON9dktgoy0dogqvnGhk";
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("7ayskgolVbQZlbu725vo4WEwMC5wdTGyhQ0JskTstXA")//模板id
                .url("http://workoasecond.vipgz1.91tunnel.com/#/show/"+processId+"/0")//点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", "你发起的"+processTemplate.getName()+"审批申请已经被处理了，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", currentSysUser.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = null;
        try {
            msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        System.out.println("推送消息返回：{}"+ msg);
    }
}
