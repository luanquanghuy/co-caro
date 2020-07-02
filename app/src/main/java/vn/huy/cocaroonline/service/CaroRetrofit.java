package vn.huy.cocaroonline.service;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CaroRetrofit {
    private static CoCaroService instance;
    public static CoCaroService getInstance(){
        if(instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl("https://cocaroonline.herokuapp.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(CoCaroService.class);
        }
        return  instance;
    }

    public static Call<CaroStatus> getRoomName(){
        return getInstance().getRoomName();
    }
}
