package com.hugo.test.controllers;


import org.springframework.http.HttpStatus;

public class DataServiceResponse {
    private String result = "";
    private Integer total = 0;
    private HttpStatus httpStatus;
    private String status = "";
    private Object content = null;

    public DataServiceResponse(){
        result = "";
        total = 0;
        httpStatus = HttpStatus.NOT_FOUND;
        status = HttpStatus.NOT_FOUND.toString();
        content = null;
    }

    public DataServiceResponse(String result, Integer total, String status){
        this.result = result;
        this.total = total;
        this.httpStatus = HttpStatus.OK;
        this.status = status;
        this.content = null;
    }

    public DataServiceResponse(String result, Integer total, HttpStatus httpStatus){
        this.result = result;
        this.total = total;
        this.httpStatus = httpStatus;
        this.status = httpStatus.toString();
        this.content = null;
    }

    public DataServiceResponse(String result, Integer total, String status, Object content){
        this.result = result;
        this.total = total;
        this.httpStatus = HttpStatus.OK;
        this.status = status;
        this.content = content;
    }

    public DataServiceResponse(String result, Integer total, HttpStatus httpStatus, Object content){
        this.result = result;
        this.total = total;
        this.httpStatus = httpStatus;
        this.status = httpStatus.toString();
        this.content = content;
    }

    public String getResult(){
        return this.result;
    }

    public void setResult(String result){
        this.result = result;
    }

    public Integer getTotal() {
        return this.total;
    }

    public void setTotal(Integer total){
        this.total = total;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus){
        this.httpStatus = httpStatus;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content){
        this.content = content;
    }
}
