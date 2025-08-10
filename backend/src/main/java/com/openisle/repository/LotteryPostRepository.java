package com.openisle.repository;

import com.openisle.model.LotteryPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotteryPostRepository extends JpaRepository<LotteryPost, Long> {
}
