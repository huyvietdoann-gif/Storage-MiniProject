package com.example.storage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storage.utils.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private Button btnViewProducts, btnViewCategories, btnLogin, btnLogout;
    private TextView tvLoginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnViewProducts = findViewById(R.id.btnViewProducts);
        btnViewCategories = findViewById(R.id.btnViewCategories);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        tvLoginStatus = findViewById(R.id.tvLoginStatus);

        btnViewProducts.setOnClickListener(v -> startActivity(new Intent(this, ProductListActivity.class)));
        btnViewCategories.setOnClickListener(v -> startActivity(new Intent(this, CategoryListActivity.class)));
        btnLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        btnLogout.setOnClickListener(v -> {
            SharedPrefManager.getInstance(this).logout();
            updateUI();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            tvLoginStatus.setText("Logged in as User ID: " + SharedPrefManager.getInstance(this).getUserId());
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvLoginStatus.setText("Not logged in");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }
}