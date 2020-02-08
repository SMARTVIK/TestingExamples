package com.vs.panditji;

import android.content.SharedPreferences;

import com.vs.panditji.model.SignInResponse;

public class ApplicationDataController {
    private boolean userLoggedIn;
    private String userId;
    private SignInResponse currentUserResponse;

    private static ApplicationDataController instance = new ApplicationDataController();

    public static ApplicationDataController getInstance() {
        return instance;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    public boolean getUserLoggedIn() {
        return userLoggedIn;
    }

    public String getUserId() {
        return PanditJi.getInstance().getSharedPrefs().getString(Constants.USER_ID,null);
    }

    public void setUserId(String userId) {
        this.userId = userId;
        PanditJi.getInstance().getSharedPrefs().edit().putString(Constants.USER_ID,userId).commit();
    }

    public void setCurrentUserResponse(SignInResponse currentUserResponse) {
        this.currentUserResponse = currentUserResponse;
    }

    public SignInResponse getCurrentUserResponse() {
        return currentUserResponse;
    }

    public String getUserName() {
        return PanditJi.getInstance().getSharedPrefs().getString(Constants.NAME,null);
    }

    public String getUserEmail() {
        return PanditJi.getInstance().getSharedPrefs().getString(Constants.EMAIL,null);
    }

    public void logout() {
        PanditJi.getInstance().getSharedPrefs().edit().clear()  .commit();
    }
}
