package com.crisismesh.crisismesh.routing;

import com.crisismesh.crisismesh.model.Connection;
import com.crisismesh.crisismesh.repository.ConnectionRepository;
import org.springframework.stereotype.Service;
import com.crisismesh.crisismesh.repository.NodeRepository;
import com.crisismesh.crisismesh.model.Node;

import java.util.*;

@Service
public class RoutingService {

    private final ConnectionRepository connectionRepository;
    private final NodeRepository nodeRepository;

    public RoutingService(
            ConnectionRepository connectionRepository,
            NodeRepository nodeRepository) {

        this.connectionRepository = connectionRepository;
        this.nodeRepository = nodeRepository;
    }

    public List<String> findRoute(String source, String target) {

        List<Connection> connections = connectionRepository.findAll();

        Map<String, List<String>> graph = new HashMap<>();

        for (Connection connection : connections) {

            if (!connection.isActive()) {
                continue;
            }
            Node sourceNode = nodeRepository.findByName(connection.getSourceNodeId())
                    .orElse(null);

            Node targetNode = nodeRepository.findByName(connection.getTargetNodeId())
                    .orElse(null);

            if (sourceNode == null || targetNode == null) {
                continue;
            }

            if (!sourceNode.isActive() || !targetNode.isActive()) {
                continue;
            }
            graph
                    .computeIfAbsent(connection.getSourceNodeId(), k -> new ArrayList<>())
                    .add(connection.getTargetNodeId());
            graph
                    .computeIfAbsent(connection.getTargetNodeId(), k -> new ArrayList<>())
                    .add(connection.getSourceNodeId());
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();

        queue.add(source);
        parent.put(source, null);

        while (!queue.isEmpty()) {

            String current = queue.poll();

            if (current.equals(target)) {
                break;
            }

            for (String neighbour : graph.getOrDefault(current, new ArrayList<>())) {

                if (!parent.containsKey(neighbour)) {
                    parent.put(neighbour, current);
                    queue.add(neighbour);
                }
            }
        }

        if (!parent.containsKey(target)) {
            return new ArrayList<>();
        }

        List<String> route = new ArrayList<>();

        String current = target;

        while (current != null) {
            route.add(current);
            current = parent.get(current);
        }

        Collections.reverse(route);

        return route;
    }
}