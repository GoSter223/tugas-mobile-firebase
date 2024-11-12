package com.example.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class BlogActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView textView;  // TextView untuk menampilkan data blog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Cari tombol dengan id btn_add
        Button btnAdd = findViewById(R.id.btn_add);

        // Set onClickListener untuk navigasi ke AddBlogActivity
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BlogActivity.this, AddBlogActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Cari TextView untuk menampilkan data blog
        textView = findViewById(R.id.textView);

        // Ambil dan tampilkan data blog dari Firestore
        fetchBlogsFromFirestore();
    }

    private void fetchBlogsFromFirestore() {
        // Ambil data dari koleksi "blog"
        db.collection("blog")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder blogs = new StringBuilder();
                        QuerySnapshot documentSnapshots = task.getResult();
                        for (QueryDocumentSnapshot document : documentSnapshots) {
                            // Ambil data dari setiap dokumen
                            String title = document.getString("title");
                            String image = document.getString("image");
                            String content = document.getString("content");

                            // Gabungkan data menjadi satu string
                            blogs.append("Title: ").append(title).append("\n");
                            blogs.append("Email: ").append(image).append("\n");
                            blogs.append("Content: ").append(content).append("\n\n");
                        }
                        // Tampilkan data di TextView
                        textView.setText(blogs.toString());
                    } else {
                        // Jika gagal mengambil data
                        Toast.makeText(BlogActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
