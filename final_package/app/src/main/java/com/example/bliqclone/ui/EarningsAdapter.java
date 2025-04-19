package com.example.bliqclone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bliqclone.R;
import com.example.bliqclone.models.EarningRecord;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying earning records in driver mode
 */
public class EarningsAdapter extends RecyclerView.Adapter<EarningsAdapter.EarningViewHolder> {
    private List<EarningRecord> earningRecords;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public EarningsAdapter(List<EarningRecord> earningRecords) {
        this.earningRecords = earningRecords;
        
        // Initialize formatters
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "EG"));
        currencyFormat.setCurrency(Currency.getInstance("EGP"));
        dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public EarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_earning, parent, false);
        return new EarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningViewHolder holder, int position) {
        EarningRecord record = earningRecords.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return earningRecords.size();
    }

    class EarningViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvEarningAmount;
        private ImageView ivServiceLogo;
        private TextView tvServiceInfo;
        private TextView tvTime;
        private TextView tvRoute;
        private TextView tvDistance;

        public EarningViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvDate = itemView.findViewById(R.id.tvDate);
            tvEarningAmount = itemView.findViewById(R.id.tvEarningAmount);
            ivServiceLogo = itemView.findViewById(R.id.ivServiceLogo);
            tvServiceInfo = itemView.findViewById(R.id.tvServiceInfo);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }

        public void bind(EarningRecord record) {
            // Set date and amount
            tvDate.setText(dateFormat.format(record.getDate()));
            tvEarningAmount.setText(currencyFormat.format(record.getAmount()));
            
            // Set service info
            ivServiceLogo.setImageResource(record.getServiceProvider().getIconResourceId());
            tvServiceInfo.setText(record.getServiceInfo());
            
            // Set time and route
            tvTime.setText(record.getTime());
            tvRoute.setText(record.getRoute());
            tvDistance.setText(record.getFormattedDistance());
        }
    }
}
