package net.filmcity.app.controllers;

import net.filmcity.app.domain.Movie;
import net.filmcity.app.domain.Rating;
import net.filmcity.app.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class MoviesController {

    private final MovieRepository movieRepository;

    @Autowired
    MoviesController(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    @GetMapping("/movies")
    public List<Movie> allMovies() {
        return movieRepository.findAll();
    }



    @PostMapping("/movies")
    public Movie addMovie(@RequestBody Movie movie) {
        movieRepository.save(movie);
        return movie;
    }
    @PutMapping("/movies")
    public Movie updateMovieById(@RequestBody Movie movie) {
        movieRepository.findById(movie.getId()).orElseThrow(MovieNotFoundException::new);
        return movieRepository.save(movie);
    }
    @GetMapping("/movies/{id}")
    public Movie findMovie(@PathVariable Long id){
        return movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
    }

   @DeleteMapping("/movies/{id}")
    public Movie deleteMovieByID(@PathVariable Long id){
        Movie movie = movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
        movieRepository.deleteById(id);
        return movie;
    }
    @PutMapping("/movies/{id}/book")
    public Movie markFilmAsARentedByID(@PathVariable Long id, @RequestParam String renter){
        Movie movie= movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
        movie.setBooked(true);
        movie.setRenter(renter);
        return movieRepository.save(movie);

    }

    @PutMapping("/movies/{id}/return")
    public Movie markFilmAsReturnedByID(@RequestBody Movie movie){
        movieRepository.findById(movie.getId()).orElseThrow(MovieNotFoundException::new);
        movie.setBooked(false);
        movie.setRenter(null);
        return movieRepository.save(movie);
    }

    @PutMapping("/movies/{id}/rating")
    public Movie addRating(@RequestBody Rating rating, @PathVariable Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
        movie.setRating(rating.getScore());
        return movieRepository.save(movie);
    }


}
