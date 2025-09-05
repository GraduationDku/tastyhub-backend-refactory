package com.example.userservice.village.entity;


import com.example.userservice.village.dtos.LocationRequest;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Village{

    private String addressTownName;

    private double lat; //위도

    private double lng; //경도

    public void update(LocationRequest locationRequest, String addressTownName) {
        this.addressTownName = addressTownName;
        this.lat = locationRequest.getLat();
        this.lng = locationRequest.getLng();
    }

    public static Village createVillage(String addressFromCoordinates, double lat, double lng) {
        Village village = Village.builder()
                .addressTownName(addressFromCoordinates)
                .lat(lat)
                .lng(lng)
                .build();
        return village;
    }
}
