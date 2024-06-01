package com.example.incercarelicenta;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ResetParola extends AppCompatActivity {

    Button btnSalveaza;
    EditText edtParolaCurenta, edtParolaNoua, edtParolaConfirm;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_parola);

        edtParolaCurenta = findViewById(R.id.edit_text_current_password);
        edtParolaNoua = findViewById(R.id.edit_text_schimba_parola);
        edtParolaConfirm = findViewById(R.id.edit_text_schimba_parola_confirmare);
        btnSalveaza = findViewById(R.id.buton_schimba_parola);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        btnSalveaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtParolaCurenta.getText().toString().isEmpty()) {
                    edtParolaCurenta.setError("Introduceți parola curentă");
                    return;
                }

                if (edtParolaNoua.getText().toString().isEmpty()) {
                    edtParolaNoua.setError("Introduceți noua parolă");
                    return;
                }

                if (edtParolaConfirm.getText().toString().isEmpty()) {
                    edtParolaConfirm.setError("Confirmare parolă");
                    return;
                }

                if (!edtParolaNoua.getText().toString().equals(edtParolaConfirm.getText().toString())) {
                    edtParolaConfirm.setError("Parola nu se potrivește");
                    return;
                }

                reAuthenticateAndChangePassword(edtParolaCurenta.getText().toString(), edtParolaNoua.getText().toString());
            }
        });
    }

    private void reAuthenticateAndChangePassword(String currentPassword, final String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetParola.this, "Parola a fost schimbată", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ResetParola.this, "Eroare la schimbarea parolei: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ResetParola.this, "Eroare la re-autentificare: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
