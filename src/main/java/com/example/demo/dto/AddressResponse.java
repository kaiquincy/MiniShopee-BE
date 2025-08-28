package com.example.demo.dto;

import com.example.demo.model.Address;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String line1;
    private String ward;
    private String district;
    private String city;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AddressResponse(Address a) {
        this.id = a.getId();
        this.fullName = a.getFullName();
        this.phone = a.getPhone();
        this.line1 = a.getLine1();
        this.ward = a.getWard();
        this.district = a.getDistrict();
        this.city = a.getCity();
        this.isDefault = a.getIsDefault();
        this.createdAt = a.getCreatedAt();
        this.updatedAt = a.getUpdatedAt();
    }
}
