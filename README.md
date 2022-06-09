
# Charisma API @github

[![Build and Verify Charisma API](https://github.com/rti-international-charisma/charisma-api/actions/workflows/build-verify-service.yml/badge.svg)](https://github.com/rti-international-charisma/charisma-api/actions/workflows/build-verify-service.yml)

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

## Getting started

- Clone the repo
- Install Java JDK 11 or greater
- Install [Gradle](https://gradle.org/install/) 

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

##  Related projects

This project has counterpart projects:

- [Charisma-dart: Flutter Web Front end](https://github.com/rti-international-charisma/charisma-dart)
- [Charisma-CMS: Default content for this project](https://github.com/rti-international-charisma/charisma-directus)

## License information

This project is licensed under the [Apache 2.0 open-source license](https://www.apache.org/licenses/LICENSE-2.0)

*This mobile website was developed by RTI International, Wits Reproductive Health and Research Institute, and FHI360 with technical support from Equal Experts and Fluidity Software. It was funded through Digital Square, a PATH-led initiative funded and designed by the United States Agency for International Development, the Bill & Melinda Gates Foundation, and a consortium of other donors. It was made possible by the generous support of the American people through the United States Agency for International Development (USAID). The contents are the responsibility of PATH and do not necessarily reflect the views of USAID or the United States Government.*
