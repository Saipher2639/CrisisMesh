package com.crisismesh.crisismesh.controller;

import com.crisismesh.crisismesh.model.Node;
import com.crisismesh.crisismesh.repository.NodeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {

    private final NodeRepository nodeRepository;

    public NodeController(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @PostMapping
    public Node createNode(@RequestBody Node node) {
        return nodeRepository.save(node);
    }

    @GetMapping
    public List<Node> getAllNodes() {
        return nodeRepository.findAll();
    }

    @PutMapping("/{name}/status")
    public Node updateStatus(
            @PathVariable String name,
            @RequestParam boolean active) {

        Node node = nodeRepository.findByName(name)
                .orElseThrow();

        node.setActive(active);

        return nodeRepository.save(node);
    }
}