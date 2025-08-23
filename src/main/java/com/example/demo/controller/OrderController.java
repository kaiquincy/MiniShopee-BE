package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.OrderResponseDetail;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.service.OrderService;
import com.example.demo.service.PaymentService;
import com.example.demo.service.UserService;
import com.example.demo.model.Order;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);


    @PostMapping
    public ApiResponse<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        // Validate the order request
        if (orderRequest.getMethod() == null) {
            throw new AppException(ErrorCode.PAYMENTMETHOD_NULL);
        }

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        Order order = orderService.placeOrder(userService.getCurrentUserId());
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .createdAt(order.getCreatedAt())
                .status("PENDING")
                .paymentMethod(orderRequest.getMethod().name())
                .totalAmount(order.getGrandTotal())
                .build();

        logger.info("Order id: {}", order.getId());

        // Tạo payment nếu phương thức thanh toán là PayOS
        if (orderRequest.getMethod() == PaymentMethod.PAYOS) {
            PaymentResponse rsp = paymentService.initiate(orderRequest.getMethod(), order.getId());
            orderResponse.setPaymentLink(rsp.getPaymentUrl());
        }
        
        apiResponse.setResult(orderResponse);
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<OrderResponseDetail>> listOrders(
            @RequestParam(required = false) Long userId) {

        Long uid = (userId != null ? userId : userService.getCurrentUserId());
        List<OrderResponseDetail> orders = orderService.getOrdersWithDetails(uid);

        ApiResponse<List<OrderResponseDetail>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(orders);
        return apiResponse;
    }
}
