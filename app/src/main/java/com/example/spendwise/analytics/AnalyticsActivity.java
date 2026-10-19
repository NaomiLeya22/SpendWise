package com.example.spendwise.analytics;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spendwise.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

public class AnalyticsActivity extends AppCompatActivity {

    private TextView incomeText;
    private TextView expensesText;
    private TextView balanceText;
    private PieChart categoryPieChart;
    private Button exportButton;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private List<TransactionRecord> records =
            new ArrayList<>();

    private final NumberFormat money =
            NumberFormat.getCurrencyInstance(
                    Locale.forLanguageTag("en-ZM")
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        incomeText = findViewById(R.id.incomeValue);
        expensesText = findViewById(R.id.expenseValue);
        balanceText = findViewById(R.id.balanceValue);
        categoryPieChart = findViewById(R.id.categoryPieChart);
        exportButton = findViewById(R.id.exportReportButton);

        configureChart();

        exportButton.setOnClickListener(view -> exportReport());

        /*
         * Replace this temporary empty list with records loaded from
         * your existing Room database. See the integration instructions
         * below this class.
         */
        displayAnalytics(Collections.emptyList());
    }

    private void configureChart() {
        categoryPieChart.setUsePercentValues(true);
        categoryPieChart.getDescription().setEnabled(false);
        categoryPieChart.setDrawEntryLabels(true);
        categoryPieChart.setEntryLabelTextSize(12f);
        categoryPieChart.setNoDataText(
                "Add expenses to see your spending breakdown."
        );
        categoryPieChart.getLegend().setEnabled(true);
    }

    public void displayAnalytics(List<TransactionRecord> data) {
        records = data == null
                ? new ArrayList<>()
                : new ArrayList<>(data);

        incomeText.setText(
                "Total income: " +
                        money.format(AnalyticsCalculator.getTotalIncome(records))
        );

        expensesText.setText(
                "Total expenses: " +
                        money.format(AnalyticsCalculator.getTotalExpenses(records))
        );

        balanceText.setText(
                "Balance: " +
                        money.format(AnalyticsCalculator.getBalance(records))
        );

        Map<String, Double> categoryTotals =
                AnalyticsCalculator.getExpensesByCategory(records);

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > 0) {
                entries.add(new PieEntry(
                        entry.getValue().floatValue(),
                        entry.getKey()
                ));
            }
        }

        PieDataSet dataSet = new PieDataSet(
                entries,
                "Expenses"
        );

        dataSet.setSliceSpace(3f);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(categoryPieChart));

        categoryPieChart.setData(pieData);
        categoryPieChart.invalidate();
    }

    private void exportReport() {
        exportButton.setEnabled(false);

        executor.execute(() -> {
            try {
                File report = PdfReportExporter.export(
                        getApplicationContext(),
                        records
                );

                runOnUiThread(() -> {
                    exportButton.setEnabled(true);
                    Toast.makeText(
                            this,
                            "PDF report saved inside app storage.",
                            Toast.LENGTH_LONG
                    ).show();


                });

            } catch (IOException exception) {
                runOnUiThread(() -> {
                    exportButton.setEnabled(true);
                    Toast.makeText(
                            this,
                            "Could not generate the report.",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        executor.shutdown();
        super.onDestroy();
    }

}
