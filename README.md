# Upgrade - Back-end Tech Challenge

Test description:
* An underwater volcano formed a new small island in the Pacific Ocean last month. All the conditions on the island seems perfect and it was
  decided to open it up for the general public to experience the pristine uncharted territory.
  The island is big enough to host a single campsite so everybody is very excited to visit. In order to regulate the number of people on the island, it
  was decided to come up with an online web application to manage the reservations. You are responsible for design and development of a REST
  API service that will manage the campsite reservations.

###Assumptions

* The campsite will be free for all.
* The campsite can be reserved for max 3 days.
* The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
* Reservations can be cancelled anytime.
* For sake of simplicity assume the check-in & check-out time is 12:00 AM

###Requirements

* The users will need to find out when the campsite is available. So the system should expose an API to provide information of the
  availability of the campsite for a given date range with the default being 1 month.
* Provide an end point for reserving the campsite. The user will provide his/her email & full name at the time of reserving the campsite
  along with intended arrival date and departure date. Return a unique booking identifier back to the caller if the reservation is successful.
* The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end point(s) to allow
  modification/cancellation of an existing reservation
* Due to the popularity of the island, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping
  date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite.
* Provide appropriate error messages to the caller to indicate the error cases.
* In general, the system should be able to handle large volume of requests for getting the campsite availability.
  There are no restrictions on how reservations are stored as as long as system constraints are not violated.

## Design

The application is organized in several packages, using the MVC pattern. I used Spring Boot, with JPA
and H2 memory database (for simplicity and portability). I've also added Swagger 2 for documentation
on the Rest API.
 
When building this application I tried to follow, mainly, the KISS principle: Keep It 
Simple, Stupid. So, I used some clean code principles, JUnit for test, Java google 
Style Guide and JavaDocs for comments. Since I used JavaDocs, you can check in each 
class their documentation for further details.

All requirements and constraints were tested with JUnit tests. The overlap concurrent issue
was handled at application level through transaction isolation and synchronized methods. Another
approach would be to handle it at database level, if the requirement was a micro service architecture.

## Running the Application

These instructions will tell you how to get this project running on your local machine for 
development, testing and review purposes.

This project has the following requirements:

```
Java 1.8
Maven 3.x
```

### Running

You can run the application from the command line. 

```
cd /path/to/project-root
mvn spring-boot:run
```

You can now check the swagger documentation at _http://localhost:8080/swagger-ui.html_ where you can
find all endpoints and how to use then.

## Tests

To run the tests separately, use the maven command:

 ```
 mvn test
 ```

Several test classes were developed, but only for classes that has some sort of logic. 
Tests were developed aiming to not test only the success case, but other scenarios as also. 

### Authors

* **Olavo Holanda** - *Initial work*
