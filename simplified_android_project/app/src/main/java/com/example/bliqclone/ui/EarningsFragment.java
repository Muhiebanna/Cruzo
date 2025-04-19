package com.example.bliqclone.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bliqclone.R;
import com.example.bliqclone.adapters.EarningsAdapter;
import com.example.bliqclone.api.ApiManager;
import com.example.bliqclone.api.DriverService;
import com.example.bliqclone.models.EarningRecord;
import com.google.android.material.tabs.TabLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for displaying driver earnings
 */
public class EarningsFragment extends Fragment {
    private TabLayout tabLayoutEarnings;
    private TextView tvTotalEarnings;
    private TextView tvCompletedRides;
    private TextView tvOnlineHours;
    private TextView tvAverageRating;
    private RecyclerView rvEarningsHistory;
    private TextView tvNoEarnings;
    
    private EarningsAdapter adapter;
    private List<EarningRecord> earningRecords = new ArrayList<>();
    private ApiManager apiManager;
    private DriverService driverService;
    
    private NumberFormat currencyFormat;
    private int currentPeriod = 0; // 0 = Today, 1 = This Week, 2 = This Month
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earnings, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        tabLayoutEarnings = view.findViewById(R.id.tabLayoutEarnings);
        tvTotalEarnings = view.findViewById(R.id.tvTotalEarnings);
        tvCompletedRides = view.findViewById(R.id.tvCompletedRides);
        tvOnlineHours = view.findViewById(R.id.tvOnlineHours);
        tvAverageRating = view.findViewById(R.id.tvAverageRating);
        rvEarningsHistory = view.findViewById(R.id.rvEarningsHistory);
        tvNoEarnings = view.findViewById(R.id.tvNoEarnings);
        
        // Initialize API manager and driver service
        apiManager = ApiManager.getInstance();
        driverService = apiManager.getDriverService();
        
        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "EG"));
        currencyFormat.setCurrency(Currency.getInstance("EGP"));
        
        // Set up adapter
        adapter = new EarningsAdapter(earningRecords);
        rvEarningsHistory.setAdapter(adapter);
        
        // Set up tab listener
        tabLayoutEarnings.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentPeriod = tab.getPosition();
                loadEarnings(currentPeriod);
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Refresh data when tab is reselected
                loadEarnings(currentPeriod);
            }
        });
        
        // Load initial earnings data
        loadEarnings(currentPeriod);
    }
    
    /**
     * Load earnings data for the selected period
     * @param period 0 = Today, 1 = This Week, 2 = This Month
     */
    private void loadEarnings(int period) {
        String periodType;
        switch (period) {
            case 0:
                periodType = "today";
                break;
            case 1:
                periodType = "week";
                break;
            case 2:
                periodType = "month";
                break;
            default:
                periodType = "today";
        }
        
        driverService.getEarnings(periodType, new DriverService.DriverServiceCallback<DriverService.EarningsData>() {
            @Override
            public void onSuccess(DriverService.EarningsData result) {
                if (result.getEarningRecords().isEmpty()) {
                    showNoEarningsMessage();
                } else {
                    showEarnings(result);
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
            }
        });
    }
    
    /**
     * Display earnings data
     */
    private void showEarnings(DriverService.EarningsData data) {
        // Update summary
        tvTotalEarnings.setText(currencyFormat.format(data.getTotalEarnings()));
        tvCompletedRides.setText(String.valueOf(data.getCompletedRides()));
        tvOnlineHours.setText(formatHours(data.getOnlineHours()));
        tvAverageRating.setText(String.format("%.1f", data.getAverageRating()));
        
        // Update history
        earningRecords.clear();
        earningRecords.addAll(data.getEarningRecords());
        adapter.notifyDataSetChanged();
        
        // Show RecyclerView, hide empty message
        rvEarningsHistory.setVisibility(View.VISIBLE);
        tvNoEarnings.setVisibility(View.GONE);
    }
    
    /**
     * Show message when no earnings data is available
     */
    private void showNoEarningsMessage() {
        // Clear summary
        tvTotalEarnings.setText(currencyFormat.format(0));
        tvCompletedRides.setText("0");
        tvOnlineHours.setText("0h 0m");
        tvAverageRating.setText("0.0");
        
        // Clear history
        earningRecords.clear();
        adapter.notifyDataSetChanged();
        
        // Show empty message, hide RecyclerView
        rvEarningsHistory.setVisibility(View.GONE);
        tvNoEarnings.setVisibility(View.VISIBLE);
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        if (getActivity() != null) {
            // In a real app, show a proper error message
            tvNoEarnings.setText("Error: " + message);
            rvEarningsHistory.setVisibility(View.GONE);
            tvNoEarnings.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Format hours and minutes
     */
    private String formatHours(double hours) {
        int h = (int) hours;
        int m = (int) ((hours - h) * 60);
        return h + "h " + m + "m";
    }
}
