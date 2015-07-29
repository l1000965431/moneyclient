package com.dragoneye.money.user;

/**
 * Created by happysky on 15-7-22.
 */
public class CurrentUser {
    public static UserBase getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(UserBase currentUser) {
        CurrentUser.currentUser = currentUser;
    }

    private static UserBase currentUser;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        CurrentUser.token = token;
    }

    private static String token;
}
