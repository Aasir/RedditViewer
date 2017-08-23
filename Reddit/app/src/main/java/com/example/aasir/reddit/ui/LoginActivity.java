package com.example.aasir.reddit.ui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aasir.reddit.R;
import com.example.aasir.reddit.RedditAPI;
import com.example.aasir.reddit.model.Account.Login;
import com.example.aasir.reddit.utils.URLs;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.input_email)
    EditText mUsername;
    @BindView(R.id.input_password)
    EditText mPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        btnLogin.setOnClickListener(v -> {
            if(validate()){
                showProgressDialog();
                Log.d(TAG, "Validatation Successful");
                login(mUsername.getText().toString(), mPassword.getText().toString());
                progressDialog.dismiss();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        if (username.isEmpty()) {
            mUsername.setError("enter a username");
            valid = false;
        } else {
            mUsername.setError(null);
        }

        if (password.isEmpty()) {
            mPassword.setError("please fill this field");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    private void login(String username, String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RedditAPI redditAPI = retrofit.create(RedditAPI.class);
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        Call<Login> call = redditAPI.signIn(headerMap, username, username, password, "json");

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                Log.d(TAG, "onResponse: " + response.toString());

                try {
                    String modhash = response.body().getJSON().getData().getModhash();
                    String cookie = response.body().getJSON().getData().getCookie();
                    Log.d(TAG, "onResponse ModHash: " + modhash);
                    Log.d(TAG, "onResponse Cookie: " + cookie);


                    if (!modhash.equals("")) {
                        saveSessionParams(username, modhash, cookie);
                        mUsername.setText("");
                        mPassword.setText("");
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        //Activity is finished and returns to previous activity
                        finish();
                    }
                }
                catch (NullPointerException n){
                    Log.d(TAG, "onResponse: Login Failed" );
                    mUsername.setText("");
                    mPassword.setText("");
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to perform login: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "An Error Occurred", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Authenticating ...");
        progressDialog.show();

        // To Dismiss progress dialog
        //progressDialog.dismiss();
    }

    /**
     * Save the session params if login is successful
     * @param username
     * @param modhash
     * @param cookie
     */
    private void saveSessionParams(String username, String modhash, String cookie){
        SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor   editor = preferences.edit();

        Log.d(TAG, "saveSessionParams: Session Variables: \n" +
            "username: " + username + "\n" +
            "modhash: " + modhash + "\n" +
            "cookie: " + cookie + "\n");

        editor.putString(getResources().getString(R.string.session_username), username);
        editor.putString(getResources().getString(R.string.session_modhash), modhash);
        editor.putString(getResources().getString(R.string.session_cookie), cookie);
        editor.apply();
    }

}
