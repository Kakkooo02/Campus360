# Campus360

Campus360 is a smart campus management system built with Java and Python. The project provides role-based dashboards for students, faculty, technicians, and administrators, helping users manage campus services such as maintenance requests, facility bookings, notifications, event scheduling, and AI-assisted campus support.

## Overview

Campus360 was designed as a desktop-based campus service platform. Different users can access different features depending on their role. Students and faculty can submit and track service requests, technicians can manage assigned maintenance tasks, and administrators can approve requests, assign work, manage users, and monitor campus activities.

The project also includes a Python-based AI chatbot component that supports campus-related questions using document-based context.

## Features

* User login and role-based access
* Student, faculty, technician, and admin dashboards
* Maintenance request creation and tracking
* Admin approval and technician assignment workflow
* Technician status updates
* Facility booking system
* Event scheduling and calendar view
* Notification tracking
* Account settings and user management
* AI chatbot support using Python and LangChain
* Text-file and JSON-based data storage for simple persistence

## Tech Stack

| Area             | Technologies                             |
| ---------------- | ---------------------------------------- |
| Main application | Java                                     |
| User interface   | Java Swing                               |
| AI chatbot       | Python, Streamlit, LangChain, OpenAI API |
| Data storage     | Text files, JSON                         |
| Project type     | Desktop application with AI support      |

## Repository Structure

```text
Campus360/
├── AIChatbotWindow.java
├── AccountSettingsWindow.java
├── AdminApproveMaintenance.java
├── AdminDashboard.java
├── AdminEventApproval.java
├── BookingAdmin.java
├── BookingFaculty.java
├── BookingStudent.java
├── CalendarView.java
├── DataStoreTxt.java
├── EventScheduler.java
├── HistoryDialog.java
├── LoginPage.java
├── MainFrame.java
├── MaintenanceRequest.java
├── MaintenanceService.java
├── ManageUsersWindow.java
├── NotificationService.java
├── RoleAccessWindow.java
├── Status.java
├── User.java
├── UsersExt.java
├── app.py
├── ingest.py
├── loaders.py
├── qa_chain.py
├── requirements.txt
├── classrooms.txt
├── events.txt
├── labs.txt
├── notifications.txt
├── request_history.txt
├── requests.txt
├── sports.txt
├── user_context.json
├── users.txt
└── README.md
```

## Main Modules

### Role-Based Dashboards

Campus360 includes separate dashboards for different campus users:

* **Students:** submit service requests, view bookings, check events, and access campus support
* **Faculty:** manage bookings, view campus services, and interact with the system
* **Technicians:** view assigned maintenance tasks and update their progress
* **Administrators:** approve maintenance requests, assign technicians, manage users, and review system activity

### Maintenance Request System

Users can create maintenance requests with details such as the issue, location, and status. Administrators can review requests and assign them to technicians, while technicians can update request progress.

### Booking System

The booking module supports campus facility reservations, including classrooms, labs, and sports facilities.

### Event Scheduling

The event scheduling feature allows campus events to be created, reviewed, approved, and displayed through a calendar-style interface.

### Notification System

The notification system keeps users updated about request status changes, approvals, and other important campus activity.

### AI Chatbot

The AI chatbot component uses Python, Streamlit, LangChain, and OpenAI tools to provide AI-assisted answers based on campus-related information.

## Installation and Setup

### Java Application

1. Clone the repository:

```bash
git clone https://github.com/Kakkooo02/Campus360.git
cd Campus360
```

2. Open the project in a Java IDE such as Eclipse, IntelliJ IDEA, or VS Code.

3. Make sure Java is installed.

4. Run the main application file:

```text
MainFrame.java
```

If your IDE uses another entry point, run the file that starts the login page.

### Python Chatbot

1. Install the required Python packages:

```bash
pip install -r requirements.txt
```

2. Create a `.env` file for your API key:

```text
OPENAI_API_KEY=your_api_key_here
```

3. Run the chatbot application:

```bash
streamlit run app.py
```

## Screenshots

Screenshots will be added to show the main parts of the system, including:

* Login page
* Student dashboard
* Admin dashboard
* Maintenance request page
* Booking page
* AI chatbot page

## What I Learned

Through this project, I practiced:

* Building desktop applications using Java Swing
* Applying object-oriented programming concepts
* Creating role-based access for different users
* Designing maintenance, booking, notification, and event workflows
* Managing simple data storage using text files and JSON
* Connecting a Java application with Python-based AI tools
* Writing and organizing a larger multi-file software project

## Future Improvements

* Replace text-file storage with a database
* Improve the user interface design
* Add password hashing for better security
* Add unit testing
* Package the Java application as an executable file
* Improve error handling and input validation
* Add more detailed documentation for each module
* Improve the AI chatbot’s response accuracy and interface

## Author

Created by [Kakkooo02](https://github.com/Kakkooo02)
