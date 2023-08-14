package com.atguigu.process.service.impl;

import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.mapper.OaProcessTemplateMapper;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-25
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    @Autowired
    //activity的service注入
    private RepositoryService repositoryService;

    @Autowired
    private OaProcessService oaProcessService;

    /**
     * 查询审批模板的分页接口实现。要根据数据库的id属性查询出实际的类型
     * @param pageParam
     * @return
     */
    @Override
    public IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam) {
        //1.调用mapper方法查询得到分页数据，里面有list集合
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        List<ProcessTemplate> pageList = processTemplatePage.getRecords();
        //2. 遍历list集合，根据元素的id查询得到对应的类型名称
        for(ProcessTemplate item : pageList){
            Long TypeId = item.getProcessTypeId();
            LambdaQueryWrapper<ProcessType> wrapper = new LambdaQueryWrapper<>();
            //ProcessType实体类对应的数据库的id就对应TypeId
            wrapper.eq(ProcessType::getId, TypeId);
            ProcessType processTypeServiceOne = oaProcessTypeService.getOne(wrapper);
            if (processTypeServiceOne==null){
                continue;
            }
            //3. 类型名称封装到实体类的processTypeName属性封装返回
            item.setProcessTypeName(processTypeServiceOne.getName());
        }
        return processTemplatePage;
    }

    /**
     * 部署流程定义方法
     * @param id
     */
    @Override
    public void publish(Long id) {
        //根据id获取对象
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);
        //绝对路径不等于空的话就定义部署,调用deployByZip方法
        if(!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())){
            oaProcessService.deployByZip(processTemplate.getProcessDefinitionPath());
        }
    }

}
