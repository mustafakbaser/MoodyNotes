package com.mustafabaser.moodynotes.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mustafabaser.moodynotes.MainActivity;
import com.mustafabaser.moodynotes.PrivacyPolicy;
import com.mustafabaser.moodynotes.R;

public class Register extends AppCompatActivity {
    EditText rUserName, rUserEmail, rUserPass, rUserConfPass;
    Button syncAccount;
    TextView loginAct;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle(R.string.register_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Backbutton

        rUserName = findViewById(R.id.userName);
        rUserEmail = findViewById(R.id.userEmailForget);
        rUserPass = findViewById(R.id.password);
        rUserConfPass = findViewById(R.id.passwordConfirm);

        syncAccount = findViewById(R.id.createAccount);
        loginAct = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar4);

        fAuth = FirebaseAuth.getInstance();

        loginAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        syncAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uUserName = rUserName.getText().toString();
                String uUserEmail = rUserEmail.getText().toString();
                String uUserPass = rUserPass.getText().toString();
                String uConfPass = rUserConfPass.getText().toString();

                if (uUserName.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty() || uUserEmail.isEmpty()) {
                    Toast.makeText(Register.this, R.string.register_empty_field, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!uUserPass.equals(uConfPass)) {
                    rUserConfPass.setError(getString(R.string.register_pass_doesnot_match));
                }
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider.getCredential(uUserEmail, uUserPass);
                fAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this, R.string.register_successful, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        FirebaseUser userNav = fAuth.getCurrentUser();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(uUserName) // buraya bi dön
                                .build();
                        userNav.updateProfile(request);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        finish();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, R.string.register_connection_alert, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }


    public void PrivacyPolicy(View view) {
        startActivity(new Intent(this, PrivacyPolicy.class));
        finish();
    }
}
