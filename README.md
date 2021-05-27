[![Build and Verify Charisma API](https://github.com/rti-international-charisma/charisma-api/actions/workflows/build-verify-service.yml/badge.svg)](https://github.com/rti-international-charisma/charisma-api/actions/workflows/build-verify-service.yml)

# Charisma API

## API

### Auth
- POST signup
- POST Login
- POST Reset Password

### Content
- GET site 'content'
- GET counselling modules
- GET assessments
- GET referrals

### User
- GET assessment score
- POST assessment score

## Usage

./gradlew clean build

- runs the unit tests
- runs the integration tests with stub response using wiremock  
- packages and publishes fat jar
