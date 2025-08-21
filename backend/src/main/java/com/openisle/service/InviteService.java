package com.openisle.service;

import com.openisle.model.InviteToken;
import com.openisle.model.User;
import com.openisle.repository.InviteTokenRepository;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InviteService {
    private final InviteTokenRepository inviteTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PointService pointService;

    @Value
    public class InviteValidateResult {
        InviteToken inviteToken;
        boolean validate;
    }

    public String generate(String username) {
        User inviter = userRepository.findByUsername(username).orElseThrow();
        LocalDate today = LocalDate.now();
        Optional<InviteToken> existing = inviteTokenRepository.findByInviterAndCreatedDate(inviter, today);
        if (existing.isPresent()) {
            return existing.get().getToken();
        }
        String token = jwtService.generateInviteToken(username);
        InviteToken inviteToken = new InviteToken();
        inviteToken.setToken(token);
        inviteToken.setInviter(inviter);
        inviteToken.setCreatedDate(today);
        inviteToken.setUsageCount(0);
        inviteTokenRepository.save(inviteToken);
        return token;
    }

    public InviteValidateResult validate(String token) {
        if (token == null || token.isEmpty()) {
            return new InviteValidateResult(null, false);
        }
        try {
            jwtService.validateAndGetSubjectForInvite(token);
        } catch (Exception e) {
            return new InviteValidateResult(null, false);
        }
        InviteToken invite = inviteTokenRepository.findById(token).orElse(null);
        return new InviteValidateResult(invite, invite != null && invite.getUsageCount() < 3);
    }

    public void consume(String token, String newUserName) {
        InviteToken invite = inviteTokenRepository.findById(token).orElseThrow();
        invite.setUsageCount(invite.getUsageCount() + 1);
        inviteTokenRepository.save(invite);
        pointService.awardForInvite(invite.getInviter().getUsername(), newUserName);
    }
}
