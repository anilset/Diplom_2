package site.nomoreparties.stellarburgers.pojo;

public class Auth {
    public String token;
    public String accessToken;
    public String refreshToken;

    public Auth() {
    }

    public Auth(String token) {
        this.token = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
