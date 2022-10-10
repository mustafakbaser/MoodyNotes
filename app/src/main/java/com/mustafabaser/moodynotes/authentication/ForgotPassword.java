package com.mustafabaser.moodynotes.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mustafabaser.moodynotes.MainActivity;
import com.mustafabaser.moodynotes.R;
import com.mustafabaser.moodynotes.Splash;

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

        userPass.setOnClickListener(v -> {
            // Hiding the keyboard when clicked the button
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                Toast.makeText(ForgotPassword.this, R.string.error_try_again, Toast.LENGTH_SHORT).show();
                return;
            }

            if (userEmail.getText().toString().isEmpty()) {
                Toast.makeText(ForgotPassword.this, R.string.login_empty_field, Toast.LENGTH_SHORT).show();
                return;
            } else {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(userEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            displayAlert();
                        } else {
                            Toast.makeText(ForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, Login.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void displayAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.info)
                .setMessage(R.string.alert_email_spam)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                })
                .show();
    }
}
