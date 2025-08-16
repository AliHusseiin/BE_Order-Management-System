package com.ejada.oms.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author Ali Hussein
 * @param <T> the type of data to be wrapped
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    private String message;
    private int statusCode;
    private boolean success;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private T data;

    // Static factory methods for common responses
    public static <T> ResponseDto<T> success(T data) {
        return ResponseDto.<T>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public static <T> ResponseDto<T> success(String message) {
        return ResponseDto.<T>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }



    public static <T> ResponseDto<T> success(String message, HttpStatus status) {
        return ResponseDto.<T>builder()
                .success(true)
                .statusCode(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ResponseDto<T> success(T data, String message) {
        return ResponseDto.<T>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    public static <T> ResponseDto<T> success(T data, String message, HttpStatus status) {
        return ResponseDto.<T>builder()
                .success(true)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ResponseDto<T> error(String message, HttpStatus status) {
        return ResponseDto.<T>builder()
                .success(false)
                .statusCode(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}