package com.vs.panditji.model;

public class SignInResponse {


    /**
     * user_id : WWxjNWFreHRlSEJaVnpGdVVVUkplazVVUlhwT2JrNTNaR2M5UFE9PQ==
     * name : Vivek Pratap Singh
     * photo : vaidik1579981131.jpg
     * token : 1579981599
     * msg : user log in
     * error : 0
     */

    private String user_id;
    private String name;
    private String photo;
    private int token;
    private String msg;
    private int error;
    private String alert;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
}
