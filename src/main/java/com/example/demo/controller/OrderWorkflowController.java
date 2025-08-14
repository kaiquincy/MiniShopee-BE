package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.OrderStatusUpdateRequest;
import com.example.demo.dto.OrderStatusUpdateResponse;
import com.example.demo.enums.OrderStatus;
import com.example.demo.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordersworkflow")
@RequiredArgsConstructor
public class OrderWorkflowController {
    private final OrderStatusService orderStatusService;

    /** Cập nhật trạng thái thủ công (seller/admin) */
    @PostMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderStatusUpdateResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest req) {
        OrderStatusUpdateResponse out = orderStatusService.updateStatus(id, req.getTarget(), req.getNote());
        ApiResponse<OrderStatusUpdateResponse> resp = new ApiResponse<>();
        resp.setResult(out);
        return ResponseEntity.ok(resp);
    }

    /** Các nút nhanh nếu muốn: */
    @PostMapping("/{id}/process")
    public ApiResponse<OrderStatusUpdateResponse> markProcessing(@PathVariable Long id) {
        ApiResponse<OrderStatusUpdateResponse> resp = new ApiResponse<>();
        resp.setResult(orderStatusService.updateStatus(id, OrderStatus.PROCESSING, "Start processing"));
        return resp;
    }

    @PostMapping("/{id}/ship")
    public ApiResponse<OrderStatusUpdateResponse> markShipping(@PathVariable Long id) {
        ApiResponse<OrderStatusUpdateResponse> resp = new ApiResponse<>();
        resp.setResult(orderStatusService.updateStatus(id, OrderStatus.SHIPPING, "Start shipping"));
        return resp;
    }

    @PostMapping("/{id}/deliver")
    public ApiResponse<OrderStatusUpdateResponse> markDelivered(@PathVariable Long id) {
        ApiResponse<OrderStatusUpdateResponse> resp = new ApiResponse<>();
        resp.setResult(orderStatusService.updateStatus(id, OrderStatus.DELIVERED, "Delivered"));
        return resp;
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<OrderStatusUpdateResponse> markCompleted(@PathVariable Long id) {
        ApiResponse<OrderStatusUpdateResponse> resp = new ApiResponse<>();
        resp.setResult(orderStatusService.updateStatus(id, OrderStatus.COMPLETED, "Customer confirmed"));
        return resp;
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderStatusUpdateResponse> cancel(@PathVariable Long id) {
        ApiResponse<OrderStatusUpdateResponse> resp = new ApiResponse<>();
        resp.setResult(orderStatusService.updateStatus(id, OrderStatus.CANCELLED, "Cancelled"));
        return resp;
    }

    @PostMapping("/{id}/refund")
    public ApiResponse<OrderStatusUpdateResponse> refund(@PathVariable Long id) {
        ApiResponse<OrderStatusUpdateResponse> resp = new ApiResponse<>();
        resp.setResult(orderStatusService.updateStatus(id, OrderStatus.REFUNDED, "Refunded"));
        return resp;
    }
}
