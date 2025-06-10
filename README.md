# Picbooker 📸✨

_Connecting talented photographers with clients seeking the perfect session_

A full-stack marketplace platform that streamlines the photography booking experience, making it easy for clients to discover photographers and for photographers to manage their business seamlessly.

---

## 🎯 Project Overview

[Picbooker](https://picbooker.net/) is a comprehensive marketplace designed to bridge the gap between photography professionals and clients. The platform offers an intuitive booking system, real-time communication, and powerful management tools for both sides of the marketplace.

**[🎬 Watch Demo]()**

---

## ✨ Key Features

### For Clients 👥

-   **Smart Search & Discovery**: Find photographers by location, style, and session type
-   **Advanced Filtering**: Filter by price range, gender, etc
-   **Detailed Profiles**: Browse photographer portfolios, reviews, and session information
-   **Seamless Booking**: Book sessions with real-time availability checking
-   **Live Chat**: Communicate directly with photographers before and after booking
-   **Flexible Management**: Easily reschedule or cancel bookings
-   **Review System**: Share feedback and rate your photography experience

### For Photographers 📷

-   **Professional Profiles**: Showcase your work with customizable galleries
-   **Business Management**: Set availability, pricing, and session types
-   **Booking Dashboard**: Manage all appointments in one centralized location
-   **Service Customization**: Define session types, duration, and add-on services
-   **Real-time Communication**: Chat with clients to discuss requirements
-   **Schedule Control**: Set working hours and manage availability

---

## 🛠️ Technologies Used

### Backend

-   **Java Spring Boot** - Robust REST API and business logic
-   **Hibernate** - Object-relational mapping and database operations
-   **PostgreSQL** - Primary database for user data and bookings
-   **STOMP WebSockets** - Real-time messaging and notifications

### Infrastructure & Deployment

-   **Firebase Cloud Storage** - Secure image and file storage
-   **Docker** - Containerization for consistent deployments
-   **Nginx** - Proxy for web sockets and ports
-   **AWS** - Cloud hosting and scalable infrastructure
-   **Cloudflare** - CDN, performance optimization, and proxy

---

## 📱 Application Walkthrough

### Client Journey

![Client Search Process](https://github.com/user-attachments/assets/d4d1004a-67be-4e27-83ab-cc290802379d)
_Clients can easily search and filter photographers based on their specific needs_

![search results](https://github.com/user-attachments/assets/828cc45b-e386-4000-8ccb-54d2b33eb107)
_Search Results_

![Booking Process](https://github.com/user-attachments/assets/e73d8791-d859-41ed-ac40-481950b16ba5)
_Streamlined booking flow with real-time availability and instant confirmation_





### Photographer Dashboard

![Photographer Dashboard](https://github.com/user-attachments/assets/2b646350-f260-4ac8-8e3b-215d7280a7eb)
_Comprehensive dashboard for managing bookings, availability, and client communications_

![Photographer Profile View](https://github.com/user-attachments/assets/c0c1f3eb-b7cd-4691-9189-88bfe75b7106)

_Detailed photographer profiles showcase portfolios, reviews, and available services_

![Photographer Gallery](https://github.com/user-attachments/assets/9b46733c-06ef-489f-8445-a73fdf219c4d)
_Photographer Gallery to upload sample Images_

![Profile Management](https://github.com/user-attachments/assets/1050775c-fd1d-4e70-b1c8-0d955b4d29fc)
_Easy-to-use profile editor for showcasing work and managing service offerings_

---

## 🎨 Logo
![Picbooker Logo](https://github.com/user-attachments/assets/345889e0-34a7-4415-b85e-ca84130f932e)



---


## 🚀 Getting Started

### Prerequisites

-   Java 17+
-   Pg admin
-   Maven

### Installation

1. **Clone the repository**

    ```bash
    git clone https://github.com/yourusername/picbooker.git
    cd picbooker
    ```

2. **Set up environment variables**

    create a .env file and fill it with the necessary variables used in application.yaml

3. **Create database**

    create a database in pgadmin and set up the credentials in application.yaml or .env

4. **Run locally (alternative)**
    ```bash
    mvn spring-boot:run
    ```

The application will be available at `http://localhost:8080`

---

## 🏗️ Architecture

The application follows a microservices-inspired architecture with clear separation of concerns:

-   **API Layer**: RESTful endpoints for all client-server communication
-   **Service Layer**: Business logic and data processing
-   **Data Layer**: PostgreSQL with Hibernate ORM
-   **Real-time Layer**: WebSocket connections for instant messaging
-   **Storage Layer**: Firebase for media files and user uploads

---

## 📄 License

This project is licensed under the [**GNU Affero General Public License v3 (AGPLv3)**](https://www.gnu.org/licenses/agpl-3.0.en.html).

### You may:

-   Use, modify, and distribute the code **if you open-source derivatives**
-   Self-host for personal/non-commercial use

### You must:

-   **Share changes** if you modify/host this code
-   **Contact us for commercial licensing** if you want to:
    -   Use this for a closed-source product
    -   Offer a competing SaaS/service

For commercial use, email: **izughyer@gmail.com**

## 📞 Contact & Support

-   Feedback and contribution on taking this project to the next step is welcome, don't hesitate to get in touch.

<!-- -   **Issues**: [GitHub Issues](https://github.com/yourusername/picbooker/issues)
-   **Email**: support@picbooker.com
-   **Documentation**: [Full Documentation](https://docs.picbooker.com) -->

---

## 🙏 Acknowledgments

-   Thanks to all the photographers and clients who provided feedback during development
-   My teammates in this project, Tamara sharawi (front-end developement) and Rua al-shareef (design)

---

_Made with ❤️ for the photography community_
