package com.lakshay.barcelonamatchcron.repository;

import com.lakshay.barcelonamatchcron.entity.MailRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRecordRepository extends JpaRepository<MailRecord, Long> {
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MailRecord m WHERE m.title = :title")
    boolean existsByExactTitle(@Param("title") String title);
}