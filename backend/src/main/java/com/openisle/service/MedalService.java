package com.openisle.service;

import com.openisle.dto.CommentMedalDto;
import com.openisle.dto.MedalDto;
import com.openisle.dto.PostMedalDto;
import com.openisle.dto.SeedUserMedalDto;
import com.openisle.model.MedalType;
import com.openisle.repository.CommentRepository;
import com.openisle.repository.PostRepository;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedalService {
    private static final long COMMENT_TARGET = 100;
    private static final long POST_TARGET = 100;
    private static final LocalDateTime SEED_USER_DEADLINE = LocalDateTime.of(2025, 9, 16, 0, 0);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<MedalDto> getMedals(Long userId) {
        List<MedalDto> medals = new ArrayList<>();

        CommentMedalDto commentMedal = new CommentMedalDto();
        commentMedal.setIcon("https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/achi_comment.png");
        commentMedal.setTitle("评论达人");
        commentMedal.setDescription("评论超过100条");
        commentMedal.setType(MedalType.COMMENT);
        commentMedal.setTargetCommentCount(COMMENT_TARGET);
        if (userId != null) {
            long count = commentRepository.countByAuthor_Id(userId);
            commentMedal.setCurrentCommentCount(count);
            commentMedal.setCompleted(count >= COMMENT_TARGET);
        } else {
            commentMedal.setCurrentCommentCount(0);
            commentMedal.setCompleted(false);
        }
        medals.add(commentMedal);

        PostMedalDto postMedal = new PostMedalDto();
        postMedal.setIcon("https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/achi_post.png");
        postMedal.setTitle("发帖达人");
        postMedal.setDescription("发帖超过100条");
        postMedal.setType(MedalType.POST);
        postMedal.setTargetPostCount(POST_TARGET);
        if (userId != null) {
            long count = postRepository.countByAuthor_Id(userId);
            postMedal.setCurrentPostCount(count);
            postMedal.setCompleted(count >= POST_TARGET);
        } else {
            postMedal.setCurrentPostCount(0);
            postMedal.setCompleted(false);
        }
        medals.add(postMedal);

        SeedUserMedalDto seedUserMedal = new SeedUserMedalDto();
        seedUserMedal.setIcon("https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/achi_seed.png");
        seedUserMedal.setTitle("种子用户");
        seedUserMedal.setDescription("2025.9.16前注册的用户");
        seedUserMedal.setType(MedalType.SEED);
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                seedUserMedal.setRegisterDate(user.getCreatedAt());
                seedUserMedal.setCompleted(user.getCreatedAt().isBefore(SEED_USER_DEADLINE));
            });
            if (seedUserMedal.getRegisterDate() == null) {
                seedUserMedal.setCompleted(false);
            }
        } else {
            seedUserMedal.setCompleted(false);
        }
        medals.add(seedUserMedal);

        return medals;
    }
}
