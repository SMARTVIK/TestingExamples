package com.vs.panditji;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.panditji.model.PanditDetailsModel;
import com.vs.panditji.util.ApiUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {


    private BookingAdapter bookingAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpBookingsList();

        getPanditProfile(ApplicationDataController.getInstance().getUserId());

        getAllBookings();

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

    private void hideLoader() {

    }

    private void getPanditProfile(String userId) {

        ApiUtil.getInstance().getPanditDetails(userId, new Callback<PanditDetailsModel>() {

            @Override
            public void onResponse(Call<PanditDetailsModel> call, Response<PanditDetailsModel> response) {


            }

            @Override
            public void onFailure(Call<PanditDetailsModel> call, Throwable t) {


            }
        });
    }
}
