package com.example.popularmovies.data;

import java.util.List;

public class MovieExtended {
    private final MovieDetails movieDetails;
    private final List<Video> videos;
    private final List<Review> reviews;

    public MovieExtended(MovieDetails movieDetails, List<Video> videos, List<Review> reviews) {
        this.movieDetails = movieDetails;
        this.videos = videos;
        this.reviews = reviews;
    }

    public MovieDetails getMovieDetails() {
        return movieDetails;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public List<Review> getReviews() {
        return reviews;

    }
}
