package com.vbs.demo.service;

import com.vbs.demo.model.Actor;
import com.vbs.demo.model.Movie;
import org.apache.jena.query.*;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MovieServiceImpl {
    public List<String> getMoviesForActor(String actor){
        Dataset dataset = DatasetFactory.create();
        ResultSet resultSet = QueryExecutionFactory.create(""
                + "SELECT ?movie ?name" +
                " WHERE {"
                + "    VALUES ?actor {"
                + "	       <"+actor+">"
                + "    }"
                + "    SERVICE <http://dbpedia.org/sparql> {"
                + "		   ?movie <http://dbpedia.org/ontology/starring> ?actor;" +
                "            <http://dbpedia.org/property/name> ?name."
                + "    }"
                + "} LIMIT 10", dataset).execSelect();

        List<Map> jsonList = new Vector<Map>();
        while (resultSet.hasNext()) {
            QuerySolution qsol = resultSet.nextSolution();
            Iterator<String> varNames = qsol.varNames();
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            while (varNames.hasNext()) {
                String varName = varNames.next();
                jsonMap.put(varName, qsol.get(varName).toString());
            }
            jsonList.add(jsonMap);
        }
        List<String> movies = new ArrayList<>();
        for (Map<String, Object> map : jsonList) {
            String name = (String) map.get("name");
            name = name.substring(0, name.length()-3);
            movies.add(name);
        }
        return movies;
    }
    public List<Actor> generateActorsList(){
        Dataset dataset = DatasetFactory.create();
        ResultSet resultSet1 = QueryExecutionFactory.create(""
                + "SELECT ?person ?name ?abstract ?image ?birthDate" +
                " WHERE {"
                + "    VALUES ?actor {"
                + "	       <http://dbpedia.org/class/yago/WikicatActors>"
                + "    }"
                + "    SERVICE <http://dbpedia.org/sparql> {"
                + "		   ?person <" + RDF.type + "> ?actor;" +
                "               <http://dbpedia.org/ontology/birthName> ?name;" +
                "               <http://dbpedia.org/ontology/abstract> ?abstract;" +
                "               <http://dbpedia.org/ontology/thumbnail> ?image;" +
                "               <http://dbpedia.org/ontology/birthDate> ?birthDate." +
                "          FILTER langMatches(lang(?abstract),'en')"
                + "    }"
                + "} LIMIT 50", dataset).execSelect();
        List<Map> jsonList = new Vector<Map>();
        while (resultSet1.hasNext()) {
            QuerySolution qsol = resultSet1.nextSolution();
            Iterator<String> varNames = qsol.varNames();
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            while (varNames.hasNext()) {
                String varName = varNames.next();
                jsonMap.put(varName, qsol.get(varName).toString());
            }
            jsonList.add(jsonMap);
        }
        List<Actor> actorsList = new ArrayList<>();
        for (Map<String, Object> map : jsonList){
            String name = (String) map.get("name");
            name = name.substring(0, name.length()-3);
            String birthDate = (String) map.get("birthDate");
            birthDate = birthDate.substring(0, 10);
            Actor actor = Actor.builder()
                    .name(name)
                    .image((String) map.get("image"))
                    .actorsMovies(getMoviesForActor((String) map.get("person")))
                    .birthDate(birthDate)
                    .build();

            boolean flag = true;
            if (actorsList.size()>0) {
                for (Actor m : actorsList) {
                    if (actor.getName().equals(m.getName())) {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                actorsList.add(actor);
            }

        }
        return actorsList;
    }

    public List<Movie> generateMoviesList(){
        Dataset dataset = DatasetFactory.create();
        ResultSet resultSet = QueryExecutionFactory.create(""
                + "SELECT ?movie ?name ?abstract ?director ?image ?genre ?directorName ?genreName" +
                " WHERE {"
                + "    VALUES ?film {"
                + "	       <http://dbpedia.org/ontology/Film>"
                + "    }"
                + "    SERVICE <http://dbpedia.org/sparql> {"
                + "		   ?movie <" + RDF.type + "> ?film;" +
                "               <http://dbpedia.org/property/name> ?name;" +
                "               <http://dbpedia.org/ontology/abstract> ?abstract;" +
                "               <http://dbpedia.org/property/country> ?america;" +
                "               <http://dbpedia.org/ontology/director> ?director;" +
                "               <http://dbpedia.org/ontology/thumbnail> ?image;" +
                "               <http://dbpedia.org/ontology/genre> ?genre." +
                "          ?director <http://dbpedia.org/property/name> ?directorName." +
                "          FILTER langMatches(lang(?abstract),'en')" +
                "          FILTER regex(?america,'United States')"
                + "    }"
                + "} LIMIT 100", dataset).execSelect();

        List<Map> jsonList = new Vector<Map>();
        while (resultSet.hasNext()) {
            QuerySolution qsol = resultSet.nextSolution();
            Iterator<String> varNames = qsol.varNames();
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            while (varNames.hasNext()) {
                String varName = varNames.next();
                jsonMap.put(varName, qsol.get(varName).toString());
            }
            jsonList.add(jsonMap);
        }

        List<Movie> moviesList = new ArrayList<>();
        for (Map<String, Object> map : jsonList){
            String name = (String) map.get("name");
            name = name.substring(0, name.length()-3);
            String director = (String) map.get("director");
            director = director.substring(0, director.length()-3);
            String genre = (String) map.get("genre");
            genre = genre.substring(28);
            List<String> actors = getActorsforMovie((String) map.get("movie"));
            Movie movie = Movie.builder().movie((String) map.get("movie"))
                    .directorName((String) map.get("directorName"))
                    .director(director)
                    .genre(genre)
                    .name(name)
                    .image((String) map.get("image"))
                    .movieActors(actors)
                    .description((String) map.get("abstract")).build();

            boolean flag = true;
            if (moviesList.size()>0) {
                for (Movie m : moviesList) {
                    if (movie.getName().equals(m.getName())) {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                moviesList.add(movie);
            }

        }
        return moviesList;
    }
    public List<String> getActorsforMovie(String movie){
        Dataset dataset = DatasetFactory.create();
        ResultSet resultSet = QueryExecutionFactory.create(""
                + "SELECT ?actor ?name" +
                " WHERE {"
                + "    VALUES ?movie {"
                + "	       <"+movie+">"
                + "    }"
                + "    SERVICE <http://dbpedia.org/sparql> {"
                + "		   ?movie <http://dbpedia.org/ontology/starring> ?actor." +
                "          ?actor <http://dbpedia.org/property/name> ?name."
                + "    }"
                + "} LIMIT 10", dataset).execSelect();
        List<Map> jsonList = new Vector<Map>();
        while (resultSet.hasNext()) {
            QuerySolution qsol = resultSet.nextSolution();
            Iterator<String> varNames = qsol.varNames();
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            while (varNames.hasNext()) {
                String varName = varNames.next();
                jsonMap.put(varName, qsol.get(varName).toString());
            }
            jsonList.add(jsonMap);
        }
        List<String> actors = new ArrayList<>();
        for (Map<String, Object> map : jsonList) {
            String name = (String) map.get("name");
            name = name.substring(0, name.length() - 3);
            actors.add(name);
        }
        return actors;

    }

    public List<Movie> findAllMovies(){
        return generateMoviesList();
    }

    public List<Actor> findAllActors() { return generateActorsList();}



    public Optional<Movie> getMovie(String name){
        List<Movie> movies = findAllMovies();

        return movies.stream().filter(f->f.getName().equals(name)).findFirst();
    }
    public Optional<Actor> getActor(String actor){
        List<Actor> actors = findAllActors();
        return actors.stream().filter(f->f.getName().equals(actor)).findFirst();
    }

}