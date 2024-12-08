package com.lakshay.barcelonamatchcron.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Team {
    BARCELONA("barcelona", "Barcelona") {
        @Override
        public List<String> getEmails() {
            return Emails.getBarcelonaEmails();
        }
    },
    MANCHESTER_UNITED("manchester+united", "Manchester United") {
        @Override
        public List<String> getEmails() {
            return Emails.getManUnitedEmails();
        }
    },
    MANCHESTER_CITY("manchester+city", "Manchester City") {
        @Override
        public List<String> getEmails() {
            return Emails.getManCityEmails();
        }
    };

    private final String searchText;
    private final String matchInTitle;

    public abstract List<String> getEmails();
}
