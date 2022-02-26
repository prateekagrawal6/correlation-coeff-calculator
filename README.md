# correlation-coeff-calculator
Accenture Netherland

# Read Me First
Accenture's Correlation Coefficient Calculator
* The program facilitates you to find out the correlation coefficient between the death % and the vaccinated % for either the continent or "all" country as the input.

# Getting Started

You can run this project by two ways.
1. java -jar artifact-name, where artifact name is nothing but correlation-coeff-calculator-0.0.1-SNAPSHOT.jar which will get generated after doing a maven clean build.
    command: java -jar correlation-coeff-calculator-0.0.1-SNAPSHOT.jar
2. using IDE, right click and run as Spring Boot application.

Context root: '' ( empty )
Default port: 8080 

## Sample Request/ Response.
Request: http://localhost:8080/v1/coeff/calculate?continent=Europe
Response: -0.5652062907694778 

Request: http://localhost:8080/v1/coeff/calculate?country=all
Response: 0.31984316794795153

Request: http://localhost:8080/v1/coeff/calculate?continent=
Response: value cannot be empty, please enter a valid input

