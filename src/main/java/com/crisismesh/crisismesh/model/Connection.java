package com.crisismesh.crisismesh.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "connections")
public class Connection {

    @Id
    private String id;

    private String sourceNodeId;

    private String targetNodeId;

    private int signalStrength;

    private int latency;

    private boolean active;
}