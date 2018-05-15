package com.example.student.myproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.student.myproject.util.UserTestService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void doLogin(View view) {
        //Omoguciti da se iz ove aktivnosti startuje PostsActivity

        UserTestService userTestService = UserTestService.retrofit.create(UserTestService.class);
        final Call<JsonObject> call =
                userTestService.doGetUsers();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String result = response.body().toString();

                JsonObject jsonObject = response.body();

                JsonArray usersJsonArray = jsonObject.get("users").getAsJsonArray();

                EditText editTextUsername = (EditText) findViewById(R.id.txt_username);
                EditText editTextPassword = (EditText) findViewById(R.id.txt_password);

                String usernameForValidation = editTextUsername.getText().toString();
                String passwordForValidation = editTextPassword.getText().toString();

                String userFound = "Not Found";

                for (JsonElement jsonElement : usersJsonArray) {
                    JsonObject user = jsonElement.getAsJsonObject();
                    String username = user.get("username").getAsString();
                    String password = user.get("password").getAsString();

                    if (username.equals(usernameForValidation) && password.equals(passwordForValidation)) {
                        userFound = "User Found";

                        Toast.makeText(LoginActivity.this, userFound, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this, PostsActivity.class);
                        startActivity(intent);

                        finish();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String result = t.getMessage();
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
