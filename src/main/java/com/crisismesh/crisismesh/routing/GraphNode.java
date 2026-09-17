package com.crisismesh.crisismesh.routing;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {

    private String nodeId;
    private List<String> connectedNodes = new ArrayList<>();

    public GraphNode(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public List<String> getConnectedNodes() {
        return connectedNodes;
    }

    public void addConnection(String nodeId) {
        connectedNodes.add(nodeId);
    }
}