package com.vs.panditji;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vs.panditji.model.PanditDetailsModel;
import com.vs.panditji.model.PopularPanditModel;
import com.vs.panditji.util.ApiUtil;
import com.vs.panditji.util.L;
import com.vs.panditji.util.Utility;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {


    private BookingAdapter bookingAdapter;
    private TextView name;
    private TextView education;
    private TextView availableCity;
    private CircleImageView image;
    private TextView joined;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();

        setUpBookingsList();

        getPanditProfile(ApplicationDataController.getInstance().getUserId());

    }

    private void initViews() {
        name = findViewById(R.id.name);
        education = findViewById(R.id.education);
        availableCity = findViewById(R.id.available_city);
        joined = findViewById(R.id.join);
        image = findViewById(R.id.profile_image);
        findViewById(R.id.bookings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getAllBookings();

            }
        });

    }

    private void setUpBookingsList() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,RecyclerView.VERTICAL));
        bookingAdapter = new BookingAdapter();
        recyclerView.setAdapter(bookingAdapter);
    }

    private void getAllBookings() {
        ApiUtil.getInstance().getBookings(ApplicationDataController.getInstance().getUserId(),new Callback<List<BookingListModel>>() {

            @Override
            public void onResponse(Call<List<BookingListModel>> call, Response<List<BookingListModel>> response) {
                hideLoader();
                bookingAdapter.setData(response.body());
            }

            @Override
            public void onFailure(Call<List<BookingListModel>> call, Throwable t) {
                hideLoader();
            }
        });
    }

    private void getPanditProfile(String userId) {

        if(!Utility.checkInternetConnection(HomeActivity.this)){
            Toast.makeText(HomeActivity.this, "No Internet Connection!!", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        ApiUtil.getInstance().getPanditDetails(userId, new Callback<PanditDetailsModel>() {

            @Override
            public void onResponse(Call<PanditDetailsModel> call, Response<PanditDetailsModel> response) {


                L.d("onResponse");

                hideLoader();

                if (response.code() == 200 && response.body() != null) {
                    PanditDetailsModel panditDetailsModel = response.body();
                    name.setText(panditDetailsModel.getName());
                    education.setText(panditDetailsModel.getEducation());
                    availableCity.setText(panditDetailsModel.getAvalble_in());
                    joined.setText(panditDetailsModel.getJoin());

                    String baseUrl = "https://vaidiksewa.in/pandit_img/";

                    Glide.with(HomeActivity.this)
                            .load(baseUrl+panditDetailsModel.getId())
                            .into(image);
                }

            }

            @Override
            public void onFailure(Call<PanditDetailsModel> call, Throwable t) {

                L.d("onFailure");

                hideLoader();
            }
        });
    }


    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = ProgressDialog.show(this,"","Please wait..",false);
        }else{
            progressDialog.show();
        }
    }

    private void hideLoader() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
