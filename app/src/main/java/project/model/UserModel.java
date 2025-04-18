package project.model;

import androidx.annotation.NonNull;

public class UserModel {

    private String email;
    private String username;
    private String password;

    public UserModel(@NonNull String email, String username, String password) {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("All fields are required!");
        }
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public UserModel(@NonNull String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("All fields are required!");
        }
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return  "email: " + email + "\n" +
                "username: " + username + "\n" +
                "password=" + password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
