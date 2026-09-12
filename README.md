# 🛠️ Home Buddy Captain

> **The Companion Android App for Home Service Professionals**  
> *Empowering service providers (Captains) to receive, manage, and complete service requests from customers in real-time.*

---

## 📲 Direct APK Download & Installation

The ready-to-use Android application package (APK) is built and included in this repository:

| File Name | Location | Size | Type |
|---|---|---|---|
| **HomeBuddyCaptain.apk** | [`apk/HomeBuddyCaptain.apk`](apk/HomeBuddyCaptain.apk) | ~20.4 MB | Debug APK (Ready to Install) |

### How to Install:

#### Method 1: Install via ADB (Recommended for Developers)
Connect your Android phone with USB Debugging enabled (or start an Android Emulator) and run:
```bash
adb install apk/HomeBuddyCaptain.apk
```

#### Method 2: Direct Install on Android Device
1. Transfer `HomeBuddyCaptain.apk` to your phone via USB cable, Google Drive, or WhatsApp.
2. Tap the APK file in your phone's File Manager.
3. If prompted, toggle **"Allow installation from this source"**.
4. Tap **Install** and open the app!

---

## 📖 About The Application

**Home Buddy Captain** is an Android mobile app designed specifically for local home repair and service professionals—called **Captains**. 

Whether you are an **Electrician, Plumber, Appliance Technician, Cleaner, or Broadband Specialist**, this app gives you a complete digital workstation right in your pocket.

### How it fits into the Home Buddy System:
- **Customers** use the *HomieShifter* app to search for nearby help and send work requests.
- **Captains (Service Providers)** use this *Home Buddy Captain* app to receive job requests, view customer problem descriptions, accept or decline jobs, and track completed tasks.

---

## ✨ Key Features

### 1. 📝 Quick & Easy Registration
- **Role-Based Setup**: Select your exact trade (*Electrician*, *Plumber*, *BroadBand Connection*, *Cleaner*, or *Appliance Technician*).
- **Two-Step Form**:
  - *Personal Info*: Name, Email, Password, Age, Mobile Number, Gender.
  - *Job Details*: Service Category, Charges, Years of Experience, Working Hours, and Locality.
- **High-Security Passwords**: Uses industry-standard **BCrypt encryption** to ensure passwords are safe and never stored as plain text.

### 2. 📍 Smart Location & Service Areas
- **GPS Location Detection**: Automatically detects your current city and locality using Google Play Services.
- **Choose Your Service Areas**: Pick the exact neighborhoods and sub-localities where you want to accept jobs.
- **Works in Rural Areas**: Built to handle areas without strict postal or sub-locality data smoothly.

### 3. 📥 Live Work Requests (Home Screen)
- **Instant Job Alerts**: When a customer requests a service in your category and area, it pops up immediately on your home screen.
- **Request Details**: Tap any request to read the customer's full problem description, name, and contact number.
- **Accept or Decline**:
  - Tap **Accept** to take the job—this moves it into your active tasks list and updates the customer.
  - Tap **Decline** if you are busy or unavailable.
- **Smooth Visuals**: Skeleton shimmer loading effect and friendly animations when you have no pending tasks.

### 4. 📋 Task Tracking & History (History Screen)
- **Active Jobs**: See all jobs you have accepted that are currently in progress.
- **Finish / Close Job**: Once your work is done, tap **Finish** to mark the job as completed on both your app and the customer's app.
- **Completed Archive**: A full record of all your past jobs for easy reference.

### 5. 👤 Profile & Portfolio Management
- **Profile Photo**: Pick and set your profile avatar directly from your phone's gallery.
- **Work Showcase**: Upload up to 3 showcase photos of your recent work to impress potential customers.
- **Edit Details**: Keep your mobile number, email, and bio up to date.
- **Password Reset**: Re-authenticate and update your password securely anytime.
- **Offline Shield**: Built-in internet monitor warns you if your Wi-Fi or mobile data drops so you don't miss customer requests.

---

## 🏗️ How Data Syncs (Architecture)

The app syncs between Captains and Customers using Google Firebase Realtime Database:

```
┌─────────────────────────┐          ┌─────────────────────────┐
│   Customer Android App  │          │ Home Buddy Captain App  │
│      (HomieShifter)     │          │    (Service Provider)   │
└────────────┬────────────┘          └────────────┬────────────┘
             │                                    │
             │ Sends Service Request              │ Accepts / Declines
             ▼                                    ▼
┌──────────────────────────────────────────────────────────────┐
│                  Firebase Realtime Database                  │
│                                                              │
│  ├── Registered Service Man / <Category> / <CaptainUID>      │
│  ├── Registered ServiceMan User / <CaptainUID>               │
│  └── service_requests /                                      │
│        ├── pending / <CaptainUID> / <UserUID>                │
│        ├── active / <CaptainUID> / <UserUID>                 │
│        └── completed / <CaptainUID> / <UserUID>              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🧰 Built With (Tech Stack)

| Technology | Purpose |
|---|---|
| **Java** | Core Android programming language |
| **Firebase Auth** | User authentication & account management |
| **Firebase Realtime Database** | Live synchronization of work requests and profiles |
| **Google Play Fused Location** | GPS location and Geocoding service |
| **jBCrypt** | Secure password hashing (work factor 12) |
| **Lottie Animations** | Vector animations for smooth empty states |
| **Facebook Shimmer** | Skeleton placeholder loading screens |
| **Glide & Picasso** | Fast and efficient image loading |

---

## 📱 Permissions Required

- **Internet & Network State**: To connect to Firebase and check if your device is online.
- **Fine & Coarse Location**: To detect your GPS coordinates and assign service areas.

---

## 🚀 Setting Up the Project in Android Studio

1. **Open the Project**: Open the `Home-Buddy-Captain/Home-Buddy-Captain` folder in **Android Studio**.
2. **Firebase Setup**: Ensure `app/google-services.json` contains your Firebase project configuration and that Realtime Database is active.
3. **Build & Run**:
   - Press the green **Run** button (`Shift + F10`) or run `./gradlew assembleDebug` in terminal.

---

## 📄 License

This project is created for educational and commercial use under project guidelines.
