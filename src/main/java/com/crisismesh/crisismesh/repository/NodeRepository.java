package com.crisismesh.crisismesh.repository;

import com.crisismesh.crisismesh.model.Node;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NodeRepository extends MongoRepository<Node, String> {

    Optional<Node> findByName(String name);
}