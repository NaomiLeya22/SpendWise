package com.example.spendwise;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QrReceiptActivity extends AppCompatActivity{

    private ImageView qrImage;
    private TextView receiptDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_receipt);

        qrImage = findViewById(R.id.qrImage);
        receiptDetails = findViewById(R.id.receiptDetails);

        String transactionId =
                getIntent().getStringExtra("transactionId");
        String type = getIntent().getStringExtra("type");
        String amount = getIntent().getStringExtra("amount");
        String category = getIntent().getStringExtra("category");
        String date = getIntent().getStringExtra("date");
        String description =
                getIntent().getStringExtra("description");

        if (transactionId == null || amount == null) {
            Toast.makeText(this,
                    "Transaction details are missing",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String receipt =
                "SpendWise Transaction Receipt\n" +
                        "Transaction ID: " + transactionId + "\n" +
                        "Type: " + safe(type) + "\n" +
                        "Amount: ZMW " + amount + "\n" +
                        "Category: " + safe(category) + "\n" +
                        "Date: " + safe(date) + "\n" +
                        "Description: " + safe(description);

        receiptDetails.setText(receipt);
        generateQRCode(receipt);
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not provided" : value;
    }

    private void generateQRCode(String content) {
        try {
            Bitmap bitmap = QrCodeGenerator.encodeToBitmap(content, 600, 600);
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Could not generate QR code",
                    Toast.LENGTH_LONG).show();
        }
    }
}
