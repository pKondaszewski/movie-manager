package pl.przemek.moviemanager.service.ombd.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.RequiredArgsConstructor;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.przemek.moviemanager.controller.MovieType;
import pl.przemek.moviemanager.dto.MovieDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.exception.OmbdApiException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OmbdApiClient {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
    private final HttpClient httpClient;

    @Value("${ombd.api.url}")
    private String apiUrl;
    @Value("${ombd.api.key}")
    private String apiKey;

    public MovieDTO getMovieByIdOrTitle(String id, String movieTitle, MovieType typeOfResult, Year releaseYear) throws OmbdApiException {
        try {
            List<NameValuePair> urlParams = prepareSingleMovieSearchParams(id, movieTitle, typeOfResult, releaseYear);
            HttpRequest request = prepareHttpRequest(urlParams);
            return getParsedResponse(request, MovieDTO.class);
        } catch (IllegalArgumentException e) {
            throw new OmbdApiException(e.getMessage(), e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new OmbdApiException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public MoviesListDTO getMoviesByTitle(String movieTitle, MovieType typeOfResult, Year releaseYear) throws OmbdApiException {
        try {
            List<NameValuePair> urlParams = prepareListMovieSearchParams(movieTitle, typeOfResult, releaseYear);
            HttpRequest request = prepareHttpRequest(urlParams);
            return getParsedResponse(request, MoviesListDTO.class);
        } catch (Exception e) {
            throw new OmbdApiException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<NameValuePair> prepareSingleMovieSearchParams(String id, String movieTitle, MovieType typeOfResult, Year releaseYear) {
        List<NameValuePair> urlParams = new ArrayList<>();

        checkRequiredFields(id, movieTitle);
        addApiKey(urlParams);
        addIdToParamsIfNotNull(id, urlParams);
        addMovieTitleToParamsIfNotNull(movieTitle, urlParams);
        addTypeOfResultToParamsIfNotNull(typeOfResult, urlParams);
        addReleaseYearToParamsIfNotNull(releaseYear, urlParams);

        return urlParams;
    }

    private List<NameValuePair> prepareListMovieSearchParams(String movieTitle, MovieType typeOfResult, Year releaseYear) {
        List<NameValuePair> urlParams = new ArrayList<>();

        addApiKey(urlParams);
        addMovieTitle(movieTitle, urlParams);
        addTypeOfResultToParamsIfNotNull(typeOfResult, urlParams);
        addReleaseYearToParamsIfNotNull(releaseYear, urlParams);

        return urlParams;
    }

    private void checkRequiredFields(String id, String movieTitle) {
        if (id == null && movieTitle == null) {
            throw new IllegalArgumentException("At least one of 'id' or 'movieTitle' is required.");
        }
    }

    private void addApiKey(List<NameValuePair> urlParams) {
        urlParams.add(new BasicHeader("apiKey", apiKey));
    }

    private void addMovieTitle(String movieTitle, List<NameValuePair> urlParams) {
        urlParams.add(new BasicHeader("s", movieTitle));
    }

    private void addIdToParamsIfNotNull(String id, List<NameValuePair> urlParams) {
        if (id != null) {
            urlParams.add(new BasicHeader("i", id));
        }
    }

    private void addMovieTitleToParamsIfNotNull(String movieTitle, List<NameValuePair> urlParams) {
        if (movieTitle != null) {
            urlParams.add(new BasicHeader("t", movieTitle));
        }
    }

    private void addTypeOfResultToParamsIfNotNull(MovieType typeOfResult, List<NameValuePair> urlParams) {
        if (typeOfResult != null) {
            urlParams.add(new BasicHeader("type", typeOfResult.name().toLowerCase()));
        }
    }
    private void addReleaseYearToParamsIfNotNull(Year releaseYear, List<NameValuePair> urlParams) {
        if (releaseYear != null) {
            String releaseYearAsString = String.valueOf(releaseYear.getValue());
            urlParams.add(new BasicHeader("y", releaseYearAsString));
        }
    }

    private HttpRequest prepareHttpRequest(List<NameValuePair> urlParams) throws URISyntaxException {
        URI apiUri = new URIBuilder(apiUrl)
                .addParameters(urlParams)
                .build();

        return HttpRequest.newBuilder()
                .GET()
                .uri(apiUri)
                .build();
    }

    private <T> T getParsedResponse(HttpRequest request, Class<T> responseType) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), responseType);
    }
}
