# Movie Manager - REST API Application

Movie Manager is an application that allows you to search for movies, add them to your favorites list, and view your favorite movies. The project focuses on the ability to work with REST API and database integration.

### Functionalities 🔍✨:

    - Search for a Movie:
        Return an object containing title, short description, genre, director and poster.
        Used an external API to retrieve movie data (OMDb API).

    - Adding a Movie to Favorites:
        Add the ability to add movies to your favorites list.

    - Displaying Favorite Movies:
        Return a list of favorite movies.

## Tech stack 🔧📚🛠

Java 17 ☕️

Spring Boot 🌱

Hibernate 🚀

MySql 🗃️

Gradle wrapper 📦

Swagger/REST API 🔄

OMDB API 🎬

Docker 🐳

JUnit5, Mockito 🤖🍹

Flyway ✈️

## How to run ▶️
### Docker way 🐋 🐋 🐋

    build movie-manager docker image -> docker build . -t movie-manager:latest
    
    enter etc dir -> cd etc
    
    run docker compose with dev profile -> docker compose --profile dev up -d
    
    enter swagger -> http://localhost:8080/swagger-ui/index.html

### Intellij way 🟥 🟦 🟪

    enter etc dir -> cd etc
    
    run docker compose with dependencies profile (only mysql) -> docker compose --profile dependencies up -d
    
    compile and run project -> run main method in pl.przemek.moviemanager.MovieManagerApplication class
    
    enter swagger -> http://localhost:8080/swagger-ui/index.html



