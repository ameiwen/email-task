package com.bl.task;

import com.bl.service.EmailTaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Component
public class EmailTask {

    private static final Logger logger = LogManager.getLogger(EmailTask.class);

    @Resource
    private EmailTaskService emailTaskService;

    public void sendEmail(){
        logger.info("send email task begin");
        emailTaskService.sendEmailTask();
        logger.info("end email task end");
    }


}
