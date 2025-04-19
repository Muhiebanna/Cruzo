#!/bin/bash

# Build script for Cruzo app (formerly Bliq clone)
# This script builds the APK file for the Cruzo app

echo "Starting Cruzo app build process..."

# Set up environment variables
ANDROID_HOME=~/android-sdk
BUILD_TOOLS_VERSION=33.0.0
GRADLE_VERSION=8.2

# Create keystore for signing the APK if it doesn't exist
if [ ! -f ~/android_bliq_app/cruzo.keystore ]; then
    echo "Creating keystore for signing the APK..."
    keytool -genkey -v -keystore ~/android_bliq_app/cruzo.keystore -alias cruzo -keyalg RSA -keysize 2048 -validity 10000 -storepass cruzo123 -keypass cruzo123 -dname "CN=Cruzo App, OU=Development, O=Cruzo, L=Cairo, ST=Cairo, C=EG"
fi

# Navigate to the project directory
cd ~/android_bliq_app

# Create local.properties file
echo "Creating local.properties file..."
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Create signing configuration in build.gradle
echo "Updating build.gradle with signing configuration..."
cat >> app/build.gradle << EOF

android {
    // Add signing configuration
    signingConfigs {
        release {
            storeFile file("../cruzo.keystore")
            storePassword "cruzo123"
            keyAlias "cruzo"
            keyPassword "cruzo123"
        }
    }
    
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
    }
}
EOF

# Build the APK using Gradle wrapper
echo "Building the APK..."
$ANDROID_HOME/cmdline-tools/latest/bin/gradle assembleRelease

# Check if build was successful
if [ -f app/build/outputs/apk/release/app-release.apk ]; then
    echo "APK built successfully!"
    cp app/build/outputs/apk/release/app-release.apk ~/cruzo.apk
    echo "APK copied to ~/cruzo.apk"
    echo "Build process completed successfully."
else
    echo "Failed to build APK. Check the build logs for errors."
    exit 1
fi
