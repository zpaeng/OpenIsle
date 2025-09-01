package com.openisle.repository;

import com.openisle.model.PollPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PollPostRepository extends JpaRepository<PollPost, Long> {
    List<PollPost> findByEndTimeAfterAndResultAnnouncedFalse(LocalDateTime now);

    List<PollPost> findByEndTimeBeforeAndResultAnnouncedFalse(LocalDateTime now);
}
