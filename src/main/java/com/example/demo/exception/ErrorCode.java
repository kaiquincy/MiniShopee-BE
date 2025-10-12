package com.example.demo.exception;

import org.antlr.v4.runtime.atn.SemanticContext.OR;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    UNCATEGORIZE_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "Email existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1003, "User not existed", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_EXISTED(1004, "Product not existed", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(1005,"Role not existed", HttpStatus.NOT_FOUND),
    LECTURE_NOT_EXISTED(1005,"Lecture not existed", HttpStatus.NOT_FOUND),
    ENROLLMENT_EMPTY(1006, "This user have no enrollment", HttpStatus.NOT_FOUND),
    ENROLLMENT_EXISTED(1007, "User already enrolled in this course", HttpStatus.BAD_REQUEST),
    CART_NOT_EXISTED(1008, "Cart not existed", HttpStatus.NOT_FOUND),
    ITEM_IN_CART_NOT_EXISTED(1009, "Item not existed in cart", HttpStatus.NOT_FOUND),
    CART_EMPTY(1010, "No item in your cart!", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXISTED(1011, "Order not existed", HttpStatus.NOT_FOUND),
    ORDER_ITEM_NOT_EXISTED(1012, "Order item not existed", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_EXISTED(1013, "Payment not existed", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXISTED(1014, "Category not existed", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_EXISTED(1015, "Address not existed", HttpStatus.NOT_FOUND),

    USERNAME_INVALID(1102, "Username must be between 5 and 15 characters", HttpStatus.BAD_REQUEST),
    USERNAME_NULL(1103, "Username cannot be null", HttpStatus.BAD_REQUEST),
    TITLE_NULL(1104, "Title cannot be null", HttpStatus.BAD_REQUEST),
    DESC_NULL(1105, "Desc cannot be null", HttpStatus.BAD_REQUEST),
    PRICE_NULL(1106, "Price cannot be null", HttpStatus.BAD_REQUEST),
    TEACHERID_NULL(1107, "Teacherid cannot be null", HttpStatus.BAD_REQUEST),
    EMAIL_BLANK(1111, "Email cannot be blank", HttpStatus.BAD_REQUEST),
    FULLNAME_BLANK(1121,"Fullname cannot be blank", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1131,"Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    FILE_MISSING(1122,"Please select a file to upload!", HttpStatus.BAD_REQUEST),
    TRANSACTION_NULL(1123,"User have no transactions", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_RATETING(1124, "Star must be between 1-5", HttpStatus.BAD_REQUEST),
    PAYMENTMETHOD_NULL(1125, "Payment method cannot be null", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_STATUS(1126, "Invalid payment status", HttpStatus.BAD_REQUEST),
    ADDRESS_FULLNAME_NULL(1127, "Fullname cannot be null", HttpStatus.BAD_REQUEST),
    ADDRESS_PHONE_NULL(1128, "Phone cannot be null", HttpStatus.BAD_REQUEST),
    ADDRESS_LINE1_NULL(1129, "Line1 cannot be null", HttpStatus.BAD_REQUEST),
    ADDRESS_CITY_NULL(1130, "City cannot be null", HttpStatus.BAD_REQUEST), 

    PERMISSION_COURSE_DENIED(1201, "You don't have enough permission to this course", HttpStatus.FORBIDDEN),
    FORBIDDEN(1202, "You don't have enough permission to this resource", HttpStatus.FORBIDDEN),

    INSUFFICIENT_FUNDS(1301, "Insufficient funds to complete the purchase", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(2001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(2002, "Access denied", HttpStatus.FORBIDDEN),

    RATE_OF_COMMENT(2100, "Rating must be between 1 and 5.", HttpStatus.BAD_REQUEST),

    NOTIFICATION_NOT_FOUND(2200, "Notification not found", HttpStatus.BAD_REQUEST),
    MARK_AS_READ_FAIL(2201, "mark as read fail", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(2202, "Category not found", HttpStatus.NOT_FOUND),
    PARENT_CATEGORY_NOT_FOUND(2203, "Parent category not found", HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND(2204, "Resource not found", HttpStatus.NOT_FOUND),

    QUANTITY_NULL(2300, "Quantity can't be null", HttpStatus.BAD_REQUEST),
    DUPLICATE_RESOURCE(2301, "You rated this product!", HttpStatus.CONFLICT),
    
    OUT_OF_STOCK(2302, "Out of stock", HttpStatus.BAD_REQUEST),
    INVALID_STATUS_TRANSITION(2303, "You can't change the status of order like that! Read OrderStatusSerive.java for receipt", HttpStatus.BAD_REQUEST), 
    INVALID_REQUEST(2304, "Invalid request", HttpStatus.BAD_REQUEST),

    ORDER_NOT_COMPLETED(2305, "Order is not completed yet", HttpStatus.BAD_REQUEST)
    ;


    private int code;
    private String message;
    private HttpStatusCode statusCode;

    private ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }
    
}
