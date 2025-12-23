package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleOkResponse {
    private boolean ok;

    public static SimpleOkResponse ok() {
        return new SimpleOkResponse(true);
    }
}
