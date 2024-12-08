package com.lakshay.barcelonamatchcron.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "mail_record")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    public static class MailRecordBuilder {
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;

        public MailRecord build() {
            if (createdDate == null) {
                createdDate = LocalDateTime.now();
            }
            if (updatedDate == null) {
                updatedDate = LocalDateTime.now();
            }
            return new MailRecord(id, title, url, createdDate, updatedDate);
        }
    }
}