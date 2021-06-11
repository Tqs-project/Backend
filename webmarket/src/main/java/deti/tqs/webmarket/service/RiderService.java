package deti.tqs.webmarket.service;

import deti.tqs.webmarket.dto.RiderDto;
import deti.tqs.webmarket.dto.TokenDto;
import deti.tqs.webmarket.model.Rider;

public interface RiderService {
    RiderDto registerRider(RiderDto riderDto) throws Exception;
    Rider updateRider(RiderDto riderDto);
    TokenDto login(RiderDto riderDto);
}