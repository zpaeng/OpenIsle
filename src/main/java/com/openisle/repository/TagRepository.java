package com.openisle.repository;

import com.openisle.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByNameContainingIgnoreCase(String keyword);
    List<Tag> findByApproved(boolean approved);
    List<Tag> findByApprovedTrue();
    List<Tag> findByNameContainingIgnoreCaseAndApprovedTrue(String keyword);
}
