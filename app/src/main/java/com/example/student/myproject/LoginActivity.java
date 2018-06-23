package com.example.student.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.myproject.model.User;
import com.example.student.myproject.util.UserService;
import com.example.student.myproject.util.Util;

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

        String username = ((TextView)findViewById(R.id.txt_username)).getText().toString();
        final String password = ((TextView)findViewById(R.id.txt_password)).getText().toString();

        UserService userService = Util.retrofit.create(UserService.class);
        final Call<User> call =
                userService.getByUsername(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response)
            {
                User user = response.body();
                if(user == null)
                {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!user.getPassword().equals(password))
                {
                    Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("loggedInUserUsername", user.getUsername());
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                intent.putExtra("username", user.getUsername());
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t)
            {
                Toast.makeText(LoginActivity.this, "No response from server", Toast.LENGTH_SHORT).show();
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

    public void goToRegistration(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }
}
