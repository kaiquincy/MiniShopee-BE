package com.example.demo.controller;

import com.example.demo.dto.AddressRequest;
import com.example.demo.dto.AddressResponse;
import com.example.demo.dto.ApiResponse;
import com.example.demo.service.AddressService;
import com.example.demo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressResponse>> myAddresses() {
        ApiResponse<List<AddressResponse>> resp = new ApiResponse<>();
        resp.setResult(addressService.myAddresses());
        return resp;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(@RequestBody AddressRequest req) {
        ApiResponse<AddressResponse> resp = new ApiResponse<>();
        try {
            AddressResponse dto = addressService.create(req);
            resp.setResult(dto);
            resp.setMessage("Tạo địa chỉ thành công");
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage(ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(@PathVariable Long id,
                                                               @RequestBody AddressRequest req) {
        ApiResponse<AddressResponse> resp = new ApiResponse<>();
        try {
            AddressResponse dto = addressService.update(id, req);
            resp.setResult(dto);
            resp.setMessage("Cập nhật địa chỉ thành công");
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage(ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        ApiResponse<String> resp = new ApiResponse<>();
        try {
            addressService.delete(id);
            resp.setResult("OK");
            resp.setMessage("Đã xóa địa chỉ");
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage(ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }

    @PostMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> makeDefault(@PathVariable Long id) {
        ApiResponse<AddressResponse> resp = new ApiResponse<>();
        try {
            AddressResponse dto = addressService.markDefault(id);
            resp.setResult(dto);
            resp.setMessage("Đã đặt địa chỉ mặc định");
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage(ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }
}
