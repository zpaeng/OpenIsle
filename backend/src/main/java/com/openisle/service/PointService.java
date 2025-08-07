package com.openisle.service;

import com.openisle.model.PointLog;
import com.openisle.model.User;
import com.openisle.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointLogRepository pointLogRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public int awardForPost(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow();
        PointLog log = getTodayLog(user);
        if (log.getPostCount() > 1) return 0;
        log.setPostCount(log.getPostCount() + 1);
        pointLogRepository.save(log);
        return addPoint(user, 30);
    }

    private PointLog getTodayLog(User user) {
        LocalDate today = LocalDate.now();
        return pointLogRepository.findByUserAndLogDate(user, today)
                .orElseGet(() -> {
                    PointLog log = new PointLog();
                    log.setUser(user);
                    log.setLogDate(today);
                    log.setPostCount(0);
                    log.setCommentCount(0);
                    log.setReactionCount(0);
                    return pointLogRepository.save(log);
                });
    }

    private int addPoint(User user, int amount) {
        user.setPoint(user.getPoint() + amount);
        userRepository.save(user);
        return amount;
    }

    // 同时为评论者和发帖人增加积分，返回值为评论者增加的积分数
    // 注意需要考虑发帖和回复是同一人的场景
    public int awardForComment(String commenterName, Long postId) {
        // 标记评论者是否已达到积分奖励上限
        boolean isTheRewardCapped = false;

        // 根据帖子id找到发帖人
        User poster = postRepository.findById(postId).orElseThrow().getAuthor();

        // 获取评论者的加分日志
        User commenter = userRepository.findByUsername(commenterName).orElseThrow();
        PointLog log = getTodayLog(commenter);
        if (log.getCommentCount() > 3) {
            isTheRewardCapped = true;
        }

        // 如果发帖人与评论者是同一个，则只计算发帖加分
        if (poster.getId().equals(commenter.getId())) {
            if (isTheRewardCapped) {
                return 0;
            } else {
                log.setCommentCount(log.getCommentCount() + 1);
                pointLogRepository.save(log);
                return addPoint(commenter, 10);
            }
        }

        // 如果不是同一个，则为发帖人和评论者同时加分
        addPoint(poster, 10);
        return addPoint(commenter, 10);
    }

    // 考虑点赞者和发帖人是同一个的情况
    public int awardForReactionOfPost(String reactionerName, Long postId) {
        // 根据帖子id找到发帖人
        User poster = postRepository.findById(postId).orElseThrow().getAuthor();

        // 获取点赞者信息
        User reactioner = userRepository.findByUsername(reactionerName).orElseThrow();

        // 如果发帖人与点赞者是同一个，则不加分
        if (poster.getId().equals(reactioner.getId())) {
            return 0;
        }

        // 如果不是同一个，则为发帖人加分
        return addPoint(poster, 10);
    }

    // 考虑点赞者和评论者是同一个的情况
    public int awardForReactionOfComment(String reactionerName, Long commentId) {
        // 根据帖子id找到评论者
        User commenter = commentRepository.findById(commentId).orElseThrow().getAuthor();

        // 获取点赞者信息
        User reactioner = userRepository.findByUsername(reactionerName).orElseThrow();

        // 如果评论者与点赞者是同一个，则不加分
        if (commenter.getId().equals(reactioner.getId())) {
            return 0;
        }

        // 如果不是同一个，则为发帖人加分
        return addPoint(commenter, 10);
    }

}
