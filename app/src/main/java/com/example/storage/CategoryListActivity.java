package com.example.storage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storage.database.AppDatabase;
import com.example.storage.model.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CategoryListActivity extends AppCompatActivity {

    private ListView lvCategories;
    private List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        lvCategories = findViewById(R.id.lvCategories);

        Executors.newSingleThreadExecutor().execute(() -> {
            categories = AppDatabase.getInstance(this).categoryDao().getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            for (Category cat : categories) {
                categoryNames.add(cat.name);
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryNames);
                lvCategories.setAdapter(adapter);
            });
        });

        lvCategories.setOnItemClickListener((parent, view, position, id) -> {
            Category selectedCategory = categories.get(position);
            Intent intent = new Intent(this, ProductListActivity.class);
            intent.putExtra("CATEGORY_ID", selectedCategory.id);
            intent.putExtra("CATEGORY_NAME", selectedCategory.name);
            startActivity(intent);
        });
    }
}