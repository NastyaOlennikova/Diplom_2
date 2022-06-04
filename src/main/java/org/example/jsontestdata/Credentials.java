package org.example;

public class Credentials {
    private String email;
    private String password;
    private String name;

    public Credentials (String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Credentials (String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public Credentials (String email) {
        this.email = email;
    }

    public Credentials () {}

    public String getEmail() {
        return email;
    }

    public String setAndReturnEmail(String email) {
        this.email = email;
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String setAndReturnPassword(String password) {
        this.password = password;
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

