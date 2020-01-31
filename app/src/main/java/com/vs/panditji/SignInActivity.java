package com.vs.panditji;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vs.panditji.model.SignInResponse;
import com.vs.panditji.util.ApiUtil;
import com.vs.panditji.util.L;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private boolean openBookingScreen;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_up_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        final EditText userName = findViewById(R.id.email_edit_text);
        final EditText password = findViewById(R.id.password_edit_text);

        findViewById(R.id.view_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                password.setInputType(InputType.TYPE_CLASS_TEXT);

            }
        });


        findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(userName.getText().toString().trim().isEmpty()){
                    userName.setError("can not be blank");
                    userName.requestFocus();
                    return;
                }

                if(password.getText().toString().trim().isEmpty()){
                    password.setError("can not be blank");
                    password.requestFocus();
                    return;
                }

                signIn(userName.getText().toString(),password.getText().toString());
            }
        });
    }

    private void signIn(final String user, final String pass) {
        showProgressDialog();

        ApiUtil.getInstance().signIn(user, pass, new Callback<SignInResponse>() {

            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {

                if (response.code() == 200 && response.body().getError() == 0) {
                    SharedPreferences sharedPreferences = PanditJi.getInstance().getSharedPrefs();
                    sharedPreferences.edit().putString("user_id", response.body().getUser_id());
                    sharedPreferences.edit().putString("email", user);
                    sharedPreferences.edit().putString("password", pass);
                    L.d("SignIn Successful " + response.body().getError()+" "+response.body().getMsg());
                    ApplicationDataController.getInstance().setUserLoggedIn(true);
                    ApplicationDataController.getInstance().setUserId(response.body().getUser_id());
                    ApplicationDataController.getInstance().setCurrentUserResponse(response.body());

                    if (openBookingScreen) {
                        finish();
                        setResult(Activity.RESULT_OK, new Intent().putExtra("user_id", response.body().getUser_id()));
                    }else{
                        startActivity(new Intent(SignInActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }

                if(response.code() == 200 && response.body().getError() ==1){
                    Toast.makeText(SignInActivity.this, ""+response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                L.d("onFailure "+t.getMessage());
                hideProgressDialog();
            }
        });
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(this,"","Please wait..");
    }
}
