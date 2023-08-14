package com.atguigu.vo.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 这个类容纳的是员工端提交请假后的那个json信息
 */
@Data
@ApiModel(description = "流程表单")
public class ProcessFormVo {

	@ApiModelProperty(value = "审批模板id")
	private Long processTemplateId;

	@ApiModelProperty(value = "审批类型id")
	private Long processTypeId;

	@ApiModelProperty(value = "表单值")
	private String formValues;

}