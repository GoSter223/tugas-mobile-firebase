package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private Button logoutButton, blogButton;
    private TextView namaCard, emailCard, emailNumber;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Firebase Auth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi Views
        logoutButton = findViewById(R.id.logoutButton);
        namaCard = findViewById(R.id.namaCard);
        emailCard = findViewById(R.id.emailCard);
        emailNumber = findViewById(R.id.emailNumber);
        blogButton = findViewById(R.id.blogButton);

        // Dapatkan pengguna yang sedang login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            getUserData(currentUser.getUid());
        }

        // Event klik tombol logout
        logoutButton.setOnClickListener(v -> {
            // Logout dari Firebase
            mAuth.signOut();

            // Arahkan ke LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        blogButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BlogActivity.class);
            startActivity(intent);
        });
    }

    private void getUserData(String userId) {
        // Referensi dokumen pengguna berdasarkan userId di Firestore
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Ambil data dari Firestore dan set ke TextView
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phoneNumber = documentSnapshot.getString("phoneNumber");

                namaCard.setText(name);
                emailCard.setText(email);
                emailNumber.setText(phoneNumber);
            } else {
                Toast.makeText(MainActivity.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
