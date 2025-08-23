package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PaymentCallbackDto;
import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Order;
import com.example.demo.model.Payment;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRe;
    private final OrderRepository orderRepo;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    PayOS payOS;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiate(
            @RequestBody PaymentRequest req) {
        PaymentResponse pr = paymentService.initiate(PaymentMethod.PAYOS, 11L);
        ApiResponse<PaymentResponse> resp = new ApiResponse<>();
        resp.setResult(pr);
        return ResponseEntity.ok(resp);
    }

    //When user paid the money
    @PostMapping(path = "/confirm-webhook")
    public ObjectNode payosTransferHandler(@RequestBody ObjectNode body)
        throws JsonProcessingException, IllegalArgumentException {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);

        try {
            // Init Response
            response.put("error", 0);
            response.put("message", "Webhook delivered");
            response.set("data", null);

            WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);
            
            //set  transaction's status to "PURCHASE"
            Payment currentTransaction = paymentRe.getPaymentByTransactionId(data.getPaymentLinkId()).get();
            
            paymentService.changeStatus(currentTransaction, "SUCCESS");

            // Order order = orderRepo.findById(orderId)
            //     .orElseThrow(() -> new AppException(
            //         ErrorCode.ORDER_NOT_EXISTED));
            // order.setStatus(OrderStatus.PAID.toString());
            // orderRepo.save(order);


            //add enrollment
            // System.out.println(currentTransaction.getProductId());
            // enrollmentService.addEnrollment(currentTransaction.getProductId(), currentTransaction.getUserId());

            //mail
            // String msg = "You have successfully registered for the course! Click this link to navigate to your course : " ;
            // String email = "kawdwk@gmail.com"; 
    
            // emailService.confirmRegisterCourse(msg, email);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }


    //cái này vô nghĩa, dùng để lưu webhookUrl vào hệ thống trên payos
    // @PostMapping(path = "/confirm-webhook")
    //     public ObjectNode confirmWebhook(@RequestBody ObjectNode requestBody) {
    //         ObjectMapper objectMapper = new ObjectMapper();
    //         ObjectNode response = objectMapper.createObjectNode();
    //         try {
    //             // Ghi log JSON body để debug
    //             logger.info("Received webhook JSON: {}", requestBody.toString());

    //             // Trích xuất webhookUrl (nếu có) hoặc xử lý JSON tùy theo logic
    //             String webhookUrl = requestBody.has("webhookUrl") ? requestBody.get("webhookUrl").asText() : null;
    //             if (webhookUrl != null) {
    //                 String result = payOS.confirmWebhook(webhookUrl);
    //                 response.put("error", 0);
    //                 response.put("message", "Webhook processed successfully");
    //                 response.set("data", objectMapper.valueToTree(result));
    //             } else {
    //                 // Xử lý JSON khác (ví dụ: webhook từ PayOS)
    //                 logger.info("No webhookUrl found, processing as PayOS webhook");
    //                 // Nếu PayOS yêu cầu verify webhook, chuyển JSON sang Webhook
    //                 Webhook payosWebhook = objectMapper.convertValue(requestBody, Webhook.class);
    //                 Object data = payOS.verifyPaymentWebhookData(payosWebhook);
    //                 response.put("error", 0);
    //                 response.put("message", "Webhook verified successfully");
    //                 response.set("data", objectMapper.valueToTree(data));
    //             }
    //             return response;
    //         } catch (Exception e) {
    //             logger.error("Error processing webhook", e);
    //             response.put("error", -1);
    //             response.put("message", e.getMessage());
    //             response.set("data", null);
    //             return response;
    //         }
    //     }


}
