package com.lakshay.barcelonamatchcron.utilities;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class Utilities {
    public Connection.Response manageConnection(String url) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                return Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "en-US,en;q=0.5")
                        .timeout(10000)  // 10 seconds timeout
                        .followRedirects(true)
                        .ignoreHttpErrors(true)
                        .execute();

            } catch (IOException e) {
                retryCount++;
                log.error("Attempt {} failed to connect to {}. Error: {}",
                        retryCount, url, e.getMessage());

                if (retryCount < maxRetries) {
                    try {
                        // Exponential backoff: 1s, 2s, 4s
                        Thread.sleep(1000L * (1L << (retryCount - 1)));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }
        }

        log.error("Failed to connect to {} after {} attempts", url, maxRetries);
        return null;
    }
}
