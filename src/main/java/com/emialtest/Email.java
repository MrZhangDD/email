package com.emialtest;


import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Date;
import java.util.Properties;

/**
 * 用来测试能否发送邮件成功
 * @author DELL
 */
public class Email {
    private static final String PROPERTIES_DEFAULT = "emailConfig.properties";
    //发件人的邮箱和密码
    /*public static String userName = "17633908117@163.com";
    public static String password = "20130800z.";
    //发件人邮箱的SMTP服务器地址
    public static String SMTP_HOST = "smtp.163.com";
    //收件人邮箱
    public static String TO_MAIL_ACCOUNT = "shimw@asiainfo.com";*/

    public static void main(String[] args) throws IOException, MessagingException {

        //1.参数配置，链接邮箱服务器
        Properties properties = new Properties();
        //配置文件位置
        //properties = PropertiesLoaderUtils.loadAllProperties("emailConfig.properties"); //中文乱码

        //第二种
        InputStream resource = Object.class.getResourceAsStream("/emailConfig.properties");
        InputStreamReader gbk = new InputStreamReader(resource, "UTF-8");
        properties.load(gbk);

        //使用的协议（JavaMail规范要求）
        properties.setProperty("mail.transport.protocol","smtp");
        // 发件人的邮箱的 SMTP 服务器地址
        properties.setProperty("mail.smtp.host", properties.getProperty("SMTP_HOST"));
        // 需要请求认证
        properties.setProperty("mail.smtp.auth","true");
// PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
        //     如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
        //     打开下面 /* ... */ 之间的注释代码, 开启 SSL 安全连接。
        /*
        // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
        //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
        final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);
        */




        //2.根据配置创建会话对象，用于和服务器交互
        Session session = Session.getInstance(properties);
        //设置为debug模式, 可以查看详细的发送 log
        session.setDebug(true);
        //3.创建一封邮件

        MimeMessage message = createMimeMessage(properties, session, properties.getProperty("USER_EMAIL"), properties.getProperty("TO_MAIL_ACCOUNT"));
        //4.根据session获取邮件传输对象
        Transport transport = session.getTransport();
        // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        //
        //    PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
        //           仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的链接, 根据给出的错误
        //           类型到对应邮件服务器的帮助网站上查看具体失败原因。
        //
        //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
        //
        //    PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
        transport.connect(properties.getProperty("USER_EMAIL"),properties.getProperty("PASSWORD"));
        //6.发送邮件
        transport.sendMessage(message,message.getAllRecipients());
        //7.关闭链接
        transport.close();

    }

    private static MimeMessage createMimeMessage(Properties properties, Session session, String userName, String toMailAccount) throws MessagingException, IOException {
        //创建一封邮件
        //创建邮件对象
        MimeMessage message = new MimeMessage(session);
        /*
         * 也可以根据已有的eml邮件文件创建 MimeMessage 对象
         * MimeMessage message = new MimeMessage(session, new FileInputStream("MyEmail.eml"));
         */
        // 2. From: 发件人
        //    其中 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
        //    真正要发送时, 邮箱必须是真实有效的邮箱。
        message.setFrom(new InternetAddress(userName,"17633908117","UTF-8"));
        //3.收件人
        message.setRecipients(MimeMessage.RecipientType.TO, String.valueOf(new InternetAddress(toMailAccount,"zhangdd7","UTF-8")));
//    To: 增加收件人（可选）
//        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("dd@receive.com", "USER_DD", "UTF-8"));
//        //    Cc: 抄送（可选）
//        message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("ee@receive.com", "USER_EE", "UTF-8"));
//        //    Bcc: 密送（可选）
//        message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("ff@receive.com", "USER_FF", "UTF-8"));
        //4.邮件主题
        message.setSubject(String.valueOf(properties.getProperty("EMAIL_TOPIC")),"GBK");
        //5。邮件正文
        message.setContent(properties.getProperty("EMAIL_TEXT"),"text/html;charset=UTF-8");

        //6.设置显示的发件时间
        message.setSentDate(new Date());
        //7.保存页面设置
        message.saveChanges();
        //8.邮件保存到本地
        OutputStream outputStream = new FileOutputStream("MyEmail.email");
        message.writeTo(outputStream);
        outputStream.flush();
        outputStream.close();
        return message;
    }
}
