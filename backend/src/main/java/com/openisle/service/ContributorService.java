package com.openisle.service;

import com.openisle.model.ContributorConfig;
import com.openisle.repository.ContributorConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContributorService {
    private static final String OWNER = "OpenIsle";
    private static final String REPO = "OpenIsle";

    private final ContributorConfigRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    @Scheduled(cron = "0 0 * * * *")
    public void updateContributions() {
        for (ContributorConfig config : repository.findAll()) {
            long lines = fetchContributionLines(config.getGithubId());
            config.setContributionLines(lines);
            repository.save(config);
        }
    }

    private long fetchContributionLines(String githubId) {
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/stats/contributors", OWNER, REPO);
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            List<Map<String, Object>> body = response.getBody();
            if (body == null) {
                return 0;
            }
            for (Map<String, Object> item : body) {
                Map<String, Object> author = (Map<String, Object>) item.get("author");
                if (author != null && githubId.equals(author.get("login"))) {
                    List<Map<String, Object>> weeks = (List<Map<String, Object>>) item.get("weeks");
                    long total = 0;
                    if (weeks != null) {
                        for (Map<String, Object> week : weeks) {
                            Number a = (Number) week.get("a");
                            Number d = (Number) week.get("d");
                            if (a != null) {
                                total += a.longValue();
                            }
                            if (d != null) {
                                total += d.longValue();
                            }
                        }
                    }
                    return total;
                }
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    public long getContributionLines(String userIname) {
        return repository.findByUserIname(userIname)
                .map(ContributorConfig::getContributionLines)
                .orElse(0L);
    }
}

