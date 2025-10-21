package com.example.demo.service;

import com.example.demo.dto.AddressRequest;
import com.example.demo.dto.AddressResponse;
import com.example.demo.exception.AppException;
import com.example.demo.model.Address;
import com.example.demo.model.User;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepo;
    private final UserRepository userRepo;
    private final UserService userService;

    public List<AddressResponse> myAddresses() {
        Long uid = userService.getCurrentUserId();
        return addressRepo.findByUserIdOrderByIsDefaultDescUpdatedAtDesc(uid)
                .stream().map(AddressResponse::new).toList();
    }

    @Transactional
    public AddressResponse create(AddressRequest req) {
        validate(req, true);

        Long uid = userService.getCurrentUserId();
        User user = userRepo.findById(uid)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Address a = Address.builder()
                .user(user)
                .fullName(req.getFullName())
                .label(req.getLabel())
                .phone(req.getPhone())
                .line1(req.getLine1())
                .ward(req.getWard())
                .district(req.getDistrict())
                .city(req.getCity())
                .isDefault(Boolean.TRUE.equals(req.getIsDefault()))
                .build();

        // Nếu set mặc định, bỏ mặc định cũ
        if (Boolean.TRUE.equals(a.getIsDefault())) {
            addressRepo.findByUserIdAndIsDefaultTrue(uid)
                    .ifPresent(old -> { old.setIsDefault(false); addressRepo.save(old); });
        } else {
            // Nếu user chưa có địa chỉ nào, auto set default = true
            boolean hasAny = !addressRepo.findByUserIdOrderByIsDefaultDescUpdatedAtDesc(uid).isEmpty();
            if (!hasAny) a.setIsDefault(true);
        }

        Address saved = addressRepo.save(a);
        return new AddressResponse(saved);
    }

    @Transactional
    public AddressResponse update(Long id, AddressRequest req) {
        validate(req, false);

        Long uid = userService.getCurrentUserId();
        Address a = addressRepo.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        if (req.getFullName() != null) a.setFullName(req.getFullName());
        if (req.getLabel() != null) a.setLabel(req.getLabel());
        if (req.getPhone() != null) a.setPhone(req.getPhone());
        if (req.getLine1() != null) a.setLine1(req.getLine1());
        if (req.getWard() != null) a.setWard(req.getWard());
        if (req.getDistrict() != null) a.setDistrict(req.getDistrict());
        if (req.getCity() != null) a.setCity(req.getCity());

        if (req.getIsDefault() != null && req.getIsDefault()) {
            // gỡ default cũ
            addressRepo.findByUserIdAndIsDefaultTrue(uid)
                    .ifPresent(old -> { if (!old.getId().equals(a.getId())) { old.setIsDefault(false); addressRepo.save(old);} });
            a.setIsDefault(true);
        }

        Address saved = addressRepo.save(a);
        return new AddressResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Long uid = userService.getCurrentUserId();
        Address a = addressRepo.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        boolean wasDefault = Boolean.TRUE.equals(a.getIsDefault());
        addressRepo.delete(a);

        // Nếu xóa default, chọn default mới (nếu còn)
        if (wasDefault) {
            List<Address> remains = addressRepo.findByUserIdOrderByIsDefaultDescUpdatedAtDesc(uid);
            if (!remains.isEmpty() && !Boolean.TRUE.equals(remains.get(0).getIsDefault())) {
                remains.get(0).setIsDefault(true);
                addressRepo.save(remains.get(0));
            }
        }
    }

    @Transactional
    public AddressResponse markDefault(Long id) {
        Long uid = userService.getCurrentUserId();
        Address a = addressRepo.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        addressRepo.findByUserIdAndIsDefaultTrue(uid)
                .ifPresent(old -> { if (!old.getId().equals(a.getId())) { old.setIsDefault(false); addressRepo.save(old);} });

        a.setIsDefault(true);
        return new AddressResponse(addressRepo.save(a));
    }

    private void validate(AddressRequest r, boolean creating) {
        if (creating) {
            if (r.getFullName() == null || r.getFullName().isBlank())
                throw new AppException(ErrorCode.ADDRESS_FULLNAME_NULL);
            if (r.getPhone() == null || r.getPhone().isBlank())
                throw new AppException(ErrorCode.ADDRESS_PHONE_NULL);
            if (r.getLine1() == null || r.getLine1().isBlank())
                throw new AppException(ErrorCode.ADDRESS_LINE1_NULL);
            if (r.getCity() == null || r.getCity().isBlank())
                throw new AppException(ErrorCode.ADDRESS_CITY_NULL);
        }
    }
}
