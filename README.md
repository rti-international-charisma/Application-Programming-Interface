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

## Documentation
We use dokka to build docs.
To access the built docs follow these steps
<img width="1000" alt="Docs" src="https://user-images.githubusercontent.com/4729192/124616272-91a4fd80-de93-11eb-81a4-619310d48ffe.png">
<img width="1000" alt="Docs2" src="https://user-images.githubusercontent.com/4729192/124616719-f2ccd100-de93-11eb-81fe-af1788d405de.png">

