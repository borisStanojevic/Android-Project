package com.example.student.myproject.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenProvider {

    public static String getToken(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("token", "");
        String token = "Bearer " + jwtToken;
        return token;
    }
}
