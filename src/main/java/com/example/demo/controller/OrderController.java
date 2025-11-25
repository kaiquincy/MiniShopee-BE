package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.service.OrderService;
import com.example.demo.service.PaymentService;
import com.example.demo.service.UserService;
import com.example.demo.model.Order;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
    public ApiResponse<Page<OrderResponseDetail>> listOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Long uid = (userId != null ? userId : userService.getCurrentUserId());

        // Create pageable with sort by createdAt descending
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Get paginated orders with optional status filter
        Page<OrderResponseDetail> orders = orderService.getOrdersWithDetailsPaginated(uid, status, pageable);

        ApiResponse<Page<OrderResponseDetail>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(orders);
        return apiResponse;
    }

    @GetMapping("/counts")
    public ApiResponse<OrderStatusCountResponse> getOrderCounts(
            @RequestParam(required = false) Long userId) {

        Long uid = (userId != null ? userId : userService.getCurrentUserId());
        OrderStatusCountResponse counts = orderService.getOrderCountsByStatus(uid);

        ApiResponse<OrderStatusCountResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(counts);
        return apiResponse;
    }
}
