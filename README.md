<img src="https://penguin.upyun.galvincdn.com/logos/penguin_stats_logo.png"
     alt="Penguin Statistics - Logo"
     width="96px" />

# Penguin Statistics!
[![License](https://img.shields.io/github/license/penguin-statistics/backend)](https://github.com/penguin-statistics/backend/blob/master/LICENSE)
[![Build Status](https://img.shields.io/travis/penguin-statistics/backend?logo=travis)](https://travis-ci.org/penguin-statistics/backend)
[![Last Commit](https://img.shields.io/github/last-commit/penguin-statistics/backend)](https://github.com/penguin-statistics/backend/commits/dev)
[![Docs](https://img.shields.io/badge/docs-GitBook-blue)](https://developer.penguin-stats.io)

This is the **backend** project repository for the [Penguin Statistics](https://penguin-stats.io/?utm_source=github) website.

This repository will not be maintained any more. Please visit the repository of the latest version from [here](https://github.com/penguin-statistics/backend-next).

## Technologies
- [Maven](https://maven.apache.org/)
- [Spring Boot 2.1.6](https://spring.io/projects/spring-boot)
- [MongoDB](https://www.mongodb.com/)

## Maintainers
This frontend project has mainly being maintained by the following contributors (in alphabetical order):
- [AlvISsReimu](https://github.com/AlvISsReimu)
- [ChaosNiku](https://github.com/ChaosNiku)
- [YukiC](https://github.com/cyj5230)

> The full list of active contributors of the *Penguin Statistics* project can be found at the [Team Members page](https://penguin-stats.io/about/members) of the website.

## How to contribute?
Our contribute guideline can be found at [Penguin Developers](https://developer.penguin-stats.io). PRs are always more than welcome!

## API docs
- [Introductions to API v2](https://developer.penguin-stats.io/docs/)
- [Swagger API docs](https://penguin-stats.io/PenguinStats/swagger/swagger-ui.html)

# Deployment
## Preparations
1. Install Maven
2. Install [Lombok](https://projectlombok.org/) plugin for your IDE
3. (Optional) Install MongoDB
4. (Optional) If you choose to run MongoDB locally, feel free to contact our team for the dumped testing file, execute the following command to import them:
```
mongorestore -h localhost:<your mongodb port> -d penguin_stats <path of penguin_stats directory>
```
5. Edit `src/main/resources/application.yml`，change the value of `username` and `password` in `spring.data.mongodb.uri` according to your settings.

## Run
1. Execute `mvn spring-boot:run` in the root directory of this project. Or run the main method in `PenguinStatisticsApplication` class.
2. If you see "PenguinStats is running" in the console, congrats!

## Build
Execute `mvn clean package`, then deploy `target/PenguinStats.war` to the server.
