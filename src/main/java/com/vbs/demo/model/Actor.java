package com.vbs.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Actor {
    String name, image, birthDate;
    List<String> actorsMovies = new ArrayList<>();

}
