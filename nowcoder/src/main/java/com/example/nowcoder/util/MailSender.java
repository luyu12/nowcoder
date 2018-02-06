package com.example.nowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

//该服务专门用来发邮件
@Service
public class MailSender implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

//    @Autowired
//    private VelocityEngine velocityEngine;

    public boolean sendWithHTMLTemplate(String to, String subject, String template, Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("administrator");
            InternetAddress from = new InternetAddress(nick + "<781105626@qq.com>");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            //邮件是有模版的
            //String result= VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,template,"UTF-8",model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText("登录异常", true);
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

    //bean在初始化的时候回调用这个函数，就会set好mailsender对象
    @Override
    public void afterPropertiesSet() throws Exception {
        //把mailsender对象的性质都设置好
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("781105626@qq.com");
        mailSender.setPassword("lkzmhhhtqxuubbcc");
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
