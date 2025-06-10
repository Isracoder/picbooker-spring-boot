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

## 📱 Application Walkthrough

### Client Journey

![Client Search Process]()
_Clients can easily search and filter photographers based on their specific needs_

![Photographer Profile View]()
_Detailed photographer profiles showcase portfolios, reviews, and available services_

![Booking Process]()
_Streamlined booking flow with real-time availability and instant confirmation_

### Photographer Dashboard

![Photographer Dashboard]()
_Comprehensive dashboard for managing bookings, availability, and client communications_

![Profile Management]()
_Easy-to-use profile editor for showcasing work and managing service offerings_

---

## 🎨 Logo

![Picbooker Logo]()

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
