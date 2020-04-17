package com.mustafabaser.moodynotes.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mustafabaser.moodynotes.MainActivity;
import com.mustafabaser.moodynotes.R;
import com.mustafabaser.moodynotes.Splash;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {

    EditText lEmail, lPassword;
    Button loginNow;
    TextView forgetPass, createAcc;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("MoodyNotes - Giriş Yap");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Backbutton

        lEmail = findViewById(R.id.email);
        lPassword = findViewById(R.id.lPassword);
        loginNow = findViewById(R.id.loginBtn);
        forgetPass = findViewById(R.id.forgotPasword);
        createAcc = findViewById(R.id.createAccount);

        spinner = findViewById(R.id.progressBar3);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        showWarning();

        loginNow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String mEmail = lEmail.getText().toString();
                                            String mPassword = lPassword.getText().toString();

                                            if (mEmail.isEmpty() || mPassword.isEmpty()) {
                                                Toast.makeText(Login.this, "Bu alanlar boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            // ilk girişte tüm anonim notları silmek
                                            spinner.setVisibility(View.VISIBLE);

                                            if (fAuth.getCurrentUser().isAnonymous()) {
                                                FirebaseUser user = fAuth.getCurrentUser();

                                                fStore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Login.this, "Anonim notlar temizlendi.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                //anonim hesapları silmek

                                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Login.this, "Anonim kullanıcı hesabı silindi.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            fAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    Toast.makeText(Login.this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Login.this, "Giriş başarısız! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    spinner.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    });
                createAcc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                    }
                });
            }


        private void showWarning() {
            AlertDialog.Builder warning = new AlertDialog.Builder(this)
                    .setTitle("Emin misiniz?")
                    .setMessage("Başka bir hesaba bağlanmak, kayıtlı tüm notları silecektir. Onları kaydetmek için yeni hesap oluşturun.")
                    .setPositiveButton("Notları Kaydet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), Register.class));
                            finish();
                        }
                    }).setNegativeButton("Kabul", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            warning.show();
        }
}

