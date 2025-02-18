package net.filmcity.app;

import net.filmcity.app.domain.Movie;
import net.filmcity.app.repositories.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MovieRepository movieRepository;


    @BeforeEach
    void tearDown() {
        movieRepository.deleteAll();
    }

    @Test
    void returnsTheExistingMovies() throws Exception {
        movieRepository.deleteAll();
        addSampleMovies();

        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0].title", equalTo("Jurassic Park")))
                .andExpect(jsonPath("$[0].coverImage", equalTo("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg")))
                .andExpect(jsonPath("$[0].director", equalTo("Steven Spielberg")))
                .andExpect(jsonPath("$[0].year", equalTo(1993)))
                .andExpect(jsonPath("$[0].synopsis", equalTo("A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA.")))
                .andExpect(jsonPath("$[1].title", equalTo("Ratatouille")))
                .andExpect(jsonPath("$[1].coverImage", equalTo("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/npHNjldbeTHdKKw28bJKs7lzqzj.jpg")))
                .andExpect(jsonPath("$[1].director", equalTo("Brad Bird")))
                .andExpect(jsonPath("$[1].year", equalTo(2007)))
                .andExpect(jsonPath("$[1].synopsis", equalTo("Remy, a resident of Paris, appreciates good food and has quite a sophisticated palate. He would love to become a chef so he can create and enjoy culinary masterpieces to his heart's delight. The only problem is, Remy is a rat.")))
                .andDo(print());
    }

    private void addSampleMovies() {
        List<Movie> movies = List.of(
                new Movie("Jurassic Park",
                        "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg",
                        "Steven Spielberg",
                        1993,
                        "A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA."),
                new Movie("Ratatouille",
                        "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/npHNjldbeTHdKKw28bJKs7lzqzj.jpg",
                        "Brad Bird",
                        2007,
                        "Remy, a resident of Paris, appreciates good food and has quite a sophisticated palate. He would love to become a chef so he can create and enjoy culinary masterpieces to his heart's delight. The only problem is, Remy is a rat.")
        );

        movieRepository.saveAll(movies);
    }


        @Test
    void allowsToCreateNewMovie () throws Exception {
        addSampleMovies();

            mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\": \"Dory\", \"coverImage\": \"https://www.covercaratulas.com/ode/mini_mini/carteles-29294.jpg\", \"director\": \"Andrew Stanton\",  \"year\": \"2016\",  \"synopsis\": \"Friendly but forgetful blue tang Dory begins a search for her long lost parents, and everyone learns a few things about the real meaning of family along the way.\" }")
            ).andExpect(status().isOk());
        }

        @Test
    void allowsToModifyAMovie() throws Exception{
        Movie movie= movieRepository.save(new Movie("Jurassic Park", "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg",
             "Steven Spielberg", 1993, "A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA."));

        mockMvc.perform(put("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\""+ movie.getId() + "\", \"title\": \"Jurassic Park\", \"coverImage\": \"https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg\", \"director\": \"Kubrick\", \"year\": \"1993\", \"synopsis\": \"A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA.\"}")
        ).andExpect(status().isOk());
        }

        @Test
    void allowsToDeleteAMovieById () throws Exception {
    Movie movie = movieRepository.save(new Movie("Jurassic Park", "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg",
            "Steven Spielberg", 1993, "A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA."));
            mockMvc.perform(MockMvcRequestBuilders.delete("/movies/"+ movie.getId()))
                    .andExpect(status().isOk());

            List<Movie> movies = movieRepository.findAll();
            assertThat(movies, hasSize(0));
        }

         @Test
    void allowsToMarkRentedMovieById () throws Exception {
        Movie movie = movieRepository.save(new Movie("Jurassic Park", "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg",
                "Steven Spielberg", 1993, "Adventure", "A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA."));
        mockMvc.perform(put("/movies/"+ movie.getId()+"/book?renter=faby"))
                .andExpect(status().isOk());

             Movie bookedMovie = movieRepository.findById(movie.getId()).get();
             assertThat(bookedMovie.isBooked(), equalTo(true));
             assertThat(bookedMovie.getRenter(), equalTo("faby"));

    }

    @Test
    void allowsToMarkAvailableMovieById () throws Exception {
        Movie movie = movieRepository.save(new Movie("Jurassic Park", "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg",
                "Steven Spielberg", 1993, "Adventure", "A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA.", false, "null", 5));
        mockMvc.perform(MockMvcRequestBuilders.put("/movies/"+ movie.getId()+"/book?renter=null"))
                .andExpect(status().isOk());

    assertThat (movie.isBooked(), equalTo(false));
    assertThat (movie.getRenter(),equalTo("null"));
    }

}
