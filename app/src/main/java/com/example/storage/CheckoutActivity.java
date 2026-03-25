package com.example.storage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storage.database.AppDatabase;
import com.example.storage.model.Order;
import com.example.storage.model.OrderDetail;
import com.example.storage.model.Product;
import java.util.List;
import java.util.concurrent.Executors;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvOrderDetails, tvTotal;
    private Button btnPay;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        tvOrderDetails = findViewById(R.id.tvOrderDetails);
        tvTotal = findViewById(R.id.tvTotal);
        btnPay = findViewById(R.id.btnPay);

        orderId = getIntent().getIntExtra("ORDER_ID", -1);

        loadInvoice();

        btnPay.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                Order order = db.orderDao().getOrderById(orderId);
                if (order != null) {
                    order.status = "Paid";
                    db.orderDao().update(order);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Order Paid successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    });
                }
            });
        });
    }

    private void loadInvoice() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<OrderDetail> details = db.orderDetailDao().getOrderDetailsByOrderId(orderId);
            
            StringBuilder sb = new StringBuilder();
            double total = 0;
            
            for (OrderDetail detail : details) {
                Product product = db.productDao().getProductById(detail.productId);
                if (product != null) {
                    sb.append(product.name).append(" x ").append(detail.quantity)
                            .append(" - $").append(detail.price * detail.quantity).append("\n");
                    total += detail.price * detail.quantity;
                }
            }
            
            final String detailsText = sb.toString();
            final double finalTotal = total;
            
            runOnUiThread(() -> {
                tvOrderDetails.setText(detailsText);
                tvTotal.setText("Total: $" + finalTotal);
            });
        });
    }
}