package deti.tqs.webmarket.service;


import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RiderService {
    
    @Autowired
	private RiderRepository repository;

    public Rider registerRider(RiderDto riderDto){

    }

    public Object login(String email, String password){

    }

    public Rider getRiderByEmail(String email) {

    }

}