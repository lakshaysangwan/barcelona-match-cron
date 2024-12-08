# Barcelona Match Notification System

An automated system that checks for new Barcelona football match replays and sends email notifications to subscribed
users.

## Features

- Automated hourly checks for new Barcelona match replays
- Email notifications when new matches are available
- Prevents duplicate notifications using database tracking
- Configurable for different football teams
- Runs serverless on AWS Lambda

## Technical Architecture

### AWS Services Used

- **AWS Lambda**: Hosts the main application
- **Amazon EventBridge**: Triggers the Lambda function hourly
- **AWS Budget**: Monitors costs and sends alerts if spending occurs

### Database

- **Supabase PostgreSQL**: Stores processed match records
- Uses connection pooling for optimal performance

### Email Service

- Gmail SMTP for sending notifications
- Configurable email templates

## Configuration

### Lambda Configuration

- Memory: 512MB
- Timeout: 5 minutes
- Runtime: Java 11
- Handler: `com.lakshay.barcelonamatchcron.LambdaHandler`

### EventBridge Schedule

- Runs hourly
- Timezone: Asia/Kolkata
- Rule Name: barcelona-match-cron-hourly

### Resource Usage (Monthly Estimates)

- Lambda Invocations: 720 (well within free tier)
- Compute Time: ~19,000 GB-seconds (within 400,000 GB-seconds free tier)
- EventBridge Events: 720 (within 1M free tier)

## Environment Variables Required

- `SUPABASE_PASSWORD`: Database password
- `SENDING_EMAIL_ADDRESS`: Gmail address for sending notifications
- `SENDING_EMAIL_APP_PASSWORD`: Gmail app-specific password

## Development

### Prerequisites

- Java 11 or higher
- Maven
- AWS CLI configured with appropriate permissions

### Local Testing

```bash
# Run the main application locally
mvn spring-boot:run

# Run tests
mvn test
```

### Deployment

The application is deployed on AWS Lambda and can be updated using:

```bash
mvn clean package
# Upload the generated JAR to AWS Lambda
```

## Monitoring

### AWS Budget Alerts

- Configured to alert on any spending above $0
- Email notifications enabled for budget alerts

### Lambda Monitoring

- CloudWatch metrics available for:
    - Execution duration
    - Memory usage
    - Error rates
    - Invocation counts

## Database Schema

### mail_record Table

- `id`: Long (Primary Key)
- `title`: String (Match title)
- `url`: String (Match replay URL)
- `created_date`: Timestamp
- `updated_date`: Timestamp

## Future Improvements

- Add support for more teams
- Implement match quality filtering
- Add match highlights detection
- Enhance email template customization

## Contributing

Feel free to submit issues and enhancement requests!

## License

[Add your license here]