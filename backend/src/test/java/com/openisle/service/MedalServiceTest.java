package com.openisle.service;

import com.openisle.dto.MedalDto;
import com.openisle.model.MedalType;
import com.openisle.model.User;
import com.openisle.repository.CommentRepository;
import com.openisle.repository.PostRepository;
import com.openisle.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedalServiceTest {
    @Test
    void getMedalsWithoutUser() {
        CommentRepository commentRepo = mock(CommentRepository.class);
        PostRepository postRepo = mock(PostRepository.class);
        UserRepository userRepo = mock(UserRepository.class);

        MedalService service = new MedalService(commentRepo, postRepo, userRepo);

        List<MedalDto> medals = service.getMedals(null);
        assertFalse(medals.get(0).isCompleted());
        assertFalse(medals.get(1).isCompleted());
        assertFalse(medals.get(2).isCompleted());
    }

    @Test
    void getMedalsWithUser() {
        CommentRepository commentRepo = mock(CommentRepository.class);
        PostRepository postRepo = mock(PostRepository.class);
        UserRepository userRepo = mock(UserRepository.class);

        when(commentRepo.countByAuthor_Id(1L)).thenReturn(120L);
        when(postRepo.countByAuthor_Id(1L)).thenReturn(80L);
        User user = new User();
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.of(2025, 9, 15, 0, 0));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        MedalService service = new MedalService(commentRepo, postRepo, userRepo);
        List<MedalDto> medals = service.getMedals(1L);

        assertTrue(medals.stream().filter(m -> m.getType() == MedalType.COMMENT).findFirst().orElseThrow().isCompleted());
        assertFalse(medals.stream().filter(m -> m.getType() == MedalType.POST).findFirst().orElseThrow().isCompleted());
        assertTrue(medals.stream().filter(m -> m.getType() == MedalType.SEED).findFirst().orElseThrow().isCompleted());
    }
}
