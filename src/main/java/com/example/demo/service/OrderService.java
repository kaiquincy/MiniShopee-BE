package com.example.demo.service;

import com.example.demo.dto.AdminOrdersResponse;
import com.example.demo.dto.OrderItemResponse;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.OrderResponseDetail;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
    public Order placeOrder(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        //kiểm tra cart rỗng?
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Order order = Order.builder()
            .user(user)
            .build();
        order = orderRepository.save(order);
        
        // Copy tất cả cartItem sang OrderItem trong cart của user
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

        // clear cart
        cartService.clearCart(userId);
        return order;
    }

    public List<AdminOrdersResponse> listOrders(String q, String status) {
        List<Order> orders = orderRepository.findAll(); // TODO: filter theo q, status nếu cần
        return orders.stream()
            .map(o -> new AdminOrdersResponse(
                o.getId(),
                o.getCreatedAt(),
                o.getStatus(),
                // o.getPaymentMethod(),
                o.getGrandTotal()
                // o.getPaymentLink()
            ))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDetail> getOrdersWithDetails(Long userId) {
        // 1. Kiểm user
        userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Lấy list orders
        List<Order> orders = orderRepository.findByUserId(userId);

        // 3. Khởi tạo list kết quả
        List<OrderResponseDetail> responses = new ArrayList<>();
        
        // 4. Duyệt từng order
        for (Order order : orders) {
            // 4.1. Lấy các OrderItem thuộc order hiện tại
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

            // 4.2. Chuyển từng OrderItem thành OrderItemResponse
            List<OrderItemResponse> itemDtos = new ArrayList<>();
            for (OrderItem ci : items) {
                // Biến phụ varients (optionValues)
                ProductVariant v = ci.getVariant();
                Map<String,String> optionValues = null;
                if (v != null && v.getOptions() != null) {
                    optionValues = v.getOptions().stream().collect(Collectors.toMap(
                        o -> o.getGroup().getName(),
                        VariantOption::getValue
                    ));
                }

                OrderItemResponse dto = OrderItemResponse.builder()
                    .productId(ci.getProduct().getId())
                    .imageUrl(ci.getProduct().getImageUrl())
                    .productName(ci.getProduct().getName())
                    .quantity(ci.getQuantity())
                    .price(ci.getPrice())
                    .discountPrice(ci.getProduct().getDiscountPrice())
                    .optionValues(optionValues)
                    .build();
                itemDtos.add(dto);
            }

            // 4.3. Tạo OrderResponse cho order hiện tại
            OrderResponseDetail resp = OrderResponseDetail.builder()
                .orderId(order.getId())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .totalAmount(order.getGrandTotal())
                .items(itemDtos)
                .build();

            // 4.4. Thêm vào danh sách trả về
            responses.add(resp);
        }

        // 5. Trả về kết quả cuối cùng
        return responses;
    }
}