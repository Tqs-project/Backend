package deti.tqs.webmarket.service;


import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Rider;
import deti.tqs.webmarket.model.User;
import deti.tqs.webmarket.repository.RiderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiderServiceImp implements RiderService {

    @Autowired
    private RiderRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    public Rider registerRider(RiderDto riderDto) throws Exception {
        if (repository.existsByUser_Email(riderDto.getUser().getEmail())) {
            throw new Exception("That email is already in use!");
        } else {
            User user = new ModelMapper().map(riderDto.getUser(), User.class);
            user.setPassword(encoder.encode(riderDto.getUser().getPassword()));
            Rider rider = new Rider(user, riderDto.getVehiclePlate());
            return repository.save(rider);
        }
    }

    @Override
    public Rider updateRider(RiderDto riderDto) {
        return null;
    }

    public TokenDto login(String email, String password){
        //TODO: make login method
        return null;
    }

    public Rider getRiderByEmail(String email) {
        return repository.findByUser_Email(email);
    }

    public List<Rider> getAllRiders() {
        return repository.findAll();
    }

}