package com.crisismesh.crisismesh.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Document(collection = "nodes")
public class Node {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String ipAddress;

    private int batteryLevel;

    private int signalStrength;

    private boolean active;
}