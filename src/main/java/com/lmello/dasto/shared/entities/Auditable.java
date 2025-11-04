package com.lmello.dasto.shared.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class Auditable {
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private String createdBy;

    private OffsetDateTime updatedAt;
    private String updatedBy;

    private OffsetDateTime deletedAt;
    private String deletedBy;
}
