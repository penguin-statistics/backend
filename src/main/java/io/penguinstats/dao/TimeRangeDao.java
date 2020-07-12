package io.penguinstats.dao;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.TimeRange;

@Repository
public interface TimeRangeDao extends MongoRepository<TimeRange, String> {

	Optional<TimeRange> findByRangeID(String rangeID);

}
