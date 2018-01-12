package com.bl.service.impl;

import com.bl.dao.EmailTaskDao;
import com.bl.model.BlEmailTask;
import com.bl.model.Email;
import com.bl.service.EmailTaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class EmailTaskServiceImpl implements EmailTaskService {

    private static final Logger logger = LogManager.getLogger(EmailTaskServiceImpl.class);


    @Resource
    private EmailTaskDao emailTaskDao;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private SimpleMailMessage simpleMailMessage;


    @Override
    public void sendEmailTask() {
        List<BlEmailTask> emailTasks = emailTaskDao.selectEmailTaskList();
        if(emailTasks!=null && emailTasks.size()>0){
            for(BlEmailTask emailTask : emailTasks){
                if(emailConfig(emailTask)){
                    emailTaskDao.updateEmailTaskStatus(emailTask.getId());
                }
            }
        }
    }

    private boolean emailConfig(BlEmailTask blEmailTask){
        Email email = new Email();
        //主题
        email.setSubject("哈喽");
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>");
        builder.append("<p>你的评论有新回复了奥~~<p/>");
        builder.append("<p>"+blEmailTask.getAuthor()+"回复了您:<p/>");
        builder.append("<p>"+blEmailTask.getMsg()+"</P>");
        builder.append("<p>查看详细信息,请<a href='http://120.79.30.96/article/"+blEmailTask.getCid()+"#comments'>点击这里</a></p>");
        builder.append("</body></html>");
        String content = builder.toString();
        email.setContent(content);
        email.setToEmails(blEmailTask.getEmail());
        return sendEmail(email);
        //附件
//        Map<String, String> attachments = new HashMap<String, String>();
//        attachments.put("清单.xlsx",excelPath+"清单.xlsx");
//        email.setAttachments(attachments);
    }

    /**
     * 发送邮件
     * @param email
     */
    private boolean sendEmail(Email email) {
        // 建立邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            // 设置发件人邮箱
            if (email.getEmailFrom()!=null) {
                messageHelper.setFrom(email.getEmailFrom());
            } else {
                messageHelper.setFrom(simpleMailMessage.getFrom());
            }
            String nick = MimeUtility.encodeText("www.yangxs.ink");
            messageHelper.setFrom(new InternetAddress(nick + "<769741443@qq.com>"));

            // 设置收件人邮箱
            if (email.getToEmails()!=null) {
                String[] toEmailArray = email.getToEmails().split(";");
                List<String> toEmailList = new ArrayList<String>();
                if (null == toEmailArray || toEmailArray.length <= 0) {
                } else {
                    for (String s : toEmailArray) {
                        if (s!=null&&!s.equals("")) {
                            toEmailList.add(s);
                        }
                    }
                    if (null == toEmailList || toEmailList.size() <= 0) {
                    } else {
                        toEmailArray = new String[toEmailList.size()];
                        for (int i = 0; i < toEmailList.size(); i++) {
                            toEmailArray[i] = toEmailList.get(i);
                        }
                    }
                }
                messageHelper.setTo(toEmailArray);
            } else {
                messageHelper.setTo(simpleMailMessage.getTo());
            }

            // 邮件主题
            if (email.getSubject()!=null) {
                messageHelper.setSubject(email.getSubject());
            } else {
                messageHelper.setSubject(simpleMailMessage.getSubject());
            }

            // true 表示启动HTML格式的邮件
            messageHelper.setText(email.getContent(), true);

            // 添加图片
            if (null != email.getPictures()) {
                for (Iterator<Map.Entry<String, String>> it = email.getPictures().entrySet()
                        .iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = it.next();
                    String cid = entry.getKey();
                    String filePath = entry.getValue();
                    if (null == cid || null == filePath) {
                        throw new RuntimeException("请确认每张图片的ID和图片地址是否齐全！");
                    }

                    File file = new File(filePath);
                    if (!file.exists()) {
                        throw new RuntimeException("图片" + filePath + "不存在！");
                    }

                    FileSystemResource img = new FileSystemResource(file);
                    messageHelper.addInline(cid, img);
                }
            }

//            // 添加附件
//            if (null != mail.getAttachments()) {
//                for (Iterator<Map.Entry<String, String>> it = mail.getAttachments()
//                        .entrySet().iterator(); it.hasNext();) {
//                    Map.Entry<String, String> entry = it.next();
//                    String cid = entry.getKey();
//                    String filePath = entry.getValue();
//                    if (null == cid || null == filePath) {
//                        throw new RuntimeException("请确认每个附件的ID和地址是否齐全！");
//                    }
//
//                    File file = new File(filePath);
//                    if (!file.exists()) {
//                        throw new RuntimeException("附件" + filePath + "不存在！");
//                    }
//
//                    FileSystemResource fileResource = new FileSystemResource(file);
//                    messageHelper.addAttachment(cid, fileResource);
//                }
//            }
            messageHelper.setSentDate(new Date());
            // 发送邮件
            message.setHeader("X-Mailer", "Microsoft Outlook Express 6.00.2900.2869");
            javaMailSender.send(message);
        }  catch (AddressException e) {
            logger.error( "收件人账户信息不正确!",e);
            return false;
        } catch (MessagingException e) {
            logger.error("收件人账户异常!",e);
            return false;
        } catch (UnsupportedEncodingException e) {
            logger.error("send error!",e);
            return false;

        }
        return true;
    }

}
