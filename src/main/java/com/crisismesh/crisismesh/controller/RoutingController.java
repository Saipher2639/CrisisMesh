package com.crisismesh.crisismesh.controller;

import com.crisismesh.crisismesh.routing.RoutingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routing")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping
    public List<String> findRoute(
            @RequestParam String source,
            @RequestParam String target) {

        return routingService.findRoute(source, target);
    }
}