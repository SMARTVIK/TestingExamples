package com.vs.panditji;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.vs.panditji.model.SignInResponse;
import com.vs.panditji.model.SignUp;
import com.vs.panditji.util.ApiUtil;
import com.vs.panditji.util.L;
import com.vs.panditji.util.MainActivity;
import com.vs.panditji.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    EditText name,email,phone,password,confirmPassword;
    private CircleImageView profileImage;
    private File fileUri;
    private ProgressDialog progressDialog;
    private Bitmap imageBitmap = null;
    private EditText city;
    private EditText availableCity;
    private EditText pin;
    private EditText education;
    private EditText expert;
    private EditText address;
    private DatePickerDialog datePickerDialog;
    private int day;
    private int month;
    private int year;
    private String selectedDate;
    private TextView datePickerButton;
    private EditText state;
    private String base64EncodedString;
    private boolean check = true;
    private SignUp signUp;


    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(this,"","Please wait..");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();

        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCompleteData();
            }
        });

    }

    private void sendProfileImageOnly() {

        ApiUtil.getInstance().signUpApiProfile(base64EncodedString,email.getText().toString().trim(), new Callback<ProfileSignUp>() {

            @Override
            public void onResponse(Call<ProfileSignUp> call, Response<ProfileSignUp> response) {


                if(response.code() == 200 && response.body().getError()==0){

                    final String user = email.getText().toString().trim();
                    final String pass = password.getText().toString().trim();

                    ApiUtil.getInstance().signIn(user, pass, new Callback<SignInResponse>() {

                        @Override
                        public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                            if(response.code() == 200 && response.body().getError() == 0){
                                SharedPreferences sharedPreferences = PanditJi.getInstance().getSharedPrefs();
                                sharedPreferences.edit().putString("user_id", response.body().getUser_id());
                                sharedPreferences.edit().putString("email", user);
                                sharedPreferences.edit().putString("password", pass);
                                L.d("SignIn Successful " + response.body().getError()+" "+response.body().getMsg());
                                ApplicationDataController.getInstance().setUserLoggedIn(true);
                                ApplicationDataController.getInstance().setUserId(response.body().getUser_id());
                                ApplicationDataController.getInstance().setCurrentUserResponse(response.body());
                                startActivity(new Intent(SignUpActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }

                            if(response.code() == 200 && response.body().getError() == 1){
                                L.d("SignIn Unsuccessful "+response.body().getMsg());
                                if(signUp!=null){
                                    TextView signUpResponse = findViewById(R.id.sign_up_response);
                                    signUpResponse.setVisibility(View.VISIBLE);
                                    findViewById(R.id.scroll).setVisibility(View.GONE);
                                    signUpResponse.setText(signUp.getAlert());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SignInResponse> call, Throwable t) {
                            hideProgressDialog();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<ProfileSignUp> call, Throwable t) {

                hideProgressDialog();


            }
        });


    }

    private void sendCompleteData() {
        if(imageBitmap == null){
            Toast.makeText(SignUpActivity.this, "Select Profile Picture First", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!checkValidity()){
            return;
        }

        if(selectedDate == null){
            Toast.makeText(SignUpActivity.this, "Select date of birth", Toast.LENGTH_SHORT).show();
            datePickerDialog.show();
            return;
        }

        showProgressDialog();

        SignUpModel signUpModel = new SignUpModel();
        signUpModel.setName(name.getText().toString().trim());
        signUpModel.setEmail(email.getText().toString().trim());
        signUpModel.setMobile(phone.getText().toString().trim());
        signUpModel.setAddress(address.getText().toString().trim());
        signUpModel.setCity(city.getText().toString().trim());
        signUpModel.setPin(pin.getText().toString().trim());
        signUpModel.setEducation(education.getText().toString().trim());
        signUpModel.setExpert(expert.getText().toString().trim());
        signUpModel.setAvailable_city(availableCity.getText().toString().trim());
        signUpModel.setPassword(password.getText().toString().trim());
//        signUpModel.setImage("data:image/png;base64,"+base64EncodedString);

        signUpModel.setDOB(selectedDate);
        Gson gson = new Gson();
        String json = gson.toJson(signUpModel);


        ApiUtil.getInstance().signUpApi(json, new Callback<SignUp>() {

            @Override
            public void onResponse(Call<SignUp> call, Response<SignUp> response) {
                if (response.code() == 200 && response.body() != null && response.body().getError() == 0) {
                    signUp = response.body();
                    sendProfileImageOnly();
                }
                Log.d("onResponse", response.body().getError() + "");

                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<SignUp> call, Throwable t) {

                 hideProgressDialog();

                Log.d("onFailure", t.getMessage() + "");

            }
        });
    }

    private boolean checkEditText(EditText text){
        if(text.getText().toString().isEmpty()){
            text.setError("can not be empty");
            text.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkValidity() {
        return checkEditText(name) &&
                checkEditText(email) &&
                checkEditText(password) &&
                checkEditText(confirmPassword) &&
                checkEditText(address) &&
                checkEditText(expert) &&
                checkEditText(pin) &&
                checkEditText(availableCity) &&
                checkEditText(city) &&
                checkEditText(education) &&
                checkEditText(state) &&
                checkPasswords() &&
                checkPin();
    }

    private boolean checkPin() {
        if (pin.getText().toString().length() == 6) {
            return true;
        }
        Toast.makeText(this, "Pin must be of 6 digits", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean checkPasswords() {
        if (password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
            return true;
        }
        Toast.makeText(this, "Password and Confirm password should be same", Toast.LENGTH_SHORT).show();
        return false;
    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            day = selectedDay;
            month = selectedMonth;
            year = selectedYear;
            selectedDate = getProperDigit(selectedDay) + " / " + getProperDigit(selectedMonth+1) + " / "
                    + selectedYear;
            datePickerButton.setText(selectedDate);
        }
    };


    private String getProperDigit(int selectedMonth) {
        return (((selectedMonth) < 10) ? ("0"+(selectedMonth)) : ""+(selectedMonth));
    }

    private void initViews() {
        state = findViewById(R.id.state_edit_text);
        education = findViewById(R.id.education_edit_text);
        city = findViewById(R.id.city_edit_text);
        availableCity = findViewById(R.id.av_city_edit_text);
        pin = findViewById(R.id.pin_edit_text);
        expert = findViewById(R.id.expert_edit_text);
        address = findViewById(R.id.address_edit_text);
        name = findViewById(R.id.name_edit_text);
        email = findViewById(R.id.email_edit_text);
        phone = findViewById(R.id.mobile_edit_text);
        password = findViewById(R.id.pass_edit_text);
        confirmPassword = findViewById(R.id.confirm_pass_edit_text);
        profileImage = findViewById(R.id.imageView);
        final Calendar c = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, datePickerListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(c.getTime().getTime());
        datePickerButton = findViewById(R.id.select_date);

        findViewById(R.id.go_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(SignUpActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//        thumbnail = Bitmap.createScaledBitmap(thumbnail, 300, 300, true);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profileImage.setImageBitmap(thumbnail);
        imageBitmap = thumbnail;

        base64EncodedString = Utility.encodeTobase64(imageBitmap);
    }

    private void compressBitmap(Bitmap thumbnail){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private String userChoosenTask;

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
//                bm = Bitmap.createScaledBitmap(bm, 300, 300, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        profileImage.setImageBitmap(bm);

        imageBitmap = bm;

        base64EncodedString = Utility.encodeTobase64(imageBitmap);

    }

    public void imageUploadToServerFunction(final Context context, Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            String emailText , passText = "";

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                emailText = email.getText().toString().trim();
                passText = password.getText().toString().trim();
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                try {
                    JSONObject jsonObject = new JSONObject(string1);

                    if(jsonObject.has("error") && jsonObject.getInt("error")==0){

                        ApiUtil.getInstance().signIn(emailText, passText, new Callback<SignInResponse>() {
                            @Override
                            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {

                                hideProgressDialog();

                                startActivity(new Intent(SignUpActivity.this,HomeActivity.class));
                            }

                            @Override
                            public void onFailure(Call<SignInResponse> call, Throwable t) {

                                hideProgressDialog();

                            }
                        });

                    }else{
                        Toast.makeText(context, ""+jsonObject.get("alert"), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put("image", ConvertImage);
                HashMapParams.put("email", emailText);

                String FinalData = imageProcessClass.ImageHttpRequest(ApiUtil.getInstance().getProfileUploadPath(), HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)
                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }

}
