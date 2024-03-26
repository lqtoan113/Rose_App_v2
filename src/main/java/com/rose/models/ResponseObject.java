package com.rose.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject implements Serializable {

    private String status;
    private String message;
    private Object data;
    private Integer totalRecord;
}
