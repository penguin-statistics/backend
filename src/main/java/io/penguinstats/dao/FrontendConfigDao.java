package io.penguinstats.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.FrontendConfig;

@Repository
public interface FrontendConfigDao extends MongoRepository<FrontendConfig, String> {

    FrontendConfig findByKey(String key);

}
