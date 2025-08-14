package com.example.demo.service;

import com.example.demo.dto.OrderStatusUpdateResponse;
import com.example.demo.enums.OrderStatus;
import com.example.demo.dto.NotificationRequest;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.*;
import com.example.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.example.demo.enums.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class OrderStatusService {
    private final OrderRepository orderRepo;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    // Bảng chuyển trạng thái cho phép
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = new EnumMap<>(OrderStatus.class);
    static {
        ALLOWED.put(PENDING,    Set.of(PAID, CANCELLED, PROCESSING));
        ALLOWED.put(PAID,       Set.of(PROCESSING, CANCELLED, REFUNDED));
        ALLOWED.put(PROCESSING, Set.of(SHIPPING, REFUNDED));
        ALLOWED.put(SHIPPING,   Set.of(DELIVERED, REFUNDED));
        ALLOWED.put(DELIVERED,  Set.of(COMPLETED, REFUNDED));
        ALLOWED.put(COMPLETED,  Set.of());      // (tuỳ chọn cho phép REFUNDED)
        ALLOWED.put(CANCELLED,  Set.of());
        ALLOWED.put(REFUNDED,   Set.of());
    }

    private boolean allowedTransition(OrderStatus from, OrderStatus to) {
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }

    private OrderStatus asEnum(Order o) {
        return OrderStatus.valueOf(o.getStatus());
    }

    private void setStatus(Order o, OrderStatus s) {
        o.setStatus(s.name());
    }

    @Transactional
    public OrderStatusUpdateResponse updateStatus(Long orderId, OrderStatus target, String note) {
        Order order = orderRepo.findById(orderId)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        OrderStatus from = asEnum(order);
        if (from == target) {
            return new OrderStatusUpdateResponse(orderId, from, target, LocalDateTime.now(), "No change");
        }
        if (!allowedTransition(from, target)) {
            throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION);
        }

        // Side-effects theo cặp chuyển
        if (from == PENDING && (target == PAID || target == PROCESSING)) {
            // đã thanh toán/seller confirm → trừ kho
            inventoryService.deductInventory(order);
        }
        if (from == PAID && target == CANCELLED) {
            // huỷ sau khi đã thanh toán → hoàn tiền + trả kho
            // paymentService.refund(order);
            inventoryService.restock(order);
        }
        if ((from == PROCESSING || from == SHIPPING || from == DELIVERED) && target == REFUNDED) {
            // paymentService.refund(order);
            // tuỳ case có trả kho hay không (nếu hàng trả về kho)
            inventoryService.restock(order);
        }

        setStatus(order, target);
        orderRepo.save(order);

        // Notification to user
        notificationService.createNotification(new NotificationRequest() {{
            setUserId(order.getUser().getId());
            setMessage("Đơn hàng #" + order.getId() + " chuyển trạng thái: " + from + " → " + target);
            setType("ORDER_UPDATED");
            setReferenceId(order.getId());
        }});

        return new OrderStatusUpdateResponse(orderId, from, target, LocalDateTime.now(), note != null ? note : "OK");
    }
}
