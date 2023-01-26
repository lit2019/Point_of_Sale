package com.increff.pos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name = "created_at")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = ZonedDateTime.now();
    }
}