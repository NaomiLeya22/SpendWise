package com.example.spendwise.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.example.spendwise.R;
import com.example.spendwise.data.local.TransactionEntity;
import com.example.spendwise.ui.viewmodel.TransactionViewModel;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    private TextInputEditText etAmount, etDescription, etDate;
    private RadioGroup rgTransactionType;
    private AutoCompleteTextView actCategory;
    private TransactionViewModel txViewModel;

    private String selectedTransactionDate;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        rgTransactionType = findViewById(R.id.rgTransactionType);
        actCategory = findViewById(R.id.actCategory);
        Button btnSave = findViewById(R.id.btnSaveTransaction);

        txViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        String[] placeholderCats = {"Groceries", "Transport", "Salary"};
        actCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, placeholderCats));

        setupDatePicker();

        btnSave.setOnClickListener(v -> {
            CharSequence amountText = etAmount.getText();
            String amtStr = amountText != null ? amountText.toString().trim() : "";
            if (TextUtils.isEmpty(amtStr)) {
                etAmount.setError("Required");
                return;
            }
            double amt;
            try {
                amt = Double.parseDouble(amtStr);
            } catch (NumberFormatException ex) {
                etAmount.setError("Invalid number");
                return;
            }

            if (amt <= 0) {
                etAmount.setError("Amount must be > 0");
                return;
            }

            CharSequence categoryText = actCategory.getText();
            String category = categoryText != null ? categoryText.toString().trim() : "";
            if (TextUtils.isEmpty(category)) {
                actCategory.setError("Please select a category");
                return;
            }

            CharSequence descText = etDescription.getText();

            String desc = descText != null ? descText.toString().trim() : "";
            String type = rgTransactionType.getCheckedRadioButtonId() == R.id.rbIncome ? "income" : "expense";

            int categoryId = 1;

            TransactionEntity t = new TransactionEntity();
            t.categoryId = categoryId;
            t.amount = amt;
            t.type = type;
            t.description = desc;
            t.transactionDate = selectedTransactionDate; // yyyy-MM-dd for DB
            t.syncStatus = "pending";

            txViewModel.addTransaction(t, result -> runOnUiThread(() -> {
                if (result != null && result > 0) {
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Save failed (validation)", Toast.LENGTH_SHORT).show();
                }
            }));
        });
    }

    private void setupDatePicker() {
        // Initialize with current date
        selectedTransactionDate = databaseFormat.format(calendar.getTime());
        etDate.setText(displayFormat.format(calendar.getTime()));

        etDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);

                        selectedTransactionDate = databaseFormat.format(calendar.getTime());
                        etDate.setText(displayFormat.format(calendar.getTime()));
                    },
                    year,
                    month,
                    day
            );

            dialog.show();
        });
    }
}
