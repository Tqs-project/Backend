package deti.tqs.webmarket.api;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log4j2
@Service
public class DistanceAPI {

    private GeoApiContext context;

    public DistanceAPI() {
        this.context = new GeoApiContext.Builder()
                .apiKey("AIzaSyD0nqtSzvVTCPNbp1GkNpBnE1upxFp7hg4")
                .build();
    }

    public DistanceMatrix getDistance(String[] origin, String[] destination) {
        try {
            return DistanceMatrixApi.newRequest(context)
                    .origins(origin)
                    .destinations(destination)
                    .mode(TravelMode.DRIVING)
                    .await();
        } catch (InterruptedException e) {
            log.warn("Thread interrupted!");
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException | ApiException e) {
            return null;
        }
    }
}
