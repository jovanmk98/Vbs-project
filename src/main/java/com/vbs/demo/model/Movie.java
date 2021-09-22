package com.vbs.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    

}
