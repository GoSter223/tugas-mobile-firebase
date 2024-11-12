package com.example.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddBlogActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private Spinner spinnerImage;
    private Button btnAdd, btnClose;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        spinnerImage = findViewById(R.id.spinner_image);
        btnAdd = findViewById(R.id.btn_add);
        btnClose = findViewById(R.id.btn_close);

        // Setup dropdown with image options
        String[] images = {"Image1", "Image2", "Image3"}; // Example image options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, images);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImage.setAdapter(adapter);

        // Add button click listener
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBlogToFirestore();
            }
        });

        // Close button click listener
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputs();
                finish();
            }
        });
    }

    private void addBlogToFirestore() {
        String title = etTitle.getText().toString().trim();
        String image = spinnerImage.getSelectedItem().toString();
        String content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's email
        FirebaseUser user = mAuth.getCurrentUser();
        String email = (user != null) ? user.getEmail() : "Anonymous";  // Default to "Anonymous" if no user is signed in

        // Create blog data
        Map<String, Object> blog = new HashMap<>();
        blog.put("email", email);  // Add email as the first column
        blog.put("title", title);
        blog.put("image", image);  // Example: this could be an URL in real app
        blog.put("content", content);

        // Add to Firestore collection
        db.collection("blog")
                .add(blog)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddBlogActivity.this, "Blog added", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    Intent intent = new Intent(AddBlogActivity.this, BlogActivity.class);
                    startActivity(intent);
                    finish();  // Go back to BlogActivity
                })
                .addOnFailureListener(e -> Toast.makeText(AddBlogActivity.this, "Failed to add blog", Toast.LENGTH_SHORT).show());
    }

    private void clearInputs() {
        etTitle.setText("");
        etContent.setText("");
        spinnerImage.setSelection(0);
    }
}
