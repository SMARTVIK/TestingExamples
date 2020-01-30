package com.vs.panditji.model;

public class SignUp {


    /**
     * alert : otp send on email and mobile
     * error : 1
     */

    private String alert;
    private int error;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
