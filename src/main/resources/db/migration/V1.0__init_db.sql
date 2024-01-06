CREATE TABLE favourite_movie (
    id SERIAL PRIMARY KEY,
    omdbID VARCHAR(255),
    title VARCHAR(255),
    plot VARCHAR(500),
    genre VARCHAR(255),
    director VARCHAR(255),
    poster VARCHAR(255)
);