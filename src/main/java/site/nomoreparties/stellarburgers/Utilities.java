package site.nomoreparties.stellarburgers;

import org.apache.commons.lang3.RandomStringUtils;

public class Utilities {
    private static String sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY0NzM3YjBjOWVkMjgwMDAxYjMyYzQyYiIsImlhdCI6MTY4NTI4OTc0MSwiZXhwIjoxNjg1MjkwOTQxfQ.7vB2deLpg3DcNe6_Uv95i2IeBxy6_FS9gosQkgF6Mu0";
    public static String getRandomLogin() {
        return RandomStringUtils.randomAlphanumeric(6) + "@yandex.ru";
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

    public static void main(String[] args) {
        System.out.println(sampleToken);
        System.out.println(getFakedToken());
    }

}
