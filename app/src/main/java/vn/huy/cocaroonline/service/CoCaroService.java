package vn.huy.cocaroonline.service;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CoCaroService {
    @GET("/")
    Call<CaroStatus> getRoomName();
}
