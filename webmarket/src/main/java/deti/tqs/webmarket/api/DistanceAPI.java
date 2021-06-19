package deti.tqs.webmarket.api;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
        } catch (IOException | ApiException | InterruptedException e) {
            return null;
        }
    }
}
