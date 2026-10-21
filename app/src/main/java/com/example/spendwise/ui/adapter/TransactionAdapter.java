package com.example.spendwise.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.example.spendwise.data.local.TransactionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {
    private List<TransactionEntity> items = new ArrayList<>();

    public TransactionAdapter() { }

    public void setItems(List<TransactionEntity> newItems) {
        if (newItems == null) {
            items = new ArrayList<>();
        } else {
            items = newItems;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TransactionEntity t = items.get(position);
        String desc = (t.description != null && !t.description.trim().isEmpty()) ? t.description : "Transaction";
        holder.tvDesc.setText(desc);
        holder.tvCategory.setText(t.category != null ? t.category : "General");
        holder.tvDate.setText(t.transactionDate != null ? t.transactionDate : "");

        boolean isExpense = "expense".equalsIgnoreCase(t.type);
        String prefix = isExpense ? "- " : "+ ";
        holder.tvAmount.setText(String.format(Locale.getDefault(), "%sZMW %.2f", prefix, t.amount));
        holder.tvAmount.setTextColor(holder.itemView.getContext().getColor(
                isExpense ? android.R.color.holo_red_dark : android.R.color.holo_green_dark
        ));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvDesc, tvCategory, tvDate, tvAmount;

        VH(View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvTransactionDescription);
            tvCategory = itemView.findViewById(R.id.tvTransactionCategory);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
            tvAmount = itemView.findViewById(R.id.tvTransactionAmount);
        }
    }
}
