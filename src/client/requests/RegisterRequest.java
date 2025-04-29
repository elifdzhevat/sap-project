package client.requests;

public class RegisterRequest {
    private String action = "register";
    private String username;
    private String password;
    private String role;

    public RegisterRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
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

    public String getRole() {
        return role;
    }
}
