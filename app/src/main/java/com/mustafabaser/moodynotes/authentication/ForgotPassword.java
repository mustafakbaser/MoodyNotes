package com.mustafabaser.moodynotes.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mustafabaser.moodynotes.R;

public class ForgotPassword extends AppCompatActivity {

    ProgressBar progressBar;
    EditText userEmail;
    Button userPass;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setTitle(R.string.forgetpass_reset_my_pass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar6);
        userEmail = findViewById(R.id.userEmailForget);
        userPass = findViewById(R.id.btnForgotPass);
        firebaseAuth = FirebaseAuth.getInstance();

        userPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(userEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPassword.this, R.string.forgotpass_password_send_to_your_mail, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}