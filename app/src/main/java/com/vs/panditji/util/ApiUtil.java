package com.vs.panditji.util;

import com.google.gson.Gson;
import com.vs.panditji.BookingListModel;
import com.vs.panditji.Constants;
import com.vs.panditji.ProfileSignUp;
import com.vs.panditji.SignUpModel;
import com.vs.panditji.listener.WebApi;
import com.vs.panditji.model.AvailabilityModel;
import com.vs.panditji.model.CategoryModel;
import com.vs.panditji.model.OtpResponse;
import com.vs.panditji.model.PanditDetailsModel;
import com.vs.panditji.model.PopularPanditModel;
import com.vs.panditji.model.PopularPoojaModel;
import com.vs.panditji.model.PujaDetailModel;
import com.vs.panditji.model.PujaModel;
import com.vs.panditji.model.SignInResponse;
import com.vs.panditji.model.SignUp;
import com.vs.panditji.model.UserProfileModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtil {

    private static final String BASE_URL = "https://www.vaidiksewa.in/pandit_api/";
    private static ApiUtil instance;
    private WebApi api;

    private ApiUtil() {
    }

    public static synchronized ApiUtil getInstance() {
        if (instance == null) {
            instance = new ApiUtil();
        }

        return instance;
    }

    public WebApi getApi() {
        if (this.api == null) {
            this.api = (WebApi)(new Retrofit.Builder()).baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(new Gson())).build().create(WebApi.class);
        }
        return this.api;
    }

    public void getUserProfile(String userId, Callback<List<UserProfileModel>> callBack){
        Map<String, String> values = new HashMap<>();
        values.put("user_id",userId);
        String requestString = new JSONObject(values).toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE),requestString.getBytes());
        Call<List<UserProfileModel>> call = this.getApi().getUserProfile(requestBody);
        call.enqueue(callBack);
    }

    public void signIn(String userMail, String pass, Callback<SignInResponse> callBack){
        Map<String, String> values = new HashMap<>();
        values.put("user",userMail);
        values.put("pass",pass);
        String requestString = new JSONObject(values).toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE),requestString.getBytes());
        Call<SignInResponse> call = this.getApi().signIn(requestBody);
        call.enqueue(callBack);
    }


    public void signUpApi(String name, String email, String mobile, String password, Callback<SignUp> callback) {
        Map<String, String> params = new HashMap();
        params.put("name", name);
        params.put("email", email);
        params.put("mobile", mobile);
        params.put("password", password);
        String requestString = (new JSONObject(params)).toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), requestString.getBytes());
        Call<SignUp> call = this.getApi().loginWithPhone(requestBody);
        call.enqueue(callback);
    }

    public void sendingOtpToServer(String otp, Callback<OtpResponse> callback) {
        Map<String, String> params = new HashMap();
        params.put("otp", otp);
        String requestString = (new JSONObject(params)).toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), requestString.getBytes());
        Call<OtpResponse> call = this.getApi().sendingOtp(requestBody);
        call.enqueue(callback);
    }

    public void getCategories(Callback<List<CategoryModel>> categoryModelCallback) {
        Call<List<CategoryModel>> call = this.getApi().getCategories();
        call.enqueue(categoryModelCallback);
    }

    public void getPopularPanditList(Callback<List<PopularPanditModel>> listCallback) {
        Call<List<PopularPanditModel>> call = this.getApi().getPopularPandit();
        call.enqueue(listCallback);
    }

    public void getPopularPoojaList(Callback<List<PopularPoojaModel>> listCallback) {
        Call<List<PopularPoojaModel>> call = this.getApi().getPopularPoojaList();
        call.enqueue(listCallback);
    }

    public void getListOfPujas(String cat, Callback<List<PujaModel>> listCallback) {
        Map<String, String> params = new HashMap();
        params.put("main", cat);
        String requestString = (new JSONObject(params)).toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), requestString.getBytes());
        Call<List<PujaModel>> call = this.getApi().getListPujas(requestBody);
        call.enqueue(listCallback);
    }

    public void getPujaDetails(String id, Callback<List<PujaDetailModel>> listCallback) {
        Map<String, String> params = new HashMap();
        params.put("id", id);
        String requestString = (new JSONObject(params)).toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), requestString.getBytes());
        Call<List<PujaDetailModel>> call = this.getApi().getPujaDetails(requestBody);
        call.enqueue(listCallback);
    }


    public void getReviewsList() {


    }

    public void getPujaList() {



    }

    public void getPanditDetails(String id, Callback<PanditDetailsModel> panditDetailsModelCallback) {
        Map<String, String> params = new HashMap();
        params.put("user_id", id);
        String requestString = (new JSONObject(params)).toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), requestString.getBytes());
        Call<PanditDetailsModel> call = this.getApi().getPanditDetails(requestBody);
        call.enqueue(panditDetailsModelCallback);
    }

    public void getPandit(String locationCity, String date, String poojaName, Callback<AvailabilityModel> availabilityModelCallback) {
        Map<String, String> params = new HashMap();
        params.put("city", locationCity);
        params.put("date", date);
        params.put("pooja", poojaName);
        String requestString = (new JSONObject(params)).toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), requestString.getBytes());
        Call<AvailabilityModel> call = this.getApi().getPandit(requestBody);
        call.enqueue(availabilityModelCallback);
    }

    public void signUpApi(String signUpModel, Callback<SignUp> signUpCallback) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1));
        this.api = (WebApi)(new Retrofit.Builder()).client(httpClient.build()).baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(new Gson())).build().create(WebApi.class);

        String requestString = null;
        try {
            requestString = (new JSONObject(signUpModel)).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), requestString.getBytes());
        Call<SignUp> call = this.getApi().loginWithPhone(requestBody);
        call.enqueue(signUpCallback);
    }

    public void signUpApiProfile(String base64Json, String email, Callback<ProfileSignUp> profileSignUpCallback) {
        Map<String, String> params = new HashMap();
        params.put("image", "data:image/png;base64,"+base64Json);
        params.put("email", email);
        String requestString = (new JSONObject(params)).toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE), requestString.getBytes());
        Call<ProfileSignUp> call = this.getApi().upload(requestBody);
        call.enqueue(profileSignUpCallback);

    }

    public void getBookings(String userId, Callback<List<BookingListModel>> listCallback) {
        Map<String, String> values = new HashMap<>();
        values.put("user_id",userId);
        String requestString = new JSONObject(values).toString();
        final RequestBody requestBody = RequestBody.create(MediaType.parse(Constant.MEDIA_TYPE),requestString.getBytes());
        Call<List<BookingListModel>> call = this.getApi().getBookings(requestBody);
        call.enqueue(listCallback);
    }

    public String getProfileUploadPath() {
        return BASE_URL+Constants.PROFILE;
    }
}
