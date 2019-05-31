package com.cmpundhir.cm.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    EditText email,pass;
    Button login,signup;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.singup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em,ps;
                em = email.getText().toString();
                ps = pass.getText().toString();
                if(TextUtils.isEmpty(em)){
                    email.setError("Please enter email");
                    return;
                }
                if(TextUtils.isEmpty(ps)){
                    pass.setError("Please enter password");
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(em).matches()){
                    email.setError("Please enter valid email");
                    return;
                }
                if(!isValidPassword(ps)){
                    pass.setError("Please enter a strong password which will contain lower case , upper case,numeric and special symbols.");
                    return;
                }
                firebaseLogin(em,ps);
            }
        });
    }

    private void firebaseLogin(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void updateUI(FirebaseUser user){
        Toast.makeText(this, "welcome "+user.getEmail(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
