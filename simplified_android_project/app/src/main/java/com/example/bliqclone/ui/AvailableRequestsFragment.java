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
import com.example.bliqclone.adapters.RideRequestAdapter;
import com.example.bliqclone.api.ApiManager;
import com.example.bliqclone.api.DriverService;
import com.example.bliqclone.models.RideRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying available ride requests to drivers
 */
public class AvailableRequestsFragment extends Fragment implements RideRequestAdapter.RideRequestListener {
    private RecyclerView rvRideRequests;
    private TextView tvNoRequests;
    private RideRequestAdapter adapter;
    private List<RideRequest> rideRequests = new ArrayList<>();
    private ApiManager apiManager;
    private DriverService driverService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_available_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvRideRequests = view.findViewById(R.id.rvRideRequests);
        tvNoRequests = view.findViewById(R.id.tvNoRequests);

        // Initialize API manager and driver service
        apiManager = ApiManager.getInstance();
        driverService = apiManager.getDriverService();

        // Set up adapter
        adapter = new RideRequestAdapter(rideRequests, this);
        rvRideRequests.setAdapter(adapter);

        // Load ride requests
        loadRideRequests();
    }

    /**
     * Load available ride requests from the API
     */
    private void loadRideRequests() {
        driverService.getAvailableRequests(new DriverService.DriverServiceCallback<List<RideRequest>>() {
            @Override
            public void onSuccess(List<RideRequest> result) {
                if (result.isEmpty()) {
                    showNoRequestsMessage();
                } else {
                    showRideRequests(result);
                }
            }

            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
            }
        });
    }

    /**
     * Display ride requests in the RecyclerView
     */
    private void showRideRequests(List<RideRequest> requests) {
        rideRequests.clear();
        rideRequests.addAll(requests);
        adapter.notifyDataSetChanged();
        rvRideRequests.setVisibility(View.VISIBLE);
        tvNoRequests.setVisibility(View.GONE);
    }

    /**
     * Show message when no ride requests are available
     */
    private void showNoRequestsMessage() {
        rvRideRequests.setVisibility(View.GONE);
        tvNoRequests.setVisibility(View.VISIBLE);
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        if (getActivity() != null) {
            // In a real app, show a proper error message
            tvNoRequests.setText("Error: " + message);
            rvRideRequests.setVisibility(View.GONE);
            tvNoRequests.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAcceptRequest(RideRequest request) {
        driverService.acceptRideRequest(request, new DriverService.DriverServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result && getActivity() != null) {
                    // In a real app, navigate to ride details or update UI
                    loadRideRequests(); // Refresh the list
                }
            }

            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
            }
        });
    }

    @Override
    public void onDeclineRequest(RideRequest request) {
        driverService.declineRideRequest(request, new DriverService.DriverServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    // Remove the request from the list
                    rideRequests.remove(request);
                    adapter.notifyDataSetChanged();
                    
                    if (rideRequests.isEmpty()) {
                        showNoRequestsMessage();
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
            }
        });
    }
}
