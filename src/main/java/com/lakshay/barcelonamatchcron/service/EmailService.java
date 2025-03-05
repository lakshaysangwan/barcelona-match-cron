package com.lakshay.barcelonamatchcron.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class EmailService {
    // Maps for competition paths to readable names
    private static final Map<String, String> COMPETITION_MAP = Map.of(
            "uefa/champions-league", "UEFA Champions League",
            "uefa/europa-league", "UEFA Europa League",
            "spain/laliga", "La Liga",
            "spain/copa-del-rey", "Copa del Rey",
            "spain/spanish-super-cup", "Spanish Super Cup",
            "england/premier-league", "Premier League",
            "england/fa-cup", "FA Cup",
            "england/efl-cup", "EFL Cup"
    );
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public boolean sendMail(String url, List<String> toEmails, String teamName) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // Parse match information from URL
            MatchInfo matchInfo = parseMatchInfoFromUrl(url);

            // Create enhanced email content
            String htmlMsg = buildEmailContent(matchInfo, url);

            helper.setTo(fromEmail);
            helper.setBcc(toEmails.toArray(new String[0]));
            helper.setFrom(fromEmail);
            String subject = matchInfo.homeTeam().isEmpty() || matchInfo.awayTeam().isEmpty()
                    ? "New " + teamName + " Match Uploaded 🎥🌟"
                    : matchInfo.homeTeam() + " vs " + matchInfo.awayTeam() + " - " + matchInfo.competition() + " 🎥🌟";
            helper.setSubject(subject);
            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
            log.info("Email sent successfully to {} recipients for {} match", toEmails.size(), teamName);
            return true;

        } catch (Exception e) {
            log.error("Failed to send email for {} match", teamName, e);
            e.printStackTrace();
            return false;
        }
    }

    private MatchInfo parseMatchInfoFromUrl(String url) {
        // Extract competition and match details from URL
        // Example URL: https://www.fullreplays.com/spain/laliga/barcelona-vs-getafe-25-sep-2024/
        Pattern pattern = Pattern.compile("fullreplays\\.com/([^/]+)/([^/]+)/([^/]+)-vs-([^/]+)-(\\d+-[^/]+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            String region = matcher.group(1);
            String competitionPath = matcher.group(2);
            String homeTeam = capitalizeTeam(matcher.group(3).replace("-", " "));
            String awayTeam = capitalizeTeam(matcher.group(4).replace("-", " "));
            String date = formatDate(matcher.group(5));

            // Get full competition name
            String competitionKey = region + "/" + competitionPath;
            String competition = COMPETITION_MAP.getOrDefault(competitionKey,
                    capitalizeTeam(competitionPath.replace("-", " ")));

            return new MatchInfo(homeTeam, awayTeam, competition, date);
        }

        // Fallback to simple match title if parsing fails
        return new MatchInfo("", "", "", "");
    }

    private String capitalizeTeam(String team) {
        String[] words = team.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    private String formatDate(String rawDate) {
        // Convert format if needed (e.g., "25-sep-2024" -> "25 Sep 2024")
        return rawDate.replace("-", " ");
    }

    private String buildEmailContent(MatchInfo matchInfo, String url) {
        StringBuilder htmlBuilder = new StringBuilder();

        // Build a more attractive email template
        htmlBuilder.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;'>");

        // Header with match details
        if (!matchInfo.homeTeam.isEmpty() && !matchInfo.awayTeam.isEmpty()) {
            htmlBuilder.append("<h2 style='text-align: center; color: #333;'>")
                    .append(matchInfo.homeTeam)
                    .append(" vs ")
                    .append(matchInfo.awayTeam)
                    .append("</h2>");

            if (!matchInfo.competition.isEmpty()) {
                htmlBuilder.append("<h3 style='text-align: center; color: #555; margin-top: -10px;'>")
                        .append(matchInfo.competition)
                        .append("</h3>");
            }

            if (!matchInfo.date.isEmpty()) {
                htmlBuilder.append("<p style='text-align: center; color: #777;'>")
                        .append(matchInfo.date)
                        .append("</p>");
            }
        }

        // Divider
        htmlBuilder.append("<hr style='border: 0; height: 1px; background: #e0e0e0; margin: 20px 0;'>");

        // Watch now button
        htmlBuilder.append("<div style='text-align: center; margin: 30px 0;'>")
                .append("<a href='")
                .append(url)
                .append("' style='background-color: #0056b3; color: white; padding: 12px 25px; text-decoration: none; border-radius: 4px; font-weight: bold;'>")
                .append("Watch Full Match Replay")
                .append("</a>")
                .append("</div>");

        // Footer
        htmlBuilder.append("<p style='color: #999; font-size: 12px; text-align: center; margin-top: 30px;'>")
                .append("This is an automated notification. Please do not reply to this email.")
                .append("</p>");

        htmlBuilder.append("</div>");

        return htmlBuilder.toString();
    }

    private record MatchInfo(String homeTeam, String awayTeam, String competition, String date) {
    }
}