package com.example.demo.service;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final ProductRepository productRepo;

    /** Trừ kho khi đơn đã thanh toán */
    @Transactional
    public void deductInventory(Order order) {
        for (OrderItem oi : order.getItems()) {
            Product p = oi.getProduct();
            if (p.getQuantity() == null || p.getQuantity() < oi.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
            p.setQuantity(p.getQuantity() - oi.getQuantity());
            productRepo.save(p);
        }
    }

    /** Cộng trả kho khi huỷ/hoàn */
    @Transactional
    public void restock(Order order) {
        for (OrderItem oi : order.getItems()) {
            Product p = oi.getProduct();
            p.setQuantity(p.getQuantity() + oi.getQuantity());
            productRepo.save(p);
        }
    }
}
