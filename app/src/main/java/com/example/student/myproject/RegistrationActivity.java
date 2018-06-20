package com.example.student.myproject;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void register(View view) {
        String username = inputUsername.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        
        if("opaopa".equals(username) && "123456".equals(password) && "twins@ludo.bre".equals(email))
            Toast.makeText(this, "Opa opa pogodi me ko iz topa", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Opa opa penis", Toast.LENGTH_SHORT).show();

    }
}
