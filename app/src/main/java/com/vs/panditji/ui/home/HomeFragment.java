package com.vs.panditji.ui.home;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.vs.panditji.ApplicationDataController;
import com.vs.panditji.HomeActivity;
import com.vs.panditji.R;
import com.vs.panditji.model.PanditDetailsModel;
import com.vs.panditji.util.ApiUtil;
import com.vs.panditji.util.L;
import com.vs.panditji.util.Utility;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView name;
    private TextView education;
    private TextView availableCity;
    private CircleImageView image;
    private TextView joined;
    private TextView verification;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        name = view.findViewById(R.id.name);
        education = view.findViewById(R.id.education);
        availableCity = view.findViewById(R.id.available_city);
        joined = view.findViewById(R.id.join);
        verification = view.findViewById(R.id.verify);
        image = view.findViewById(R.id.profile_image);
        getPanditProfile(ApplicationDataController.getInstance().getUserId(),getContext());
    }

    private void getPanditProfile(String userId, Context context) {
        if(!Utility.checkInternetConnection(context)){
            Toast.makeText(context, "No Internet Connection!!", Toast.LENGTH_SHORT).show();
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
                    verification.setText(panditDetailsModel.getVerify().equals("1")?"Verified":"Not Verified");
                    String baseUrl = "https://vaidiksewa.in/pandit_img/";
                    Glide.with(getActivity())
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

    private ProgressDialog progressDialog;

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