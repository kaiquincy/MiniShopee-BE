package com.example.demo.service;

import java.util.Date;

import javax.management.RuntimeErrorException;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.controller.OrderController;
import com.example.demo.dto.PaymentCallbackDto;
import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Payment;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderItemRepository orderitemReposityry;

    @Autowired
    PayOS payOS;

    /** Tạo giao dịch và redirect URL */
    @Transactional
    public PaymentResponse initiate(PaymentMethod method, Long orderId) {
        Order order = orderRepo.findById(orderId)
            .orElseThrow(() -> new AppException(
                ErrorCode.ORDER_NOT_EXISTED));

        List<OrderItem> orderItems = orderitemReposityry.findByOrderId(orderId);
        if (orderItems.isEmpty()) {
            throw new AppException(ErrorCode.ORDER_ITEM_NOT_EXISTED);
        }
        for (OrderItem oi : orderItems) {
            logger.info("Creating payment for order: {}, item: {}", orderId, oi.getProduct().getName());
        }
        // 1) tạo record Payment, status=PENDING
        Payment p = Payment.builder()
            .order(order)
            .amount(order.getGrandTotal())
            .currency("VND")
            .method(method)
            .status(PaymentStatus.PENDING)
            .build();
        // paymentRepo.save(p);

        // 2) gọi gateway, nhận transactionId và paymentUrl
        // GatewayResponse gw = gatewayClient.createTransaction(p);
        try {
            final String productName = "Payment for order " + orderId;
            final String description = orderId.toString();
            final String returnUrl = "http://localhost:5173/payment/return"; // replace with actual return UR
            final String cancelUrl = "Http://localhost:5173/payment/cancel"; // replace with actual cancel URl
            double total = orderItems.stream()
                    .mapToDouble(oi -> oi.getPrice() * oi.getQuantity())
                    .sum()*25000; // Assuming price is in USD, convert to VND
            int price = (int) Math.round(total);
            logger.info("Creating payment link for price: {}", price);
            // Gen order code
            String currentTimeString = String.valueOf(String.valueOf(new Date().getTime()));
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));


            CreatePaymentLinkRequest paymentData =
                CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
                    .amount((long) price)
                    .description(description)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .item(PaymentLinkItem.builder().name(productName).price((long) price).quantity(1).build())
                    .build();
            CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);


            // 3) cập nhật transactionId, callbackData, trả về URL
            // p.setTransactionId(gw.getTransactionId());
            p.setTransactionId(data.getPaymentLinkId());
            paymentRepo.save(p);


            return new PaymentResponse(p.getTransactionId(), data.getCheckoutUrl(), p.getStatus());
        } catch (Exception e) {
            throw new RuntimeErrorException(null, "Error creating payment link: " + e.getMessage());
        }
    }

    public String changeStatus(Payment payment, String status) {
        if (payment == null) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED);
        }

        if (status.equals("SUCCESS")) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else if (status.equals("FAILED")) {
            payment.setStatus(PaymentStatus.FAILED);
        } else {
            throw new AppException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        paymentRepo.save(payment);
        return payment.getStatus().toString();
    }

}
