package com.ducnv.wsschat.model;

import java.time.Instant;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    private Instant deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        // this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("Unknown");
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
        // this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("Unknown");
    }

    // @PreRemove
    // public void preRemove() {
    //     this.deletedAt = Instant.now();
    // }
}