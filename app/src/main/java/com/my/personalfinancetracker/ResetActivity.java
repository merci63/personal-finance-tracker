package com.my.personalfinancetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    private EditText email;
    private Button btn_cancel, btn_reset;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.et_email);
        btn_reset = findViewById(R.id.btn_reset);
        btn_cancel = findViewById(R.id.btn_reset_cancel);

        btn_reset.setOnClickListener(v -> resetPassword());
        btn_cancel.setOnClickListener(v -> finish());

    }

    private void resetPassword() {
        String nEmail = email.getText().toString().trim();

        if(TextUtils.isEmpty(nEmail)){
            email.setError("Email is required");
            return;
        }
        mAuth.sendPasswordResetEmail(nEmail).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
            Toast.makeText(ResetActivity.this,"Password reset email sent", Toast.LENGTH_SHORT).show();
            finish();
            }else {
                Toast.makeText(ResetActivity.this, "Failed to send reset email: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}