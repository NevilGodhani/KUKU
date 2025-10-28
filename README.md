# 🐶 KUKU Pet Shop

### 📱 Android E-Commerce Application for Pet Products  
Developed by **Nevil A. Godhani** | Guided by **Prof. Riddhi Joshi**

---

## 🧩 Project Overview

**KUKU Pet Shop** is a mobile e-commerce platform built to provide a seamless shopping experience for pet owners.  
The application enables users to browse, select, and purchase high-quality dog food and accessories directly managed by the admin — ensuring product authenticity and reliability.

The project was originally developed as part of my **BCA 5th Semester (2024)** coursework at **Veer Narmad South Gujarat University**, and later extended with **AI & data-driven components** to align with modern intelligent system design principles.

---

## 🚀 Features

### 🧑‍💻 User Module
- Register and log in securely with Firebase Authentication  
- Browse categorized dog products (food, accessories, grooming items)  
- Search, filter, and add items to the cart  
- Manage wishlist and order history  
- Update delivery address and wallet balance  

### ⚙️ Admin Module
- Add, update, and delete products dynamically  
- Manage inventory and categories  
- Track orders and maintain product database  
- Monitor user activities and app analytics  

### 🤖 AI & Data Features *(Extended Version)*
- Product recommendation system (content-based filtering)  
- Basic sales analytics and trend visualization using Firebase export data  
- Future Scope:
  - Chatbot for pet-care assistance  
  - Dog breed recognition using TensorFlow Lite  

---

## 🧠 Technologies Used

| Layer | Tools & Technologies |
|-------|----------------------|
| **Frontend (UI)** | XML (Android Layouts) |
| **Backend Logic** | Java (Android SDK) |
| **Database** | Firebase Realtime Database |
| **Cloud Tools** | Firebase Authentication, Cloud Storage |
| **AI/ML Add-ons** | Python (pandas, scikit-learn, matplotlib) |
| **IDE** | Android Studio |

---

## 📊 System Design Highlights

- **Software Development Model:** Incremental SDLC  
- **Database Entities:** Users, Admin, Products, Cart, Orders, Wallet, Address  
- **Security:** Firebase Authentication + Encrypted user data  
- **Testing:** Unit testing, integration testing, and system testing  

---

## 🧮 Data Analytics Extension (Optional AI Module)

This section demonstrates how Firebase data can be used to extract insights and train ML models.

### Example Workflow
1. Export sales/order data from Firebase.
2. Clean and preprocess data using Python (`pandas`).
3. Visualize sales trends using `matplotlib` or `seaborn`.
4. Train a simple recommendation model (content-based or collaborative filtering).
5. Integrate results into the Android app (or show in a dashboard).

🧾 *Notebook Example:* `AI_Enhancements/recommendation_model.ipynb`

---

## 📱 Screenshots

| Admin Panel | User Interface |
|--------------|----------------|
| ![Admin Panel](screenshots/admin_panel.png) | ![User UI](screenshots/user_ui.png) |

*(Add your real screenshots in the `/screenshots` folder and update paths.)*

---

## 🛠 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/NevilGodhani/KUKU-PetShop.git
