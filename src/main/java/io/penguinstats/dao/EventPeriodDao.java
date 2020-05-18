package io.penguinstats.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.EventPeriod;

@Repository
public interface EventPeriodDao extends MongoRepository<EventPeriod, String> {

}
