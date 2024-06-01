package com.example.incercarelicenta;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResetNume extends AppCompatActivity {

    EditText edtNumeNou;
    Button btnSalveaza;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_nume);

        edtNumeNou = findViewById(R.id.edit_text_schimba_nume);
        btnSalveaza = findViewById(R.id.buton_schimba_nume);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        String currentNume = getIntent().getStringExtra("nume");
        edtNumeNou.setText(currentNume);

        btnSalveaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String numeNou = edtNumeNou.getText().toString().trim();
                if (numeNou.isEmpty()) {
                    edtNumeNou.setError("Introduceți noul nume de utilizator");
                    return;
                }

                updateUserNameInFirestore(numeNou);
            }
        });
    }

    private void updateUserNameInFirestore(final String numeNou) {
        DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.update("username", numeNou).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetNume.this, "Numele de utilizator a fost schimbat", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ResetNume.this, "Eroare la actualizarea numelui de utilizator: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
