package com.magic.crm.dto;

import com.magic.crm.entity.User;

public class AuthResponse {
    
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private UserInfo user;
    
    public AuthResponse(String token, String refreshToken, User user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = new UserInfo(user);
    }
    
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String phoneNumber;
        private User.Role role;
        
        public UserInfo(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.fullName = user.getFullName();
            this.phoneNumber = user.getPhoneNumber();
            this.role = user.getRole();
        }
        
        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getPhoneNumber() { return phoneNumber; }
        public User.Role getRole() { return role; }
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
}