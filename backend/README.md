# pronom-backend

This is the skeleton app for Pronom-backend.

How-to guide:

1. git clone https://github.com/wallscope/pronom-backend.git
2. Use ./gradlew bootRun in pronom-backend directory to launch the app.

Authentication is taken care of by Spring Security - configuration can be found in src/main/resources/application.properties
Default:
user - user
password - password

Available endpoints:

- hello world - http://localhost:8080/hello

The HTML page is a template supported by Thymeleaf that can be edited in src/main/resources/templates/helloworld.html
