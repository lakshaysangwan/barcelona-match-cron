package com.lakshay.barcelonamatchcron.repository;

import com.lakshay.barcelonamatchcron.entity.MailRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailRecordRepository extends MongoRepository<MailRecord, String> {
    Optional<MailRecord> findByTitle(String postTitle);
}
