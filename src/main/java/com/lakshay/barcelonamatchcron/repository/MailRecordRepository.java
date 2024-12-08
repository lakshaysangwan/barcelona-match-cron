package com.lakshay.barcelonamatchcron.repository;

import com.lakshay.barcelonamatchcron.entity.MailRecord;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRecordRepository extends JpaRepository<MailRecord, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "false")})
    @Query(value = "SELECT EXISTS(SELECT 1 FROM mail_record WHERE title = ?1)", nativeQuery = true)
    boolean existsByExactTitle(String title);
}