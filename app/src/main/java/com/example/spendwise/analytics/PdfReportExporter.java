package com.example.spendwise.analytics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PdfReportExporter {

    private PdfReportExporter() {
    }

    public static File export(
            Context context,
            List<TransactionRecord> records
    ) throws IOException {

        PdfDocument document = new PdfDocument();

        Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextSize(22);
        titlePaint.setTypeface(Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
        ));

        Paint headingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        headingPaint.setTextSize(15);
        headingPaint.setTypeface(Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
        ));

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(12);

        NumberFormat money = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("en-ZM")
        );

        File reportDirectory = new File(
                context.getFilesDir(),
                "reports"
        );

        if (!reportDirectory.exists() && !reportDirectory.mkdirs()) {
            throw new IOException("Could not create report directory.");
        }

        File outputFile = new File(
                reportDirectory,
                "SpendWise_Report.pdf"
        );

        PdfDocument.Page page = null;

        try {
            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(
                            595, 842, 1
                    ).create();

            page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            int x = 40;
            int y = 55;

            canvas.drawText("SpendWise Financial Report", x, y, titlePaint);
            y += 35;

            canvas.drawText(
                    "Total Income: " + money.format(
                            AnalyticsCalculator.getTotalIncome(records)
                    ),
                    x,
                    y,
                    textPaint
            );

            y += 22;

            canvas.drawText(
                    "Total Expenses: " + money.format(
                            AnalyticsCalculator.getTotalExpenses(records)
                    ),
                    x,
                    y,
                    textPaint
            );

            y += 22;

            canvas.drawText(
                    "Balance: " + money.format(
                            AnalyticsCalculator.getBalance(records)
                    ),
                    x,
                    y,
                    textPaint
            );

            y += 38;

            canvas.drawText("Expenses by Category", x, y, headingPaint);
            y += 24;

            Map<String, Double> categories =
                    AnalyticsCalculator.getExpensesByCategory(records);

            for (Map.Entry<String, Double> entry : categories.entrySet()) {
                if (y > 790) {
                    document.finishPage(page);

                    PdfDocument.PageInfo nextPageInfo =
                            new PdfDocument.PageInfo.Builder(
                                    595, 842, document.getPages().size() + 1
                            ).create();

                    page = document.startPage(nextPageInfo);
                    canvas = page.getCanvas();
                    y = 50;
                }

                canvas.drawText(
                        entry.getKey() + ": " + money.format(entry.getValue()),
                        x,
                        y,
                        textPaint
                );

                y += 20;
            }

            document.finishPage(page);
            page = null;

            try (FileOutputStream outputStream =
                         new FileOutputStream(outputFile)) {
                document.writeTo(outputStream);
            }

            return outputFile;

        } finally {
            if (page != null) {
                document.finishPage(page);
            }

            document.close();
        }
    }

}
