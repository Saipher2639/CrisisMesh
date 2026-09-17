package com.crisismesh.crisismesh.model;

import lombok.Data;

@Data
public class Message {

    private String messageId;

    private String source;

    private String target;

    private String content;

    private int priority;

    private boolean acknowledgement;
}