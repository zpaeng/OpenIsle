package com.openisle.service;

import com.openisle.dto.CommentMedalDto;
import com.openisle.dto.MedalDto;
import com.openisle.dto.PostMedalDto;
import com.openisle.dto.SeedUserMedalDto;
import com.openisle.model.MedalType;
import com.openisle.model.User;
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
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }
        MedalType selected = user != null ? user.getDisplayMedal() : null;

        CommentMedalDto commentMedal = new CommentMedalDto();
        commentMedal.setIcon("https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/achi_comment.png");
        commentMedal.setTitle("评论达人");
        commentMedal.setDescription("评论超过100条");
        commentMedal.setType(MedalType.COMMENT);
        commentMedal.setTargetCommentCount(COMMENT_TARGET);
        if (user != null) {
            long count = commentRepository.countByAuthor_Id(userId);
            commentMedal.setCurrentCommentCount(count);
            commentMedal.setCompleted(count >= COMMENT_TARGET);
        } else {
            commentMedal.setCurrentCommentCount(0);
            commentMedal.setCompleted(false);
        }
        commentMedal.setSelected(selected == MedalType.COMMENT);
        medals.add(commentMedal);

        PostMedalDto postMedal = new PostMedalDto();
        postMedal.setIcon("https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/achi_post.png");
        postMedal.setTitle("发帖达人");
        postMedal.setDescription("发帖超过100条");
        postMedal.setType(MedalType.POST);
        postMedal.setTargetPostCount(POST_TARGET);
        if (user != null) {
            long count = postRepository.countByAuthor_Id(userId);
            postMedal.setCurrentPostCount(count);
            postMedal.setCompleted(count >= POST_TARGET);
        } else {
            postMedal.setCurrentPostCount(0);
            postMedal.setCompleted(false);
        }
        postMedal.setSelected(selected == MedalType.POST);
        medals.add(postMedal);

        SeedUserMedalDto seedUserMedal = new SeedUserMedalDto();
        seedUserMedal.setIcon("https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/achi_seed.png");
        seedUserMedal.setTitle("种子用户");
        seedUserMedal.setDescription("2025.9.16前注册的用户");
        seedUserMedal.setType(MedalType.SEED);
        if (user != null) {
            seedUserMedal.setRegisterDate(user.getCreatedAt());
            seedUserMedal.setCompleted(user.getCreatedAt().isBefore(SEED_USER_DEADLINE));
        } else {
            seedUserMedal.setCompleted(false);
        }
        seedUserMedal.setSelected(selected == MedalType.SEED);
        medals.add(seedUserMedal);
        if (user != null && selected == null) {
            for (MedalDto medal : medals) {
                if (medal.isCompleted()) {
                    medal.setSelected(true);
                    user.setDisplayMedal(medal.getType());
                    userRepository.save(user);
                    break;
                }
            }
        }

        return medals;
    }

    public void ensureDisplayMedal(User user) {
        if (user == null || user.getDisplayMedal() != null) {
            return;
        }
        if (commentRepository.countByAuthor_Id(user.getId()) >= COMMENT_TARGET) {
            user.setDisplayMedal(MedalType.COMMENT);
        } else if (postRepository.countByAuthor_Id(user.getId()) >= POST_TARGET) {
            user.setDisplayMedal(MedalType.POST);
        } else if (user.getCreatedAt().isBefore(SEED_USER_DEADLINE)) {
            user.setDisplayMedal(MedalType.SEED);
        }
        if (user.getDisplayMedal() != null) {
            userRepository.save(user);
        }
    }

    public void selectMedal(String username, MedalType type) {
        User user = userRepository.findByUsername(username).orElseThrow();
        boolean completed = getMedals(user.getId()).stream()
                .filter(m -> m.getType() == type)
                .findFirst()
                .map(MedalDto::isCompleted)
                .orElse(false);
        if (!completed) {
            throw new IllegalArgumentException("Medal not completed");
        }
        user.setDisplayMedal(type);
        userRepository.save(user);
    }
}
