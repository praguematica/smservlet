package uk.co.praguematica.smservlet;

/**
 * Created by mfa on 07/10/2016.
 */
public class UserContext {
    private String userId;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserContext() {
    }

    public UserContext(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
