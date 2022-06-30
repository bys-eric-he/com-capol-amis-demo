package com.capol.amis.response;

/**
 * 封装Response的错误码
 */
public interface IErrorCode {
    Integer getCode();

    String getMessage();
}