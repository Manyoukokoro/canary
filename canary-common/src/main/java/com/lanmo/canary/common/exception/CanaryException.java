package com.lanmo.canary.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by bowen on 2017/1/17.
 */
@JsonIgnoreProperties("cause")
public class CanaryException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;


    public CanaryException(String message) {
        super(message);
    }

    public CanaryException(String message, Throwable cause) {
        super(message, cause);
    }


}
