package com.example.student.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.myproject.model.Role;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.LoginService;
import com.example.student.myproject.util.UserService;
import com.example.student.myproject.util.UserTokenState;
import com.example.student.myproject.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.txt_username);
        etPassword = (EditText) findViewById(R.id.txt_password);

        try
        {
            Intent intent = getIntent();
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
            etUsername.setText(username);
            etPassword.setText(password);
        }
        catch (Exception e){}
    }

    public void doLogin(final View view) {

        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        final User userToLogIn = new User();
        userToLogIn.setUsername(username);
        userToLogIn.setPassword(password);

        LoginService loginService= Util.retrofit.create(LoginService.class);
        final Call<UserTokenState> call =
                loginService.login(userToLogIn);
        call.enqueue(new Callback<UserTokenState>() {
            @Override
            public void onResponse(Call<UserTokenState> call, Response<UserTokenState> response)
            {

                if(response.code() == 401)
                {
                    Toast.makeText(LoginActivity.this, "Bad credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(response.code() == 200)
                {
                    String jwtToken = response.body().getAccessToken();
                    SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("loggedInUserUsername", username);
                    editor.putString("token",  jwtToken);
                    editor.commit();

                    UserService userService = Util.retrofit.create(UserService.class);
                    final Call<User> callNew = userService.getByUsername("Bearer " + jwtToken, username);
                    callNew.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                String userJson = objectMapper.writeValueAsString(response.body());
                                editor.putString("currentUser", userJson);
                                editor.commit();

                                boolean currentUserIsCommentator = false;
                                for(Role role : response.body().getRoles())
                                {
                                    if(role.getRole().equals("ROLE_COMMENTATOR"))
                                    {
                                        currentUserIsCommentator = true;
                                        break;
                                    }
                                }
                                if(currentUserIsCommentator)
                                {
                                    Intent intent = new Intent(LoginActivity.this, PostsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                    intent.putExtra("username", userToLogIn.getUsername());
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
            @Override
            public void onFailure(Call<UserTokenState> call, Throwable t)
            {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
