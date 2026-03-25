package com.example.storage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storage.database.AppDatabase;
import com.example.storage.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ProductListActivity extends AppCompatActivity {

    private ListView lvProducts;
    private TextView tvListTitle;
    private List<Product> products = new ArrayList<>();
    private int categoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        lvProducts = findViewById(R.id.lvProducts);
        tvListTitle = findViewById(R.id.tvListTitle);

        categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        if (categoryName != null) {
            tvListTitle.setText("Products in " + categoryName);
        }

        loadProducts();

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = products.get(position);
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", selectedProduct.id);
            startActivity(intent);
        });
    }

    private void loadProducts() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            if (categoryId == -1) {
                products = db.productDao().getAllProducts();
            } else {
                products = db.productDao().getProductsByCategory(categoryId);
            }

            List<String> productNames = new ArrayList<>();
            for (Product p : products) {
                productNames.add(p.name + " - $" + p.price);
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
                lvProducts.setAdapter(adapter);
            });
        });
    }
}