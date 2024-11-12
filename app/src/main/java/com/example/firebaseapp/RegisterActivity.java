package com.example.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, nameInput, phoneNumberInput;
    private Button registerButton;
    private TextView loginText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi FirebaseAuth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi Views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        nameInput = findViewById(R.id.nameInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        loginText = findViewById(R.id.already_acc);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        // Event klik tombol register
        registerButton.setOnClickListener(v -> registerUser());

        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        // Mengambil input dari pengguna
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email diperlukan");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password diperlukan");
            return;
        }
        if (password.length() < 6) {
            passwordInput.setError("Password harus lebih dari 6 karakter");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Nama diperlukan");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberInput.setError("Nomor telepon diperlukan");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Mendaftarkan user di Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), email, name, phoneNumber);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Pendaftaran gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String email, String name, String phoneNumber) {
        // Membuat objek data pengguna untuk Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name);
        userData.put("phoneNumber", phoneNumber);

        // Menyimpan data ke Firestore di collection "users"
        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();
                    // Pindah ke MainActivity setelah berhasil menyimpan data
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}