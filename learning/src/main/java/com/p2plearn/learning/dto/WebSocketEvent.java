package com.p2plearn.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebSocketEvent<T> {

    private String type;

    private T data;
}