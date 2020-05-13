package io.penguinstats.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.penguinstats.model.Notice;

@Repository
public interface NoticeDao extends MongoRepository<Notice, String> {

}
