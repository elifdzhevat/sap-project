package client.requests;

public class LoginRequest {
    private String action = "login";
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
