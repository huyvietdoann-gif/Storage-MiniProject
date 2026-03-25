package com.example.storage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storage.database.AppDatabase;
import com.example.storage.model.Order;
import com.example.storage.model.OrderDetail;
import com.example.storage.model.Product;
import com.example.storage.utils.SharedPrefManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductPrice, tvProductDescription;
    private Button btnAddToCart;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);

        Executors.newSingleThreadExecutor().execute(() -> {
            product = AppDatabase.getInstance(this).productDao().getProductById(productId);
            runOnUiThread(() -> {
                if (product != null) {
                    tvProductName.setText(product.name);
                    tvProductPrice.setText("$" + product.price);
                    tvProductDescription.setText(product.description);
                }
            });
        });

        btnAddToCart.setOnClickListener(v -> {
            if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                addToCart();
            }
        });
    }

    private void addToCart() {
        int userId = SharedPrefManager.getInstance(this).getUserId();
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Order pendingOrder = db.orderDao().getPendingOrderByUserId(userId);
            
            long orderId;
            if (pendingOrder == null) {
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Order newOrder = new Order(userId, currentDate, "Pending");
                orderId = db.orderDao().insert(newOrder);
            } else {
                orderId = pendingOrder.id;
            }

            db.orderDetailDao().insert(new OrderDetail((int) orderId, product.id, 1, product.price));

            runOnUiThread(() -> {
                new AlertDialog.Builder(this)
                        .setTitle("Success")
                        .setMessage("Product added to cart. Continue shopping?")
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No (Checkout)", (dialog, which) -> {
                            Intent intent = new Intent(this, CheckoutActivity.class);
                            intent.putExtra("ORDER_ID", (int) orderId);
                            startActivity(intent);
                            finish();
                        })
                        .show();
            });
        });
    }
}