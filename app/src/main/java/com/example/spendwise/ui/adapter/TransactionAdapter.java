package com.example.spendwise.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.R;
import com.example.spendwise.data.local.TransactionEntity;

import java.util.ArrayList;
import java.util.List;

    public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {
        private List<TransactionEntity> items = new ArrayList<>();

        public TransactionAdapter(){ }

        public void setItems(List<TransactionEntity> newItems) {
            if (newItems == null) {
                items = new ArrayList<>();
            } else {
                items = newItems;
            }
            notifyDataSetChanged();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            TransactionEntity t = items.get(position);
            String desc = (t.description != null && !t.description.isEmpty()) ? t.description : t.transactionDate;
            holder.tvDesc.setText(desc);
            holder.tvDate.setText(t.transactionDate);
            String sign = "expense".equals(t.type) ? "-" : "+";
            holder.tvAmount.setText((t.type.equals("expense") ? "-" : "+") + String.format("$%.2f", t.amount));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvDesc, tvDate, tvAmount;
            VH(View itemView) {
                super(itemView);
                tvDesc = itemView.findViewById(R.id.tvTransactionDescription);
                tvDate = itemView.findViewById(R.id.tvMonth);
                tvAmount = itemView.findViewById(R.id.tvExpenses);
            }
        }
    }


