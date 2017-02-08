package com.esgi.freres.androidproject.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.esgi.freres.androidproject.Fragments.LoginFragment;
import com.esgi.freres.androidproject.R;

import static java.security.AccessController.getContext;

public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sp.getString("token", null);
        if(token != null){
            Intent MainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(MainActivityIntent);
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container, new LoginFragment());
        fragmentTransaction.commit();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
