package com.example.demo.scheduler;

import com.example.demo.model.Order;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderScheduler {
    private final OrderRepository orderRepo;
    private final OrderStatusService orderStatusService;

    /** Mỗi 5 phút: auto-cancel các đơn PENDING quá 30 phút */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cancelExpiredPending() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30); // 30 phút trước
        List<Order> list = orderRepo.findByStatusAndCreatedAtBefore(OrderStatus.PENDING.name(), threshold);
        // Duyệt qua các đơn hàng và cập nhật trạng thái thành CANCELLED
        for (Order o : list) {
            orderStatusService.updateStatus(o.getId(), OrderStatus.CANCELLED, "Auto-cancel due to timeout");
        }
    }

    /** Mỗi 1 giờ: auto-complete đơn đã DELIVERED quá 3 ngày */
    // @Scheduled(fixedRate = 60 * 60 * 1000)
    // public void completeDelivered() {
    //     LocalDateTime threshold = LocalDateTime.now().minusDays(3);
    //     List<Order> list = orderRepo.findByStatusAndCreatedAtBefore(OrderStatus.DELIVERED.name(), threshold);
    //     for (Order o : list) {
    //         orderStatusService.updateStatus(o.getId(), OrderStatus.COMPLETED, "Auto-complete after 3 days");
    //     }
    // }
}
