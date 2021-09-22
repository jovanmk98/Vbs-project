package com.vbs.demo.controller;

import com.vbs.demo.model.Actor;
import com.vbs.demo.model.Movie;
import com.vbs.demo.service.MovieServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
@AllArgsConstructor

public class MovieController {
    private final MovieServiceImpl movieService;

    @GetMapping("/movies")
    public String listAllMovies(Model model) {
        List<Movie> moviesList = this.movieService.findAllMovies();
        model.addAttribute("movies", moviesList);
        return "movies";
    }
    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/movie/{name}")
    public String getMovie(@PathVariable String name, Model model) {
        Movie movie = this.movieService.getMovie(name).get();
        model.addAttribute("movie", movie);
        return "movie";
    }

    @GetMapping("/actors")
    public String listAllActors(Model model){
        List<Actor> actorsList = this.movieService.findAllActors();
        model.addAttribute("actors", actorsList);
        return "actors";
    }


    @GetMapping("/actor/{name}")
    public String getActor(@PathVariable String name, Model model){
        Actor actor = this.movieService.getActor(name).get();
        model.addAttribute("actor", actor);
        model.addAttribute("actorMoviesList", actor.getActorsMovies());
        return "actor";
    }


}
