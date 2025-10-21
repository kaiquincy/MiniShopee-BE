package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.service.OrderService;
// import com.example.demo.service.AdminOrderService;
// import com.example.demo.service.AdminProductService;
import com.example.demo.service.UserService;
// import com.example.demo.service.AuditService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    // private final AdminOrderService orderService;
    private final UserService userService;
    private final OrderService orderService;
    // private final AdminProductService productService;
    // private final AuditService auditService;

    // -------- Orders --------

    // GET /api/admin/orders
    @GetMapping("/orders")
    public ApiResponse<List<AdminOrdersResponse>> listOrders(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status
    ) {
        List<AdminOrdersResponse> result = orderService.listOrders(q, status);
        ApiResponse<List<AdminOrdersResponse>> rsp = new ApiResponse<>();
        rsp.setResult(result);
        return rsp;
    }

    // PATCH /api/admin/orders/{id}/status
    // @PatchMapping("/orders/{orderId}/status")
    // public ApiResponse<SimpleOkResponse> updateOrderStatus(
    //         @PathVariable Long orderId,
    //         @RequestBody UpdateOrderStatusRequest req
    // ) {
    //     if (req == null || req.getStatus() == null) {
    //         throw new AppException(ErrorCode.VALIDATION_FAILED, "status is required");
    //     }
    //     OrderStatus next = req.getStatus();
    //     orderService.updateStatus(orderId, next, req.getNote());
    //     auditService.create(AuditCreateRequest.builder()
    //             .action("ORDER_STATUS")
    //             .actor(auditService.getCurrentActorEmail()) // tuỳ bạn hiện thực
    //             .meta("#" + orderId + " → " + next.name())
    //             .build());
    //     ApiResponse<SimpleOkResponse> rsp = new ApiResponse<>();
    //     rsp.setResult(SimpleOkResponse.ok());
    //     return rsp;
    // }

    // -------- Users --------

    // GET /api/admin/users
    @GetMapping("/users")
    public ApiResponse<List<AdminUsersResponse>> listUsers(
            @RequestParam(required = false) String q
    ) {
        List<User> users = userService.findAll(); // TODO: filter theo q nếu cần
        List<AdminUsersResponse> result = users.stream()
            .map(u -> new AdminUsersResponse(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRole(),
                    u.getPhone(),
                    u.getAvatarUrl(),
                    u.getStatus(),
                    u.getFullName(),
                    u.getGender(),
                    u.getDateOfBirth(),
                    u.getCreatedAt(),
                    u.getUpdatedAt(),
                    u.getLastLoginAt()
            ))
            .collect(Collectors.toList());

        ApiResponse<List<AdminUsersResponse>> rsp = new ApiResponse<>();
        rsp.setResult(result);
        return rsp;
    }

    // PATCH /api/admin/users/{id}
    // @PatchMapping("/users/{userId}")
    // public ApiResponse<SimpleOkResponse> toggleUserActive(
    //         @PathVariable Long userId,
    //         @RequestBody ToggleUserActiveRequest req
    // ) {
    //     if (req == null || req.getActive() == null) {
    //         throw new AppException(ErrorCode.VALIDATION_FAILED, "active is required");
    //     }
    //     userService.setActive(userId, req.getActive());
    //     auditService.create(AuditCreateRequest.builder()
    //             .action("USER_TOGGLE")
    //             .actor(auditService.getCurrentActorEmail())
    //             .meta("userId=" + userId + " → " + (req.getActive() ? "Active" : "Inactive"))
    //             .build());
    //     ApiResponse<SimpleOkResponse> rsp = new ApiResponse<>();
    //     rsp.setResult(SimpleOkResponse.ok());
    //     return rsp;
    // }

    // -------- Products --------

    // GET /api/admin/products
    // @GetMapping("/products")
    // public ApiResponse<List<ProductAdminResponse>> listProducts(
    //         @RequestParam(required = false) String q
    // ) {
    //     List<ProductAdminResponse> result = productService.listProducts(q);
    //     ApiResponse<List<ProductAdminResponse>> rsp = new ApiResponse<>();
    //     rsp.setResult(result);
    //     return rsp;
    // }

    // PATCH /api/admin/products/{id}
    // @PatchMapping("/products/{productId}")
    // public ApiResponse<SimpleOkResponse> toggleProductVisible(
    //         @PathVariable Long productId,
    //         @RequestBody ToggleProductVisibleRequest req
    // ) {
    //     if (req == null || req.getVisible() == null) {
    //         throw new AppException(ErrorCode.VALIDATION_FAILED, "visible is required");
    //     }
    //     productService.setVisible(productId, req.getVisible());
    //     auditService.create(AuditCreateRequest.builder()
    //             .action("PRODUCT_TOGGLE")
    //             .actor(auditService.getCurrentActorEmail())
    //             .meta("productId=" + productId + " → " + (req.getVisible() ? "Visible" : "Hidden"))
    //             .build());
    //     ApiResponse<SimpleOkResponse> rsp = new ApiResponse<>();
    //     rsp.setResult(SimpleOkResponse.ok());
    //     return rsp;
    // }

    // -------- Audit --------

    // GET /api/admin/audit
    // @GetMapping("/audit")
    // public ApiResponse<List<AuditResponse>> listAudit(
    //         @RequestParam(required = false) String q
    // ) {
    //     List<AuditResponse> list = auditService.list(q);
    //     ApiResponse<List<AuditResponse>> rsp = new ApiResponse<>();
    //     rsp.setResult(list);
    //     return rsp;
    // }

    // POST /api/admin/audit
    // @PostMapping("/audit")
    // public ApiResponse<SimpleOkResponse> createAudit(@RequestBody AuditCreateRequest req) {
    //     auditService.create(req);
    //     ApiResponse<SimpleOkResponse> rsp = new ApiResponse<>();
    //     rsp.setResult(SimpleOkResponse.ok());
    //     return rsp;
    // }

    // -------- Revenue summary (dashboard) --------

    // GET /api/admin/revenue?from=2025-09-01&to=2025-09-14
    // @GetMapping("/revenue")
    // public ApiResponse<List<RevenuePoint>> revenue(
    //         @RequestParam(required = false) LocalDate from,
    //         @RequestParam(required = false) LocalDate to
    // ) {
    //     List<RevenuePoint> series = orderService.revenueDaily(from, to);
    //     ApiResponse<List<RevenuePoint>> rsp = new ApiResponse<>();
    //     rsp.setResult(series);
    //     return rsp;
    // }
}
