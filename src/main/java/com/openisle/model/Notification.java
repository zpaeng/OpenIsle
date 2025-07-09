package com.openisle.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a user notification.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column
    private Boolean approved;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false,
            columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private LocalDateTime createdAt;
}
