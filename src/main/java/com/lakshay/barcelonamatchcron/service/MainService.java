package com.lakshay.barcelonamatchcron.service;

import com.lakshay.barcelonamatchcron.entity.MailRecord;
import com.lakshay.barcelonamatchcron.enums.Team;
import com.lakshay.barcelonamatchcron.repository.MailRecordRepository;
import com.lakshay.barcelonamatchcron.utilities.Utilities;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Component
@Transactional(readOnly = true)
public class MainService {
    private final MailRecordRepository repositoryFactory;
    private final Utilities utilities;
    private final EmailService emailService;
    @PersistenceContext
    private EntityManager entityManager;

    public MainService(MailRecordRepository repositoryFactory, Utilities utilities, EmailService emailService) {
        this.repositoryFactory = repositoryFactory;
        this.utilities = utilities;
        this.emailService = emailService;
    }

    private MailRecordRepository getRepository() {
        entityManager.clear();
        return repositoryFactory;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void runTask() {
        entityManager.clear();
        entityManager.flush();

        for (Team team : Team.values()) {
            try {
                processTeamMatches(team);
                // Close and reopen transaction between teams
                entityManager.flush();
                entityManager.clear();
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Error in task for team: {}", team.getMatchInTitle(), e);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processTeamMatches(Team team) {
        try {
            processTeamMatchesInternal(team);
            entityManager.flush();
            entityManager.clear();
        } catch (Exception e) {
            log.error("Error processing matches for team: {}", team.getMatchInTitle(), e);
            throw e;
        }
    }

    private void processTeamMatchesInternal(Team team) {
        Connection.Response response = getResponse(team);
        if (response == null) {
            return;
        }

        getMatchPosts(response)
                .stream()
                .map(this::extractPostData)
                .filter(postData -> isRelevantMatch(postData, team))
                .filter(postData -> !isExistingRecord(postData.title()))
                .forEach(postData -> processNewMatch(postData, team));
    }

    private Connection.Response getResponse(Team team) {
        String searchUrl = "https://www.fullreplays.com/?s=" + team.getSearchText();
        return utilities.manageConnection(searchUrl);
    }

    private Elements getMatchPosts(Connection.Response response) {
        return Jsoup.parse(response.body()).getElementsByTag("article");
    }

    private PostData extractPostData(Element post) {
        Element anchor = post.getElementsByClass("entry-header").get(0).getElementsByTag("h2").get(0);
        String title = anchor.text();
        String url = anchor.getElementsByTag("a").get(0).attr("href");
        return new PostData(title, url);
    }

    private boolean isRelevantMatch(PostData postData, Team team) {
        return postData.title().toLowerCase().contains(team.getMatchInTitle().toLowerCase());
    }

    private boolean isExistingRecord(String title) {
        try {
            return getRepository().existsByExactTitle(title);
        } catch (Exception e) {
            log.error("Error checking record existence: {}", e.getMessage());
            return false;
        }
    }

    private void processNewMatch(PostData postData, Team team) {
        try {
            if (emailService.sendMail(postData.title(), postData.url(), team.getEmails(), team.getMatchInTitle())) {
                log.info("{} match found and mail sent: {}", team.getMatchInTitle(), postData.title());
                saveRecord(postData);
            }
        } catch (Exception e) {
            log.error("Error processing record for title: {}", postData.title(), e);
        }
    }

    private void saveRecord(PostData postData) {
        try {
            MailRecord newRecord = MailRecord.builder()
                    .title(postData.title())
                    .url(postData.url())
                    .build();
            getRepository().save(newRecord);
            entityManager.flush();
            log.info("Successfully saved record with title: {}", postData.title());
        } catch (Exception e) {
            log.error("Error saving record: {}", e.getMessage());
            throw e;
        }
    }
}

record PostData(String title, String url) {
}
