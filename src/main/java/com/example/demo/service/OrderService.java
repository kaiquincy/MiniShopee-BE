package com.example.demo.service;

import com.example.demo.dto.AdminOrdersResponse;
import com.example.demo.dto.OrderItemResponse;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.OrderResponseDetail;
import com.example.demo.dto.OrderStatusCountResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final RatingRepository ratingRepo;

    @Transactional
    public Order placeOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Order order = Order.builder()
                .user(user)
                .build();
        order = orderRepository.save(order);

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(ci.getProduct())
                    .quantity(ci.getQuantity())
                    .price(ci.getProduct().getPrice())
                    .variant(ci.getVariant())
                    .build();
            orderItemRepository.save(oi);
        }

        cartService.clearCart(userId);
        return order;
    }

    public List<AdminOrdersResponse> listOrders(String q, String status) {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(o -> new AdminOrdersResponse(
                        o.getId(),
                        o.getCreatedAt(),
                        o.getStatus(),
                        o.getGrandTotal()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDetail> getOrdersWithDetails(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponseDetail> responses = new ArrayList<>();

        for (Order order : orders) {
            responses.add(convertToOrderResponseDetail(order));
        }

        return responses;
    }

    // NEW: Paginated version with optional status filter
    @Transactional(readOnly = true)
    public Page<OrderResponseDetail> getOrdersWithDetailsPaginated(Long userId, String status, Pageable pageable) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Page<Order> orders;

        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
            // Filter by status
            orders = orderRepository.findByUserIdAndStatus(userId, status.toUpperCase(), pageable);
        } else {
            // Get all orders
            orders = orderRepository.findByUserId(userId, pageable);
        }

        // Convert to OrderResponseDetail
        return orders.map(this::convertToOrderResponseDetail);
    }

    @Transactional(readOnly = true)
    public OrderStatusCountResponse getOrderCountsByStatus(Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Object[]> results = orderRepository.countOrdersByStatus(userId);

        long processing = 0L;
        long shipping = 0L;
        long completed = 0L;
        long cancelled = 0L;
        long pending = 0L;

        for (Object[] result : results) {
            String status = ((String) result[0]).toUpperCase();
            Long count = (Long) result[1];

            switch (status) {
                case "PROCESSING":
                    processing = count;
                    break;
                case "SHIPPING":
                    shipping = count;
                    break;
                case "COMPLETED":
                    completed = count;
                    break;
                case "CANCELLED":
                    cancelled = count;
                    break;
                case "PENDING":
                    pending = count;
                    break;
            }
        }

        long total = processing + shipping + completed + cancelled + pending;

        return OrderStatusCountResponse.builder()
                .processing(processing)
                .shipping(shipping)
                .completed(completed)
                .cancelled(cancelled)
                .pending(pending)
                .total(total)
                .build();
    }

    // Helper method to convert Order to OrderResponseDetail
    private OrderResponseDetail convertToOrderResponseDetail(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> itemDtos = new ArrayList<>();

        for (OrderItem ci : items) {
            ProductVariant v = ci.getVariant();
            Map<String, String> optionValues = null;
            if (v != null && v.getOptions() != null) {
                optionValues = v.getOptions().stream().collect(Collectors.toMap(
                        o -> o.getGroup().getName(),
                        VariantOption::getValue
                ));
            }

            String image = null;
            if (v != null && v.getImageUrl() != null) {
                image = v.getImageUrl();
            } else {
                image = ci.getProduct().getImageUrl();
            }

            // Check if this order item has been rated
            boolean hasRating = ratingRepo.findByOrderItemId(ci.getId()).isPresent();

            OrderItemResponse dto = OrderItemResponse.builder()
                    .orderItemId(ci.getId())
                    .productId(ci.getProduct().getId())
                    .imageUrl(image)
                    .productName(ci.getProduct().getName())
                    .quantity(ci.getQuantity())
                    .price(ci.getPrice())
                    .discountPrice(ci.getProduct().getDiscountPrice())
                    .optionValues(optionValues)
                    .hasRating(hasRating) // Add this
                    .build();
            itemDtos.add(dto);
        }

        return OrderResponseDetail.builder()
                .orderId(order.getId())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .totalAmount(order.getGrandTotal())
//                .paymentMethod(order.getPaymentMethod())
                .items(itemDtos)
                .build();
    }
}