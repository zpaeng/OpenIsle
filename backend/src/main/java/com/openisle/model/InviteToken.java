package com.openisle.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Invite token entity tracking usage counts.
 */
@Data
@Entity
public class InviteToken {
    @Id
    private String token;

    @ManyToOne
    private User inviter;

    private LocalDate createdDate;

    private int usageCount;
}
