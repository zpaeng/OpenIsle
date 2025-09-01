package com.openisle.repository;

import com.openisle.model.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PollVoteRepository extends JpaRepository<PollVote, Long> {
    List<PollVote> findByPostId(Long postId);
}
