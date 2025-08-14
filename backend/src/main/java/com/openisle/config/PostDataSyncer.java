package com.openisle.config;

import com.openisle.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PostDataSyncer  implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final CommentService commentService;
    @Override
    public void run(String... args) {

        // 我们需要处理下历史数据，一些帖子的评论数量和最后回复时间可能为空
        // 系统下次升级后，可以删掉这些代码
        List<Long> oldPosts = jdbcTemplate.query("""
                        select distinct p.id from posts p join comments c on p.id = c.post_id
                        where last_reply_at is null
                      """,
                 (rs, rowNum) -> rs.getLong(1));

        log.info("found {} old posts: {}", oldPosts.size(), oldPosts);
        oldPosts.forEach(postId -> {
            long cnt = commentService.countComments(postId);
            LocalDateTime lastCommentTime = commentService.getLastCommentTime(postId);
            jdbcTemplate.update("update posts set comment_count = ?, last_reply_at = ? where id = ?", cnt, lastCommentTime, postId);
            log.info("update post {} success", postId);
        });
    }
}
