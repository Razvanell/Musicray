Hi,

This is my music streaming app. I code it to learn new things. 

Currently it has:
- A new modern UI - it's quite pretty. It uses Bootstrap 5.3.6, FA & has some custom animations.
- Mediaplayer with custom controls & an option to find information about songs using AI (A valid openAI token is needed though).
- Login/Registration, secured with Spring Security and JWTToken.
- Navigation bar - adaptable when logged in.
- User tab where playlists can be checked & where a logged-in user can edit their playlists.
- Playlist tab where playlists can be updated with new songs or where the whole library can be found.


I updated it to work with Java 21+ & migrated the build system from Maven to Gradle to gain more flexibility in
configuration.

I also updated the frontend (found here: https://github.com/Razvanell/Musicray-angular) to Angular 19.

It currently runs on an in memory H2 database. However it can run very well with Postgres(deployed with Docker) or MySQL when ran in production mode. Both tested.

When first runnig the app the DatabaseUpdateConfig.java should populate the database if spring.jpa.hibernate.ddl-auto is
set to create. It can be found in application.properties files.

A Maildev server simulates the email verification. Without it new users cannot be registered.

The app can be found on localhost:4200 once everything is running

Have fun!


