package site.nomoreparties.stellarburgers;

import org.apache.commons.lang3.RandomStringUtils;

public class Utilities {
    public static String getRandomLogin() {
        return RandomStringUtils.randomAlphanumeric(6) + "@yandex.ru";
    }

    public static String getRandomPwd() {
        return RandomStringUtils.randomNumeric(6);
    }
    public static String getRandomString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }
}
