package com.cmpundhir.cm.firebasedemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 111;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               updateUI();
            }
        },5000);
    }

    private void updateUI(){
        if(auth.getCurrentUser()==null){
            authUI();
//            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
//            startActivity(intent);
//            finish();
        }else{
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case RC_SIGN_IN:
                updateUI();
        }
    }

    private void authUI(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                                new AuthUI.IdpConfig.TwitterBuilder().build(),
//                                new AuthUI.IdpConfig.GitHubBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build(),
//                                new AuthUI.IdpConfig.PhoneBuilder().build(),
                                new AuthUI.IdpConfig.AnonymousBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }
}
