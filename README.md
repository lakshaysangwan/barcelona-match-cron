# FC Barcelona Match Notification Service

## Overview

This project runs a cron job every 2 hours to scrape information about the latest FC Barcelona football matches. It
uses [Jsoup](https://jsoup.org/) to scrape the match data and MongoDB to keep track of email notifications. When a new
match is found, an email notification is sent, and a record is added to MongoDB. The project is deployed on Railway.app.

## Future Scope

- Better formatted emails

## Prerequisites

- Java
- MongoDB
- Gmail Account
- Maven (For Dependency Management)

## Technologies Used

- Java
- Jsoup for Web Scraping
- MongoDB as Database
- Railway.app for Deployment
- Gmail for Sending Emails

## Setup & Installation

### MongoDB

Make sure you have MongoDB running and accessible.

### Gmail Setup

1. Log in to your Gmail account and go to [Manage your Google Account](https://myaccount.google.com/).
2. Navigate to Security -> App Passwords.
3. Generate a new App Password specifically for this project.

### Clone Repository

```bash
git clone https://github.com/lakshaysangwan/barcelona-match-cron.git
```

### Configuration

Replace placeholders in the `application.properties` file with your MongoDB and Gmail credentials.

### Deployment

The project is deployed on Railway.app, follow their [docs](https://docs.railway.app/) for deployment instructions.

## Running on IDE

### IntelliJ IDEA

1. Open the project folder in IntelliJ IDEA.
2. Right-click on the `pom.xml` file and select `Reload Project`.
3. Navigate to the `BarcelonaMatchCronApplication` class, right-click and
   select `Run 'BarcelonaMatchCronApplication.main()'`.

### Eclipse

1. Open Eclipse and go to `File -> Import -> Existing Maven Projects`.
2. Navigate to the project folder and click `Finish`.
3. Right-click on the project in the Project Explorer, go to `Run As -> Java Application`.

## Cron Job

The cron job is configured to run every 2 hours and checks for the latest FC Barcelona matches available on first page.

## Contributing

If you want to contribute, feel free to create a Pull Request.