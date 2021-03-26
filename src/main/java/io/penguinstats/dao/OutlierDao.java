package io.penguinstats.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.Outlier;

@Repository
public interface OutlierDao extends MongoRepository<Outlier, String> {

}
