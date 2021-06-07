package deti.tqs.webmarket.service;


import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.repository.RiderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RiderService {
    
    @Autowired
	private RiderRepository repository;

    public Rider registerRider(RiderDto riderDto) throws Exception {
        if (repository.existsByUser_Email(riderDto.getEmail())) {
            throw new Exception("That email is already in use!");
        } else {
            Rider rider = new ModelMapper().map(riderDto, Rider.class);
            return repository.save(rider);
        }
    }

    public Object login(String email, String password){

    }

    public Rider getRiderByEmail(String email) {

    }

}