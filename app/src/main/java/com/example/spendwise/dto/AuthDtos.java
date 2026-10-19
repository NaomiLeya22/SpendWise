package com.example.spendwise.dto;

public class AuthDtos {

    private AuthDtos() {
    }

    public static class RegisterRequest {
        public String fullName;
        public String email;
        public String password;

        public RegisterRequest(
                String fullName,
                String email,
                String password
        ) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
        }
    }

    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class AuthData {
        public String token;
        public UserDto user;
    }

    public static class UserDto {
        public long id;
        public String fullName;
        public String email;
    }

}
