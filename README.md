Hi, 

This is my music streaming app.

I updated it to work with Java 21 & migrated the build system from Maven to Gradle to gain more flexibility in configuration.

I also updated the frontend (found here: https://github.com/Razvanell/Musicray-angular) to Angular 19.

It currently runs with a MySQL database. However it can run very well with Postgres, which can easily be deployed with Docker.
When first runnig the app the DatabaseUpdateConfig.java should populate the database if spring.jpa.hibernate.ddl-auto is set to create. It can be found in application.properties.

A Maildev server simulates the email verification. Without it new users cannot be registered.



The app runs on localhost:4200

Have fun!


