package com.atguigu.process.service;

/** 有待审批消息，我们需要通知审批人审批信息，审批人审批过了，我们要通知提交申请人查看信息。
 * @author zijianLi
 * @create 2023- 03- 29- 22:52
 */
public interface MessageService {
    /**
     * 给要审批任务的人推送消息，提醒推送
     * @param processId
     * @param userId
     * @param taskId
     */
    void pushPendingMessage(Long processId, Long userId, String taskId);

    /**
     * 审批后推送提交审批人员
     * @param processId
     * @param userId
     * @param status
     */
    void pushProcessedMessage(Long processId, Long userId, Integer status);
}
