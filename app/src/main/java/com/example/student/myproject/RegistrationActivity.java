package com.example.student.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.myproject.model.User;
import com.example.student.myproject.util.RegisterService;
import com.example.student.myproject.util.UserService;
import com.example.student.myproject.util.Util;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    private EditText inputUsername;
    private TextView labelUsername;
    private EditText inputEmail;
    private TextView labelEmail;
    private EditText inputPassword;
    private TextView labelPassword;
    private Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_registration);

        inputUsername = (EditText) findViewById(R.id.signup_input_username);
        labelUsername = (TextView) findViewById(R.id.signup_label_username);
        inputEmail = (EditText) findViewById(R.id.signup_input_email);
        labelEmail = (TextView) findViewById(R.id.signup_label_email);
        inputPassword = (EditText) findViewById(R.id.signup_input_password);
        labelPassword = (TextView) findViewById(R.id.signup_label_password);
        signUpBtn = (Button) findViewById(R.id.btn_register);

        inputUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    String inputUsernameContent = inputUsername.getText().toString().trim();
                    Pattern pattern = Pattern.compile("\\w{6,21}");
                    Matcher matcher = pattern.matcher(inputUsernameContent);
                    if (!matcher.matches())
                    {
                        labelUsername.setTextColor(Color.RED);
                        labelUsername.setText("Username not valid");
                        labelUsername.setVisibility(View.VISIBLE);
                        signUpBtn.setClickable(false);
                    }
                    else
                    {
                        labelUsername.setTextColor(Color.GREEN);
                        labelUsername.setText("Username valid");
                        labelUsername.setVisibility(View.VISIBLE);
                        signUpBtn.setClickable(true);
                    }
                }
            }
        });

        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    String inputEmailContent = inputEmail.getText().toString().trim();
                    Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(inputEmailContent);
                    if(!matcher.matches())
                    {
                        labelEmail.setTextColor(Color.RED);
                        labelEmail.setText("Email not valid");
                        labelEmail.setVisibility(View.VISIBLE);
                        signUpBtn.setClickable(false);
                    }
                    else
                    {
                        labelEmail.setTextColor(Color.GREEN);
                        labelEmail.setText("Email valid");
                        labelEmail.setVisibility(View.VISIBLE);
                        signUpBtn.setClickable(true);
                    }
                }
            }
        });

        inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    String inputPasswordContent = inputPassword.getText().toString().trim();
                    Pattern pattern = Pattern.compile("\\w{6,21}");
                    Matcher matcher = pattern.matcher(inputPasswordContent);
                    if(!matcher.matches())
                    {
                        labelPassword.setTextColor(Color.RED);
                        labelPassword.setText("Password not valid");
                        labelPassword.setVisibility(View.VISIBLE);
                        signUpBtn.setClickable(false);
                    }
                    else
                    {
                        labelPassword.setTextColor(Color.GREEN);
                        labelPassword.setText("Password valid");
                        labelPassword.setVisibility(View.VISIBLE);
                        signUpBtn.setClickable(true);
                    }
                }
            }
        });

    }

    public void register(final View view) {
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        RegisterService registerService= Util.retrofit.create(RegisterService.class);
        final Call<User> call = registerService.register(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code() == 400)
                {
                    Snackbar.make(view.getRootView(), "Username/Email already exists", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                intent.putExtra("username", response.body().getUsername());
                intent.putExtra("password", response.body().getPassword());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t)
            {
                Snackbar.make(view.getRootView(), t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}
