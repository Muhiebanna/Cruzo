package com.example.bliqclone.models;

/**
 * Enum representing the different ride service providers available in Egypt
 */
public enum ServiceProvider {
    UBER("Uber"),
    BOLT("Bolt"),
    CAREEM("Careem"),
    INDRIVE("inDrive"),
    SWVL("Swvl"),
    DIDI("DiDi"),
    HALAN("Halan");

    private final String displayName;

    ServiceProvider(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the color resource ID associated with this service provider
     * @return Resource ID for the color
     */
    public int getColorResourceId() {
        switch (this) {
            case UBER:
                return com.example.bliqclone.R.color.cruzo_uber;
            case BOLT:
                return com.example.bliqclone.R.color.cruzo_bolt;
            case CAREEM:
                return com.example.bliqclone.R.color.cruzo_careem;
            case INDRIVE:
                return com.example.bliqclone.R.color.cruzo_indrive;
            case SWVL:
                return com.example.bliqclone.R.color.cruzo_swvl;
            case DIDI:
                return com.example.bliqclone.R.color.cruzo_didi;
            case HALAN:
                return com.example.bliqclone.R.color.cruzo_halan;
            default:
                return com.example.bliqclone.R.color.cruzo_text_primary;
        }
    }

    /**
     * Get the icon resource ID associated with this service provider
     * @return Resource ID for the icon
     */
    public int getIconResourceId() {
        switch (this) {
            case UBER:
                return com.example.bliqclone.R.drawable.ic_uber;
            case BOLT:
                return com.example.bliqclone.R.drawable.ic_bolt;
            case CAREEM:
                return com.example.bliqclone.R.drawable.ic_careem;
            case INDRIVE:
                return com.example.bliqclone.R.drawable.ic_indrive;
            case SWVL:
                return com.example.bliqclone.R.drawable.ic_swvl;
            case DIDI:
                return com.example.bliqclone.R.drawable.ic_didi;
            case HALAN:
                return com.example.bliqclone.R.drawable.ic_halan;
            default:
                return com.example.bliqclone.R.drawable.ic_default_service;
        }
    }
}
