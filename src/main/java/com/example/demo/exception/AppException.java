package com.example.demo.exception;

public class AppException extends RuntimeException {
    private ErrorCode errorCode;

    // Constructor giữ nguyên để hỗ trợ AppException(ErrorCode.USER_NOT_EXISTED)
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // Constructor mới với message tùy chỉnh
    public AppException(ErrorCode errorCode, String message) {
        super(message != null && !message.isEmpty() ? message : errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}