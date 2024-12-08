package com.lakshay.barcelonamatchcron.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public boolean sendMail(String postTitle, String url, List<String> toEmails, String teamName) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = "<h1><strong>" + postTitle + "</strong></h1>" +
                    "<p>URL: " + url + "</p>";

            helper.setTo(fromEmail);
            helper.setBcc(toEmails.toArray(new String[0]));
            helper.setFrom(fromEmail);
            helper.setSubject("New " + teamName + " Match Uploaded 🎥🌟");
            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
            log.info("Email sent successfully to {} recipients for {} match", toEmails.size(), teamName);
            return true;

        } catch (Exception e) {
            log.error("Failed to send email for " + teamName + " match", e);
            e.printStackTrace();
            return false;
        }
    }
}
