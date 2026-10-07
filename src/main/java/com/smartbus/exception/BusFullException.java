package com.smartbus.exception;

public class BusFullException extends RuntimeException {
    public BusFullException(String message) { super(message); }
}
