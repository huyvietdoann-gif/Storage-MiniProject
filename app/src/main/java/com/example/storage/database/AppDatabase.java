package com.example.storage.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.storage.dao.*;
import com.example.storage.model.*;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "shopping_db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Prepopulate database
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        AppDatabase database = getInstance(context);
                                        // Categories
                                        long cat1 = database.categoryDao().insert(new Category("Electronics"));
                                        long cat2 = database.categoryDao().insert(new Category("Clothing"));

                                        // Products
                                        database.productDao().insert(new Product("Smartphone", 500, "Latest model", (int) cat1));
                                        database.productDao().insert(new Product("Laptop", 1200, "High performance", (int) cat1));
                                        database.productDao().insert(new Product("T-Shirt", 20, "Cotton", (int) cat2));

                                        // Default User
                                        database.userDao().insert(new User("admin", "123", "admin@example.com"));
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}