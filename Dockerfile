# ─────────────────────────────────────────────────────────────────────────────
# Polyfam Hospital Billing System — Dockerfile
# Purpose: Builds the Android APK inside a consistent, isolated environment.
# Base image provides Java 17 + Android SDK + Gradle pre-installed.
# ─────────────────────────────────────────────────────────────────────────────

FROM mingc/android-build-box:latest

# Set working directory inside the container
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Fix Windows line endings (CRLF -> LF) and make Gradle wrapper executable
RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew

# Accept Android SDK licenses non-interactively
RUN yes | sdkmanager --licenses || true

# Build the debug APK
RUN ./gradlew assembleDebug --no-daemon

# The built APK will be at:
# /app/app/build/outputs/apk/debug/app-debug.apk
