package com.lakshay.barcelonamatchcron.service;

import com.lakshay.barcelonamatchcron.entity.MailRecord;
import com.lakshay.barcelonamatchcron.enums.Team;
import com.lakshay.barcelonamatchcron.repository.MailRecordRepository;
import com.lakshay.barcelonamatchcron.utilities.Utilities;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MainService {
    private final MailRecordRepository mailRecordRepository;
    private final Utilities utilities;
    private final EmailService emailService;

    public MainService(MailRecordRepository mailRecordRepository, Utilities utilities, EmailService emailService) {
        this.mailRecordRepository = mailRecordRepository;
        this.utilities = utilities;
        this.emailService = emailService;
    }

    public void runTask() {
        log.info("Starting match notification task");

        for (Team team : Team.values()) {
            try {
                processTeam(team);
                // Small delay between teams to avoid rate limiting
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Error in task for team: {}", team.getMatchInTitle(), e);
                // Continue with next team
            }
        }
    }

    public void processTeam(Team team) {
        log.info("Processing matches for team: {}", team.getMatchInTitle());

        try {
            Connection.Response response = getResponse(team);
            if (response == null) {
                return;
            }

            Elements matchPosts = getMatchPosts(response);
            log.info("Posts found on page: {}", matchPosts.size());

            // Process each match individually
            for (Element post : matchPosts) {
                PostData postData = extractPostData(post);
                if (postData != null && isRelevantMatch(postData, team) && !isExistingRecord(postData.title())) {
                    processMatch(postData, team);
                }
            }

            log.info("Completed processing for team: {}", team.getMatchInTitle());
        } catch (Exception e) {
            log.error("Error processing matches for team: {}", team.getMatchInTitle(), e);
            throw e;
        }
    }

    public void processMatch(PostData postData, Team team) {
        try {
            boolean emailSent = emailService.sendMail(
                    postData.url(),
                    team.getEmails(),
                    team.getMatchInTitle()
            );

            if (emailSent) {
                saveRecord(postData);
                log.info("{} match found and mail sent: {}", team.getMatchInTitle(), postData.title());
            }
        } catch (Exception e) {
            log.error("Error processing match '{}': {}", postData.title(), e.getMessage());
            throw e;
        }
    }

    private Connection.Response getResponse(Team team) {
        String searchUrl = "https://www.fullreplays.com/?s=" + team.getSearchText();
        try {
            return utilities.manageConnection(searchUrl);
        } catch (Exception e) {
            log.error("Failed to get response for URL {}: {}", searchUrl, e.getMessage());
            return null;
        }
    }

    private Elements getMatchPosts(Connection.Response response) {
        try {
            return Jsoup.parse(response.body()).getElementsByTag("article");
        } catch (Exception e) {
            log.error("Failed to parse HTML: {}", e.getMessage());
            return new Elements();
        }
    }

    private PostData extractPostData(Element post) {
        try {
            Element anchor = post.getElementsByClass("entry-header").get(0).getElementsByTag("h2").get(0);
            String title = anchor.text();
            String url = anchor.getElementsByTag("a").get(0).attr("href");
            return new PostData(title, url);
        } catch (Exception e) {
            log.warn("Error extracting post data: {}", e.getMessage());
            return null;
        }
    }

    private boolean isRelevantMatch(PostData postData, Team team) {
        return postData.title().toLowerCase().contains(team.getMatchInTitle().toLowerCase());
    }

    private boolean isExistingRecord(String title) {
        try {
            return mailRecordRepository.existsByExactTitle(title);
        } catch (Exception e) {
            log.error("Error checking record existence: {}", e.getMessage());
            return false;
        }
    }

    public void saveRecord(PostData postData) {
        try {
            MailRecord newRecord = MailRecord.builder()
                    .title(postData.title())
                    .url(postData.url())
                    .build();

            mailRecordRepository.save(newRecord);
        } catch (Exception e) {
            log.error("Error saving record: {}", e.getMessage());
            throw e;
        }
    }
}

record PostData(String title, String url) {
}