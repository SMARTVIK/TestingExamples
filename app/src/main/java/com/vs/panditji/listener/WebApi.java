package com.vs.panditji.listener;


import com.vs.panditji.BookingListModel;
import com.vs.panditji.ProfileSignUp;
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

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WebApi {


    @POST("register_p.php")
    Call<SignUp> loginWithPhone(@Body RequestBody requestBody);

    @POST("verify_otp.php")
    Call<OtpResponse> sendingOtp(@Body RequestBody requestBody);

    @POST("main_cat")
    Call<List<CategoryModel>> getCategories();

    @POST("pandit")
    Call<List<PopularPanditModel>> getPopularPandit();

    @POST("home_cat")
    Call<List<PopularPoojaModel>> getPopularPoojaList();

    @POST("cat")
    Call<List<PujaModel>> getListPujas(@Body RequestBody requestBody);

    @POST("user_profile")
    Call<List<UserProfileModel>> getUserProfile(@Body RequestBody requestBody);

    @POST("login")
    Call<SignInResponse> signIn(@Body RequestBody requestBody);

    @POST("pooja_details")
    Call<List<PujaDetailModel>> getPujaDetails(@Body RequestBody requestBody);

    @POST("pandit_profile")
    Call<PanditDetailsModel> getPanditDetails(@Body RequestBody requestBody);

    @POST("check_availability")
    Call<AvailabilityModel> getPandit(@Body RequestBody requestBody);

    @POST("profile.php")
    Call<ProfileSignUp> sendProfile(@Body RequestBody requestBody);

    @POST("profile.php")
    Call<ProfileSignUp> upload(@Body RequestBody file);

    @POST("booking.php")
    Call<List<BookingListModel>> getBookings(RequestBody requestBody);
}
