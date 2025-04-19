package com.example.bliqclone.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bliqclone.R;
import com.example.bliqclone.api.ApiManager;
import com.example.bliqclone.api.RideService;
import com.example.bliqclone.models.Ride;
import com.google.android.material.slider.Slider;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity for making offers on inDrive rides
 */
public class MakeOfferActivity extends AppCompatActivity {
    private Ride ride;
    private ApiManager apiManager;
    private RideService rideService;
    
    // UI elements for making an offer
    private TextView tvServiceName;
    private TextView tvRideType;
    private TextView tvSuggestedPrice;
    private EditText etOfferAmount;
    private Slider sliderOffer;
    private Button btnSendOffer;
    
    // UI elements for offer status
    private FrameLayout offerStatusContainer;
    private TextView tvOfferStatusTitle;
    private ProgressBar progressOffer;
    private LinearLayout layoutDriverResponse;
    private TextView tvDriverName;
    private TextView tvDriverRating;
    private TextView tvVehicleInfo;
    private TextView tvResponseMessage;
    
    // UI elements for counter offer
    private LinearLayout layoutCounterOffer;
    private TextView tvCounterOfferMessage;
    private TextView tvYourOffer;
    private TextView tvCounterOffer;
    private Button btnAcceptCounter;
    private Button btnRejectCounter;
    
    // UI elements for accepted/rejected offers
    private LinearLayout layoutAccepted;
    private Button btnBookAcceptedRide;
    private LinearLayout layoutRejected;
    private Button btnMakeNewOffer;
    
    private Timer statusCheckTimer;
    private NumberFormat currencyFormat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_offer);
        
        // Get the ride from intent
        ride = (Ride) getIntent().getSerializableExtra("ride");
        if (ride == null || !ride.isOfferBased()) {
            Toast.makeText(this, "Invalid ride for offer", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize API manager and ride service
        apiManager = ApiManager.getInstance();
        rideService = apiManager.getRideService();
        
        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "EG"));
        currencyFormat.setCurrency(Currency.getInstance("EGP"));
        
        // Initialize UI elements
        initializeViews();
        setupOfferUI();
        setupListeners();
    }
    
    private void initializeViews() {
        // Offer input views
        tvServiceName = findViewById(R.id.tvServiceName);
        tvRideType = findViewById(R.id.tvRideType);
        tvSuggestedPrice = findViewById(R.id.tvSuggestedPrice);
        etOfferAmount = findViewById(R.id.etOfferAmount);
        sliderOffer = findViewById(R.id.sliderOffer);
        btnSendOffer = findViewById(R.id.btnSendOffer);
        
        // Offer status views
        offerStatusContainer = findViewById(R.id.offerStatusContainer);
        View statusView = offerStatusContainer.findViewById(R.id.tvOfferStatusTitle);
        tvOfferStatusTitle = statusView;
        progressOffer = offerStatusContainer.findViewById(R.id.progressOffer);
        layoutDriverResponse = offerStatusContainer.findViewById(R.id.layoutDriverResponse);
        tvDriverName = offerStatusContainer.findViewById(R.id.tvDriverName);
        tvDriverRating = offerStatusContainer.findViewById(R.id.tvDriverRating);
        tvVehicleInfo = offerStatusContainer.findViewById(R.id.tvVehicleInfo);
        tvResponseMessage = offerStatusContainer.findViewById(R.id.tvResponseMessage);
        
        // Counter offer views
        layoutCounterOffer = offerStatusContainer.findViewById(R.id.layoutCounterOffer);
        tvCounterOfferMessage = offerStatusContainer.findViewById(R.id.tvCounterOfferMessage);
        tvYourOffer = offerStatusContainer.findViewById(R.id.tvYourOffer);
        tvCounterOffer = offerStatusContainer.findViewById(R.id.tvCounterOffer);
        btnAcceptCounter = offerStatusContainer.findViewById(R.id.btnAcceptCounter);
        btnRejectCounter = offerStatusContainer.findViewById(R.id.btnRejectCounter);
        
        // Accepted/rejected views
        layoutAccepted = offerStatusContainer.findViewById(R.id.layoutAccepted);
        btnBookAcceptedRide = offerStatusContainer.findViewById(R.id.btnBookAcceptedRide);
        layoutRejected = offerStatusContainer.findViewById(R.id.layoutRejected);
        btnMakeNewOffer = offerStatusContainer.findViewById(R.id.btnMakeNewOffer);
    }
    
    private void setupOfferUI() {
        // Set ride details
        tvServiceName.setText(ride.getServiceProvider().getDisplayName());
        tvRideType.setText(ride.getRideType());
        
        // Set suggested price
        double suggestedPrice = ride.getSuggestedPrice();
        tvSuggestedPrice.setText(formatCurrency(suggestedPrice));
        
        // Set initial offer amount (90% of suggested price)
        double initialOffer = Math.round(suggestedPrice * 0.9);
        etOfferAmount.setText(String.valueOf(initialOffer));
        
        // Configure slider
        sliderOffer.setValueFrom((float)(suggestedPrice * 0.7)); // 70% of suggested price
        sliderOffer.setValueTo((float)(suggestedPrice * 1.3));   // 130% of suggested price
        sliderOffer.setValue((float)initialOffer);
    }
    
    private void setupListeners() {
        // Slider listener
        sliderOffer.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                etOfferAmount.setText(String.valueOf(Math.round(value)));
            }
        });
        
        // EditText listener
        etOfferAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double value = Double.parseDouble(s.toString());
                    if (value >= sliderOffer.getValueFrom() && value <= sliderOffer.getValueTo()) {
                        sliderOffer.setValue((float)value);
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            }
        });
        
        // Send offer button
        btnSendOffer.setOnClickListener(v -> {
            try {
                double offerAmount = Double.parseDouble(etOfferAmount.getText().toString());
                sendOffer(offerAmount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Counter offer buttons
        btnAcceptCounter.setOnClickListener(v -> acceptCounterOffer());
        btnRejectCounter.setOnClickListener(v -> rejectCounterOffer());
        
        // Accepted/rejected buttons
        btnBookAcceptedRide.setOnClickListener(v -> bookRide());
        btnMakeNewOffer.setOnClickListener(v -> resetOfferUI());
    }
    
    private void sendOffer(double offerAmount) {
        // Update UI to show waiting state
        btnSendOffer.setEnabled(false);
        offerStatusContainer.setVisibility(View.VISIBLE);
        tvOfferStatusTitle.setText(R.string.waiting_for_drivers);
        progressOffer.setVisibility(View.VISIBLE);
        layoutDriverResponse.setVisibility(View.GONE);
        
        // Send offer to API
        rideService.sendOffer(ride, offerAmount, new RideService.RideServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    // Start checking for status updates
                    startStatusChecks();
                } else {
                    showError("Failed to send offer");
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
            }
        });
    }
    
    private void startStatusChecks() {
        // Cancel any existing timer
        if (statusCheckTimer != null) {
            statusCheckTimer.cancel();
        }
        
        // Create new timer to check offer status every 2 seconds
        statusCheckTimer = new Timer();
        statusCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkOfferStatus();
            }
        }, 2000, 2000);
    }
    
    private void checkOfferStatus() {
        rideService.getOfferStatus(ride, new RideService.RideServiceCallback<RideService.OfferStatus>() {
            @Override
            public void onSuccess(RideService.OfferStatus status) {
                runOnUiThread(() -> handleOfferStatus(status));
            }
            
            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> showError(errorMessage));
            }
        });
    }
    
    private void handleOfferStatus(RideService.OfferStatus status) {
        switch (status) {
            case PENDING:
                // Still waiting, do nothing
                break;
                
            case ACCEPTED:
                stopStatusChecks();
                showAcceptedOffer();
                break;
                
            case REJECTED:
                stopStatusChecks();
                showRejectedOffer();
                break;
                
            case COUNTER_OFFERED:
                stopStatusChecks();
                showCounterOffer();
                break;
                
            case EXPIRED:
                stopStatusChecks();
                showExpiredOffer();
                break;
        }
    }
    
    private void showAcceptedOffer() {
        progressOffer.setVisibility(View.GONE);
        layoutDriverResponse.setVisibility(View.VISIBLE);
        
        // Set driver info
        tvDriverName.setText(ride.getDriverName());
        tvDriverRating.setText(String.valueOf(ride.getDriverRating()));
        tvVehicleInfo.setText(ride.getVehicleModel() + "\n" + ride.getVehiclePlate());
        
        // Show accepted message
        tvResponseMessage.setText(R.string.driver_accepted);
        tvResponseMessage.setTextColor(getResources().getColor(R.color.cruzo_success, null));
        
        // Hide counter offer layout
        layoutCounterOffer.setVisibility(View.GONE);
        
        // Show accepted layout
        layoutAccepted.setVisibility(View.VISIBLE);
        layoutRejected.setVisibility(View.GONE);
    }
    
    private void showRejectedOffer() {
        progressOffer.setVisibility(View.GONE);
        layoutDriverResponse.setVisibility(View.VISIBLE);
        
        // Set driver info
        tvDriverName.setText(ride.getDriverName());
        tvDriverRating.setText(String.valueOf(ride.getDriverRating()));
        tvVehicleInfo.setText(ride.getVehicleModel() + "\n" + ride.getVehiclePlate());
        
        // Show rejected message
        tvResponseMessage.setText("Driver rejected your offer");
        tvResponseMessage.setTextColor(getResources().getColor(R.color.cruzo_error, null));
        
        // Hide counter offer layout
        layoutCounterOffer.setVisibility(View.GONE);
        
        // Show rejected layout
        layoutAccepted.setVisibility(View.GONE);
        layoutRejected.setVisibility(View.VISIBLE);
    }
    
    private void showCounterOffer() {
        progressOffer.setVisibility(View.GONE);
        layoutDriverResponse.setVisibility(View.VISIBLE);
        
        // Set driver info
        tvDriverName.setText(ride.getDriverName());
        tvDriverRating.setText(String.valueOf(ride.getDriverRating()));
        tvVehicleInfo.setText(ride.getVehicleModel() + "\n" + ride.getVehiclePlate());
        
        // Show counter offer message
        double counterOffer = ride.getPrice(); // The counter offer is stored in the price field
        tvResponseMessage.setText(getString(R.string.driver_counter_offer, formatCurrency(counterOffer)));
        tvResponseMessage.setTextColor(getResources().getColor(R.color.cruzo_primary, null));
        
        // Show counter offer details
        layoutCounterOffer.setVisibility(View.VISIBLE);
        tvYourOffer.setText(formatCurrency(ride.getUserOffer()));
        tvCounterOffer.setText(formatCurrency(counterOffer));
        tvCounterOfferMessage.setText(getString(R.string.driver_counter_offer, formatCurrency(counterOffer)));
        
        // Hide accepted/rejected layouts
        layoutAccepted.setVisibility(View.GONE);
        layoutRejected.setVisibility(View.GONE);
    }
    
    private void showExpiredOffer() {
        progressOffer.setVisibility(View.GONE);
        layoutDriverResponse.setVisibility(View.VISIBLE);
        
        // Show expired message
        tvResponseMessage.setText("Your offer has expired");
        tvResponseMessage.setTextColor(getResources().getColor(R.color.cruzo_warning, null));
        
        // Hide counter offer layout
        layoutCounterOffer.setVisibility(View.GONE);
        
        // Show rejected layout to allow making a new offer
        layoutAccepted.setVisibility(View.GONE);
        layoutRejected.setVisibility(View.VISIBLE);
    }
    
    private void acceptCounterOffer() {
        double counterOffer = Double.parseDouble(tvCounterOffer.getText().toString().replace("EGP ", ""));
        
        // Show loading state
        btnAcceptCounter.setEnabled(false);
        btnRejectCounter.setEnabled(false);
        progressOffer.setVisibility(View.VISIBLE);
        
        rideService.acceptCounterOffer(ride, counterOffer, new RideService.RideServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    showAcceptedOffer();
                } else {
                    showError("Failed to accept counter offer");
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
            }
        });
    }
    
    private void rejectCounterOffer() {
        // Show loading state
        btnAcceptCounter.setEnabled(false);
        btnRejectCounter.setEnabled(false);
        progressOffer.setVisibility(View.VISIBLE);
        
        rideService.rejectCounterOffer(ride, new RideService.RideServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    showRejectedOffer();
                } else {
                    showError("Failed to reject counter offer");
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
            }
        });
    }
    
    private void bookRide() {
        // Navigate to ride details activity
        finish();
    }
    
    private void resetOffe
(Content truncated due to size limit. Use line ranges to read in chunks)