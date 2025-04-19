package com.example.bliqclone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bliqclone.R;
import com.example.bliqclone.models.RideRequest;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying ride requests in driver mode
 */
public class RideRequestAdapter extends RecyclerView.Adapter<RideRequestAdapter.RideRequestViewHolder> {
    private List<RideRequest> rideRequests;
    private RideRequestListener listener;
    private NumberFormat currencyFormat;

    public interface RideRequestListener {
        void onAcceptRequest(RideRequest request);
        void onDeclineRequest(RideRequest request);
    }

    public RideRequestAdapter(List<RideRequest> rideRequests, RideRequestListener listener) {
        this.rideRequests = rideRequests;
        this.listener = listener;
        
        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "EG"));
        currencyFormat.setCurrency(Currency.getInstance("EGP"));
    }

    @NonNull
    @Override
    public RideRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride_request, parent, false);
        return new RideRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideRequestViewHolder holder, int position) {
        RideRequest request = rideRequests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return rideRequests.size();
    }

    class RideRequestViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivServiceLogo;
        private TextView tvServiceName;
        private TextView tvRideType;
        private TextView tvPrice;
        private TextView tvPickupLocation;
        private TextView tvDestination;
        private TextView tvDistance;
        private TextView tvEstimatedTime;
        private TextView tvPaymentMethod;
        private Button btnAccept;
        private Button btnDecline;

        public RideRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivServiceLogo = itemView.findViewById(R.id.ivServiceLogo);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvRideType = itemView.findViewById(R.id.tvRideType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPickupLocation = itemView.findViewById(R.id.tvPickupLocation);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvEstimatedTime = itemView.findViewById(R.id.tvEstimatedTime);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }

        public void bind(RideRequest request) {
            // Set service info
            ivServiceLogo.setImageResource(request.getServiceProvider().getIconResourceId());
            tvServiceName.setText(request.getServiceProvider().getDisplayName());
            tvRideType.setText(request.getRideType());
            
            // Set price
            tvPrice.setText(currencyFormat.format(request.getPrice()));
            
            // Set locations
            tvPickupLocation.setText(request.getPickupLocation());
            tvDestination.setText(request.getDestination());
            
            // Set additional info
            tvDistance.setText(request.getFormattedDistance());
            tvEstimatedTime.setText(request.getFormattedEstimatedTime());
            tvPaymentMethod.setText(request.getPaymentMethod());
            
            // Set button listeners
            btnAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptRequest(request);
                }
            });
            
            btnDecline.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeclineRequest(request);
                }
            });
        }
    }
}
