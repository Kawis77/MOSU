package unitmonitoring.web.model.service;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import unitmonitoring.web.model.Datum;
import unitmonitoring.web.model.Point;
import unitmonitoring.web.model.Track;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TrackService {
    RestTemplate restTemplate = new RestTemplate();


    public List<Point> getTracks() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2NDMyMzYwOTQsImV4cCI6MTY0MzIzOTY5NCwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJqYXZha3Jpc3Rlc3RAZ21haWwuY29tOmtyaXMiLCJzY29wZSI6WyJhcGkiXX0.UfUgr98fof3P8F2YBOgzTkVqJqSeZsYjvhrDQjGSTmsycJv7WtIOBYTYlLsvnGlGm3n--KVLYOV1KgMe3WGhevt-YawC7Vfs-8QhBLJAPcG9rpAGp05kgkofdW684XyRofpu0revZ8Wif9OdlvLmkreRa-yih2_OuYXrSjTEfalWkPaeNS1vUzHDDI2seSEvr9UbuaDPbgug6C_Gv44GmsDDI8urgUB_Ew4BOGkW4dnmaQNC1GN19TxgW-OZj5iCDFS_ATXjndA-dQR9ngFC6AAUIYW6OOqy_EizuDh9Wx3o0NKJjTrLVNZOP3MBspZiP62Ca6oA8jaeJ25sySNMx5JMK8DTeAv2KwyoZ1BcZoLLlnXOtuoSfgIR_eiQLzl-RtU-1RVZ-V6SagOLFM03hq-ySWmamUXkXkX22L1UBkA-Qb7h23BGsqH8wXxrOWcQGuMSD0FpMwns-5uHgOtWj9a7wopOHts4cNduZYqHkmzXjb0h03M4DYDpH1eOhFnZyH5kBhOhRd1VxO3ulDnQDfgWuIMp3pk_L-829kCpPkPhm4li5Df1S9eyO_wflfgal6t2SsqJVff97Rr8qJ8LCIPEdLi0NMkHRLzbekulGvJMMKxveW6qYmOxEG5f6g-JGCpGlE15CjwRyYqLu0jPDv_5sSVfSRT_hTm378g6sxI");
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<Track[]> exchange = restTemplate.exchange("https://www.barentswatch.no/bwapi/v2/geodata/ais/openpositions?Xmin=10.09094&Xmax=10.67047&Ymin=63.3989&Ymax=63.58645",
                HttpMethod.GET,
                httpEntity,
                Track[].class);

        List<Point> collect = Stream.of(exchange.getBody()).map(
                track -> new Point(
                        track.getGeometry().getCoordinates().get(0),
                        track.getGeometry().getCoordinates().get(1),
                        track.getName(),
                        getDestination(track.getDestination(), track.getGeometry().getCoordinates()).getLongitude(),
                        getDestination(track.getDestination(), track.getGeometry().getCoordinates()).getLatitude()
                )
        ).collect(Collectors.toList());
        return collect;
    }

    public Datum getDestination(String destinationName, List<Double> coordinates) {
        try {
            String url = "http://api.positionstack.com/v1/forward?access_key=f9aae45e031a1e66eac64db90ffda427&query=" + destinationName;
            JsonNode data = restTemplate.getForObject(url, JsonNode.class).get("data").get(0);
            double latitude = data.get("latitude").asDouble();
            double longitude = data.get("longitude").asDouble();
            return new Datum(latitude, longitude);

        } catch (Exception ex) {
            return new Datum(coordinates.get(1), coordinates.get(0));
        }
    }
}