package com.lakshay.barcelonamatchcron.service;

import com.lakshay.barcelonamatchcron.entity.MailRecord;
import com.lakshay.barcelonamatchcron.repository.MailRecordRepository;
import com.lakshay.barcelonamatchcron.utilities.Utilities;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MainService {
    @Autowired
    private MailRecordRepository repository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private Utilities utilities;

    @Scheduled(cron = "0 0 */2 * * ?")
    public void runTask() {
        log.info("Cron ran.");
        Connection.Response response = utilities.manageConnection("https://www.footballorgin.com/?s=barcelona");
        if (response == null) {
            return;
        }
        Elements matchPosts = Jsoup.parse(response.body()).getElementsByTag("article");
        for (Element post : matchPosts) {
            Element anchor = post.getElementsByTag("a").get(2);
            String postTitle = anchor.text();
            if (postTitle.contains("Full Match")) {
                repository.findByTitle(postTitle)
                        .orElseGet(() -> {
                            String url = anchor.attr("href");
                            if (!sendMail(postTitle, url)) {
                                return null;
                            }
                            log.info(postTitle + " mail sent!!!");
                            MailRecord newRecord = MailRecord.builder()
                                    .title(postTitle)
                                    .url(url)
                                    .build();
                            repository.save(newRecord);
                            return newRecord;
                        });
            }
        }
    }

    public boolean sendMail(String postTitle, String url) {
        String[] emailIds = new String[]{"lakshay.ib.hisar@gmail.com", "ayush2152001@gmail.com"};
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String htmlMsg = "<h1><strong>" + postTitle + "</strong></h1>" +
                "<p>URL: " + url + "</p>";
        try {
            helper.setTo(emailIds);
            helper.setSubject("New FC Barcelona Match Uploaded 🎥🌟");
            helper.setText(htmlMsg, true); // Set to true, to indicate that the text includes HTML tags.
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
