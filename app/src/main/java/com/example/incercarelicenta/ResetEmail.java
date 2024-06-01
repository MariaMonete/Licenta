package com.example.incercarelicenta;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResetEmail extends AppCompatActivity {

    private static final String TAG = "ResetEmail";

    Button btnSalveaza;
    EditText edtParolaCurenta, edtMail;
    FirebaseUser user;
    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_email);

        edtParolaCurenta = findViewById(R.id.edit_text_current_password);
        edtMail = findViewById(R.id.edit_text_schimba_email);
        btnSalveaza = findViewById(R.id.buton_schimba_email);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        String email = getIntent().getStringExtra("email");
        edtMail.setText(email);

        btnSalveaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtParolaCurenta.getText().toString().isEmpty()) {
                    edtParolaCurenta.setError("Introduceți parola curentă");
                    return;
                }

                if (edtMail.getText().toString().isEmpty()) {
                    edtMail.setError("Introduceți email-ul");
                    return;
                }

                reAuthenticateAndSendVerificationEmail(edtParolaCurenta.getText().toString(), edtMail.getText().toString());
            }
        });
    }

    private void reAuthenticateAndSendVerificationEmail(String currentPassword, final String newEmail) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetEmail.this, "Verificați noul email pentru a confirma schimbarea.", Toast.LENGTH_SHORT).show();
                                addEmailChangeListener();
                            } else {
                                Toast.makeText(ResetEmail.this, "Eroare la trimiterea emailului de verificare: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Eroare la trimiterea emailului de verificare: " + task.getException().getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(ResetEmail.this, "Eroare la re-autentificare: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addEmailChangeListener() {
        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Verificăm dacă email-ul utilizatorului a fost schimbat
                    String newEmail = edtMail.getText().toString();
                    if (user.getEmail().equals(newEmail)) {
                        // Actualizăm email-ul în Firestore
                        updateUserEmailInFirestore(newEmail);
                    } else {
                        // Dacă email-ul nu a fost încă schimbat, încercăm din nou după un timp
                        user.reload().addOnCompleteListener(this);
                    }
                }
            }
        });
    }

    private void updateUserEmailInFirestore(String newEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.update("email", newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetEmail.this, "Email-ul a fost schimbat", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ResetEmail.this, "Eroare la actualizarea emailului în Firestore: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
