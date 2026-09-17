package com.crisismesh.crisismesh.repository;

import com.crisismesh.crisismesh.model.Connection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConnectionRepository extends MongoRepository<Connection, String> {
}