package com.openisle.controller;

import com.openisle.model.Post;
import com.openisle.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
public class RssController {
    private final PostService postService;

    @Value("${app.website-url:https://www.open-isle.com}")
    private String websiteUrl;

    private static final Pattern MD_IMAGE = Pattern.compile("!\\[[^\\]]*\\]\\(([^)]+)\\)");
    private static final Pattern HTML_IMAGE = Pattern.compile("<img[^>]+src=[\"']?([^\"'>]+)[\"']?[^>]*>");

    @GetMapping(value = "/api/rss", produces = "application/rss+xml;charset=UTF-8")
    public String feed() {
        List<Post> posts = postService.listLatestRssPosts(10);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<rss version=\"2.0\">");
        sb.append("<channel>");
        sb.append("<title>OpenIsle RSS</title>");
        sb.append("<link>").append(websiteUrl).append("</link>");
        sb.append("<description>Latest posts</description>");
        DateTimeFormatter fmt = DateTimeFormatter.RFC_1123_DATE_TIME;
        for (Post p : posts) {
            String link = websiteUrl + "/posts/" + p.getId();
            sb.append("<item>");
            sb.append("<title><![CDATA[").append(p.getTitle()).append("]]></title>");
            sb.append("<link>").append(link).append("</link>");
            sb.append("<guid isPermaLink=\"true\">").append(link).append("</guid>");
            sb.append("<pubDate>")
                    .append(p.getCreatedAt().atZone(ZoneId.systemDefault()).format(fmt))
                    .append("</pubDate>");
            String desc = p.getContent() + "\n<p>更多细节请访问原文：" + link + "</p>";
            sb.append("<description><![CDATA[").append(desc).append("]]></description>");
            String img = firstImage(p.getContent());
            if (img != null) {
                sb.append("<enclosure url=\"").append(img).append("\" type=\"")
                        .append(getMimeType(img)).append("\" />");
            }
            sb.append("</item>");
        }
        sb.append("</channel></rss>");
        return sb.toString();
    }

    private String firstImage(String content) {
        Matcher m = MD_IMAGE.matcher(content);
        if (m.find()) {
            return m.group(1);
        }
        m = HTML_IMAGE.matcher(content);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private String getMimeType(String url) {
        String lower = url.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        return "image/jpeg";
    }
}
