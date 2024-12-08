package com.lakshay.barcelonamatchcron.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Emails {
    LAKSHAY("lakshay.ib.hisar@gmail.com"),
    AYUSH("ayush2152001@gmail.com ");

    private final String email;

    public static List<String> getBarcelonaEmails() {
        return Arrays.stream(Emails.values())
                .map(Emails::getEmail)
                .collect(Collectors.toList());
    }

    public static List<String> getManCityEmails() {
        return Collections.singletonList(AYUSH.getEmail());
    }

    public static List<String> getManUnitedEmails() {
        return Collections.singletonList(LAKSHAY.getEmail());
    }
}
