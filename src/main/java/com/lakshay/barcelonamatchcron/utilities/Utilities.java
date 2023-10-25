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
        int hitCount = 0;
        Connection.Response response;
        while (hitCount < 10) {
            try {
                response = Jsoup.connect(url).method(Connection.Method.GET).execute();
                return response;
            } catch (IOException e) {
                log.info("Failed to hit " + url + " website.");
                hitCount++;
            }
            hitCount++;
        }
        return null;
    }
}
