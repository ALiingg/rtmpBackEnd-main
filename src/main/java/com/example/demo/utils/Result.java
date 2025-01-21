package com.example.demo.utils;

/**
 * A generic class representing the result of an operation.
 * Contains a status code, message, and optional data of type T.
 * @param <T> The type of data to be returned in the result.
 */
public class Result<T> {
    private String code; // Status code representing success or failure
    private String msg;  // Message providing additional information about the result
    private T data;      // Generic data field to hold the result data

    // Getter and setter for the status code
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // Getter and setter for the message
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    // Getter and setter for the data
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Default constructor
    public Result() {
    }

    // Constructor to initialize Result with data
    public Result(T data) {
        this.data = data;
    }

    /**
     * Creates a success result with no data.
     * @return A Result object with code "0" and message "成功" (Success).
     */
    public static Result success() {
        Result result = new Result<>();
        result.setCode("0"); // Set code to indicate success
        result.setMsg("成功"); // Success message in Chinese
        return result;
    }

    /**
     * Creates a success result with specified data.
     * @param data The data to be included in the result
     * @return A Result object with code "0", message "成功", and the specified data.
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>(data);
        result.setCode("0"); // Set code to indicate success
        result.setMsg("成功"); // Success message in Chinese
        return result;
    }

    /**
     * Creates a success result with specified data and a custom message.
     * @param data The data to be included in the result
     * @param msg A custom success message
     * @return A Result object with code "0", the specified message, and data.
     */
    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<>(data);
        result.setCode("0"); // Set code to indicate success
        result.setMsg(msg); // Custom success message
        return result;
    }

    /**
     * Creates an error result with a specific error code and message.
     * @param code The error code to indicate the type of error
     * @param msg The error message describing the error
     * @return A Result object with the specified code and message, no data.
     */
    public static Result error(String code, String msg) {
        Result result = new Result();
        result.setCode(code); // Set code to indicate error
        result.setMsg(msg); // Error message
        return result;
    }
}
