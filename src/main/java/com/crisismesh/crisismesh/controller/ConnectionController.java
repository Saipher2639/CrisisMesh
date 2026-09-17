package com.crisismesh.crisismesh.controller;

import com.crisismesh.crisismesh.model.Connection;
import com.crisismesh.crisismesh.repository.ConnectionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionRepository connectionRepository;

    public ConnectionController(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @PostMapping
    public Connection createConnection(@RequestBody Connection connection) {
        return connectionRepository.save(connection);
    }

    @GetMapping
    public List<Connection> getAllConnections() {
        return connectionRepository.findAll();
    }
}