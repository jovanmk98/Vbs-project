package com.vbs.demo.service;

import com.github.jsonldjava.utils.JsonUtils;
import com.vbs.demo.model.Movie;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class MovieServiceImpl {

    public List<Movie> generateMoviesList(){
        Dataset dataset = DatasetFactory.create();
        ResultSet resultSet = QueryExecutionFactory.create(""
                + "SELECT ?movie ?name ?abstract ?director ?genre ?directorName ?genreName" +
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
            String genre = (String) map.get("genre");
            genre = genre.substring(28);
            Movie movie = Movie.builder().movie((String) map.get("movie"))
                    .directorName((String) map.get("directorName"))
                    .director(director)
                    .genre(genre)
                    .name(name)
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

    public List<Movie> findAll(){
        return generateMoviesList();
    }

    public Movie getMovie(String name){
        List<Movie> movies = findAll();
        for (Movie movie : movies){
            if (movie.getName().equals(name))
                return movie;
        }
        return null;
    }

}