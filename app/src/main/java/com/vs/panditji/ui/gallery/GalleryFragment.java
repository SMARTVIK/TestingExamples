package com.vs.panditji.ui.gallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.panditji.ApplicationDataController;
import com.vs.panditji.BookingAdapter;
import com.vs.panditji.BookingListModel;
import com.vs.panditji.HomeActivity;
import com.vs.panditji.R;
import com.vs.panditji.util.ApiUtil;
import com.vs.panditji.util.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private BookingAdapter bookingAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private View noBookingAvailabel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         setUpBookingsList(view);

         getAllBookings(getContext());
    }

    private void getAllBookings(Context context) {

        if(!Utility.checkInternetConnection(context)){
            Toast.makeText(context, "No Internet Connection!!", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        ApiUtil.getInstance().getBookings(ApplicationDataController.getInstance().getUserId(),new Callback<List<BookingListModel>>() {

            @Override
            public void onResponse(Call<List<BookingListModel>> call, Response<List<BookingListModel>> response) {
                hideLoader();
                if (response.code() == 200 && response.body() != null && !response.body().isEmpty()) {
                    noBookingAvailabel.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    bookingAdapter.setData(response.body());
                }else{
                    noBookingAvailabel.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<BookingListModel>> call, Throwable t) {
                hideLoader();
            }
        });
    }

    private void setUpBookingsList(View view) {
        noBookingAvailabel = view.findViewById(R.id.no_booking);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),RecyclerView.VERTICAL));
        bookingAdapter = new BookingAdapter();
        recyclerView.setAdapter(bookingAdapter);
    }

    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = ProgressDialog.show(getContext(),"","Please wait..",false);
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