package com.vbs.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Movie {

    String movie;
    String directorName;
    String director;
    String genre;
    String name;
    String description;
    String image;
    List<String> movieActors = new ArrayList<>();

}
