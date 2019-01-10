package com.yangkang.ssmdemo01.mvc.controller;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * ActivitiController
 * 只测试了最基本的功能, 其他的可以参考这个人博客: https://www.cnblogs.com/dengjiahai/category/1175889.html
 * 1. 自动执行任务--编写委托类, 实现JavaDelegate, 重写execute提交方法,后把委托类设置到流程的task listener上面
 * 2. 定时任务--开启jobExecutorActivate, 流程图设置某节点的due date超时时间, 之后增加定时器边界, 取消活动true则超时直接取消;
 * 3. 邮件任务--首先打开邮箱的SMTP服务,生成授权码,设置邮箱的SMTP服务器主机地址mailServerHost,端口mailServerPort,来自mailServerDefaultFrom,用户名mailServerUsername,,授权码mailServerUsername,开启ssl安全传输mailServerUseSSL,最后配置邮件任务即可
 * 4. 监听任务--编写监听类,实现TaskListenerjpk, 重写notify方法, 可以读取变量动态设置assignee
 *
 * @author yangkang
 * @date 2019/1/9
 */
@Controller
@RequestMapping("/activiti")
public class ActivitiController {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private IdentityService identityService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private FormService formService;

    @Resource
    private ManagementService managementService;

    // 1. 部署流程, 成功后，会分别在 act_ge_bytearray，act_re_deployment，act_re_procdef三张表插入相应数据，多次部署同一流程的话会增加版本号，以此获取最新的流程
    /**
     * 通过定义好的流程图文件部署, 一次只能部署一个流程
     */
    @RequestMapping("/deployOne")
    public void deployOne(){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("activiti/designs/oa/MyFirstBpm-Leave.bpmn")
//                .key("")
//                .name("")
//                .category("")
                .deploy();
        System.out.println(deployment.getName() + " : " + deployment.getId() + " : " + deployment.getKey());
    }

    /**
     * 将多个流程打包部署, 一次可以部署多个流程
     */
    @RequestMapping("/deployZip")
    public void deployZip(){
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("activiti/deployments/oa/MyFirstBpm-Leave.zip");
        ZipInputStream zipInputStream = new ZipInputStream(is);
        Deployment deployment = repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
        System.out.println(deployment.getName() + " : " + deployment.getId() + " : " + deployment.getKey());
    }

    // 2. 启动流程,流程具体信息可以新建一张表来记录,并且记录流程实例id,用来以后关联查询
    //流程启动成功后会在 act_hi_actinst，act_hi_identitylink，act_hi_procinst，act_hi_taskinst，act_ru_execution，act_ru_identitylink，act_ru_task 表中插入相应数据。我们比较关心的就是 act_ru_task 表了，它存储了任务的相关信息。
    @RequestMapping("startProcess")
    public void startProcess(){
        //可以添加参数,business_key以及变量集合map,这样就可以在流程里使用EL表达式动态设置变量了,比如${userId}
        ProcessInstance leaveProcess = runtimeService.startProcessInstanceByKey("leaveProcess");
        System.out.println(leaveProcess.getId());
    }

    // 3. 查询进行中的进程
    @RequestMapping("queryProcess")
    public void queryProcess(){
//        List<Task> taskList = taskService.createTaskQuery().taskAssignee("staff").list();
        List<Task> taskList = taskService.createTaskQuery().taskAssignee("pm").list();
        for (Task task : taskList){
            System.out.println(task.getId() + " : " + task.getName()  + " : " + task.getAssignee()
                    + " : " + task.getExecutionId()  + " : " + task.getProcessInstanceId()  + " : " + task.getProcessDefinitionId());
        }
    }

    // 4. 查询流程图
    @RequestMapping("showPic")
    public void showPic(HttpServletRequest request, HttpServletResponse response){
        try {
            response.setHeader("Cache-Control", "no-store");    //禁止浏览器缓存
            response.setHeader("Pragrma", "no-cache");    //禁止浏览器缓存
            response.setDateHeader("Expires", 0);    //禁止浏览器缓存
            String processDefinitionId = "leaveProcess:3:12";
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
            String diagramResourceName = processDefinition.getDiagramResourceName();
            InputStream imageStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), diagramResourceName);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = imageStream.read(bytes, 0, 1024)) != -1) {
                response.getOutputStream().write(bytes, 0, len);
            }
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 5. 审批流程
    @RequestMapping("completeTask")
    public void completeTask(){
        //staff
//        String taskId = "17";
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("day", 3);
//        taskService.complete(taskId, param);

        //pm
        String taskId = "22";
        taskService.complete(taskId);
    }
}
