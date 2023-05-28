package site.nomoreparties.stellarburgers;

import org.apache.commons.lang3.RandomStringUtils;

public class Utilities {
    private static String sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY0NzM3YjBjOWVkMjgwMDAxYjMyYzQyYiIsImlhdCI6MTY4NTI4OTc0MSwiZXhwIjoxNjg1MjkwOTQxfQ.7vB2deLpg3DcNe6_Uv95i2IeBxy6_FS9gosQkgF6Mu0";
    private static String sampleHash = "60d3b41abdacab0026a733c6";
    public static String getRandomLogin() {
        String login = RandomStringUtils.randomAlphanumeric(6) + "@yandex.ru";
        String loginToLC = login.toLowerCase();
        return loginToLC;
    }

    public static String getRandomPwd() {
        return RandomStringUtils.randomNumeric(6);
    }

    public static String getRandomString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static String getFakedToken() {
        char[] chars = sampleToken.toCharArray();
        return RandomStringUtils.randomAlphanumeric(chars.length);
    }

    public static String getRandomHash() {
        char[] chars = sampleHash.toCharArray();
        return RandomStringUtils.randomAlphanumeric(chars.length).toLowerCase();
    }

}
