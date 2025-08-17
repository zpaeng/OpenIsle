package com.openisle.controller;

import com.openisle.dto.*;
import com.openisle.exception.FieldException;
import com.openisle.model.RegisterMode;
import com.openisle.model.User;
import com.openisle.repository.UserRepository;
import com.openisle.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailSender emailService;
    private final CaptchaService captchaService;
    private final GoogleAuthService googleAuthService;
    private final GithubAuthService githubAuthService;
    private final DiscordAuthService discordAuthService;
    private final TwitterAuthService twitterAuthService;
    private final RegisterModeService registerModeService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final InviteService inviteService;


    @Value("${app.captcha.enabled:false}")
    private boolean captchaEnabled;

    @Value("${app.captcha.register-enabled:false}")
    private boolean registerCaptchaEnabled;

    @Value("${app.captcha.login-enabled:false}")
    private boolean loginCaptchaEnabled;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (captchaEnabled && registerCaptchaEnabled && !captchaService.verify(req.getCaptcha())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid captcha"));
        }
        if (req.getInviteToken() != null && !req.getInviteToken().isEmpty()) {
            if (!inviteService.validate(req.getInviteToken())) {
                return ResponseEntity.badRequest().body(Map.of("error", "邀请码使用次数过多"));
            }
            try {
                User user = userService.registerWithInvite(
                        req.getUsername(), req.getEmail(), req.getPassword());
                inviteService.consume(req.getInviteToken());
                emailService.sendEmail(user.getEmail(), "在网站填写验证码以验证", "您的验证码是 " + user.getVerificationCode());
                return ResponseEntity.ok(Map.of(
                        "token", jwtService.generateToken(user.getUsername()),
                        "reason_code", "INVITE_APPROVED"
                ));
            } catch (FieldException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "field", e.getField(),
                        "error", e.getMessage()
                ));
            }
        }
        User user = userService.register(
                req.getUsername(), req.getEmail(), req.getPassword(), "", registerModeService.getRegisterMode());
        emailService.sendEmail(user.getEmail(), "在网站填写验证码以验证", "您的验证码是 " + user.getVerificationCode());
        if (!user.isApproved()) {
            notificationService.createRegisterRequestNotifications(user, user.getRegisterReason());
        }
        return ResponseEntity.ok(Map.of("message", "Verification code sent"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest req) {
        boolean ok = userService.verifyCode(req.getUsername(), req.getCode());
        if (ok) {
            Optional<User> userOpt = userService.findByUsername(req.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
            }

            User user = userOpt.get();

            if (user.isApproved()) {
                return ResponseEntity.ok(Map.of(
                        "message", "Verified and isApproved",
                        "reason_code", "VERIFIED_AND_APPROVED",
                        "token", jwtService.generateToken(req.getUsername())
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "message", "Verified",
                        "reason_code", "VERIFIED",
                        "token", jwtService.generateReasonToken(req.getUsername())
                ));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid verification code"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (captchaEnabled && loginCaptchaEnabled && !captchaService.verify(req.getCaptcha())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid captcha"));
        }
        Optional<User> userOpt = userService.findByUsername(req.getUsername());
        if (userOpt.isEmpty()) {
            userOpt = userService.findByEmail(req.getUsername());
        }
        if (userOpt.isEmpty() || !userService.matchesPassword(userOpt.get(), req.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid credentials",
                    "reason_code", "INVALID_CREDENTIALS"));
        }
        User user = userOpt.get();
        if (!user.isVerified()) {
            user = userService.register(user.getUsername(), user.getEmail(), user.getPassword(), user.getRegisterReason(), registerModeService.getRegisterMode());
            emailService.sendEmail(user.getEmail(), "在网站填写验证码以验证", "您的验证码是 " + user.getVerificationCode());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "User not verified",
                    "reason_code", "NOT_VERIFIED",
                    "user_name", user.getUsername()));
        }
        if (RegisterMode.WHITELIST.equals(registerModeService.getRegisterMode()) && !user.isApproved()) {
            if (user.getRegisterReason() != null && !user.getRegisterReason().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Account awaiting approval",
                        "reason_code", "IS_APPROVING"
                ));
            }
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Register reason not approved",
                    "reason_code", "NOT_APPROVED",
                    "token", jwtService.generateReasonToken(user.getUsername())));
        }
        return ResponseEntity.ok(Map.of("token", jwtService.generateToken(user.getUsername())));
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest req) {
        boolean viaInvite = req.getInviteToken() != null && !req.getInviteToken().isEmpty();
        if (viaInvite && !inviteService.validate(req.getInviteToken())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid invite token"));
        }
        Optional<AuthResult> resultOpt = googleAuthService.authenticate(
                req.getIdToken(),
                registerModeService.getRegisterMode(),
                viaInvite);
        if (resultOpt.isPresent()) {
            AuthResult result = resultOpt.get();
            if (viaInvite && result.isNewUser()) {
                inviteService.consume(req.getInviteToken());
                return ResponseEntity.ok(Map.of(
                        "token", jwtService.generateToken(result.getUser().getUsername()),
                        "reason_code", "INVITE_APPROVED"
                ));
            }
            if (RegisterMode.DIRECT.equals(registerModeService.getRegisterMode())) {
                return ResponseEntity.ok(Map.of("token", jwtService.generateToken(result.getUser().getUsername())));
            }
            if (!result.getUser().isApproved()) {
                if (result.getUser().getRegisterReason() != null && !result.getUser().getRegisterReason().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Account awaiting approval",
                            "reason_code", "IS_APPROVING",
                            "token", jwtService.generateReasonToken(result.getUser().getUsername())
                    ));
                }
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Account awaiting approval",
                        "reason_code", "NOT_APPROVED",
                        "token", jwtService.generateReasonToken(result.getUser().getUsername())
                ));
            }

            return ResponseEntity.ok(Map.of("token", jwtService.generateToken(result.getUser().getUsername())));
        }
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid google token",
                "reason_code", "INVALID_CREDENTIALS"
        ));
    }


    @PostMapping("/reason")
    public ResponseEntity<?> reason(@RequestBody MakeReasonRequest req) {
        String username = jwtService.validateAndGetSubjectForReason(req.getToken());
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid token, Please re-login",
                    "reason_code", "INVALID_CREDENTIALS"
            ));
        }

        if (req.getReason() == null || req.getReason().trim().length() <= 20) {
             return ResponseEntity.badRequest().body(Map.of(
                    "error", "Reason's length must longer than 20",
                    "reason_code", "INVALID_CREDENTIALS"
            ));
        }

        User user = userOpt.get();
        if (user.isApproved() || registerModeService.getRegisterMode() == RegisterMode.DIRECT) {
            return ResponseEntity.ok().body(Map.of("valid", true));
        }

        user = userService.updateReason(user.getUsername(), req.getReason());
        notificationService.createRegisterRequestNotifications(user, req.getReason());
        return ResponseEntity.ok().body(Map.of("valid", true));
    }

    @PostMapping("/github")
    public ResponseEntity<?> loginWithGithub(@RequestBody GithubLoginRequest req) {
        boolean viaInvite = req.getInviteToken() != null && !req.getInviteToken().isEmpty();
        if (viaInvite && !inviteService.validate(req.getInviteToken())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid invite token"));
        }
        Optional<AuthResult> resultOpt = githubAuthService.authenticate(
                req.getCode(),
                registerModeService.getRegisterMode(),
                req.getRedirectUri(),
                viaInvite);
        if (resultOpt.isPresent()) {
            AuthResult result = resultOpt.get();
            if (viaInvite && result.isNewUser()) {
                inviteService.consume(req.getInviteToken());
                return ResponseEntity.ok(Map.of(
                        "token", jwtService.generateToken(result.getUser().getUsername()),
                        "reason_code", "INVITE_APPROVED"
                ));
            }
            if (RegisterMode.DIRECT.equals(registerModeService.getRegisterMode())) {
                return ResponseEntity.ok(Map.of("token", jwtService.generateToken(result.getUser().getUsername())));
            }
            if (!result.getUser().isApproved()) {
                if (result.getUser().getRegisterReason() != null && !result.getUser().getRegisterReason().isEmpty()) {
                    // 已填写注册理由
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Account awaiting approval",
                            "reason_code", "IS_APPROVING",
                            "token", jwtService.generateReasonToken(result.getUser().getUsername())
                    ));
                }
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Account awaiting approval",
                        "reason_code", "NOT_APPROVED",
                        "token", jwtService.generateReasonToken(result.getUser().getUsername())
                ));
            }

            return ResponseEntity.ok(Map.of("token", jwtService.generateToken(result.getUser().getUsername())));
        }
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid github code",
                "reason_code", "INVALID_CREDENTIALS"
        ));
    }

    @PostMapping("/discord")
    public ResponseEntity<?> loginWithDiscord(@RequestBody DiscordLoginRequest req) {
        boolean viaInvite = req.getInviteToken() != null && !req.getInviteToken().isEmpty();
        if (viaInvite && !inviteService.validate(req.getInviteToken())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid invite token"));
        }
        Optional<AuthResult> resultOpt = discordAuthService.authenticate(
                req.getCode(),
                registerModeService.getRegisterMode(),
                req.getRedirectUri(),
                viaInvite);
        if (resultOpt.isPresent()) {
            AuthResult result = resultOpt.get();
            if (viaInvite && result.isNewUser()) {
                inviteService.consume(req.getInviteToken());
                return ResponseEntity.ok(Map.of(
                        "token", jwtService.generateToken(result.getUser().getUsername()),
                        "reason_code", "INVITE_APPROVED"
                ));
            }
            if (RegisterMode.DIRECT.equals(registerModeService.getRegisterMode())) {
                return ResponseEntity.ok(Map.of("token", jwtService.generateToken(result.getUser().getUsername())));
            }
            if (!result.getUser().isApproved()) {
                if (result.getUser().getRegisterReason() != null && !result.getUser().getRegisterReason().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Account awaiting approval",
                            "reason_code", "IS_APPROVING",
                            "token", jwtService.generateReasonToken(result.getUser().getUsername())
                    ));
                }
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Account awaiting approval",
                        "reason_code", "NOT_APPROVED",
                        "token", jwtService.generateReasonToken(result.getUser().getUsername())
                ));
            }

            return ResponseEntity.ok(Map.of("token", jwtService.generateToken(result.getUser().getUsername())));
        }
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid discord code",
                "reason_code", "INVALID_CREDENTIALS"
        ));
    }
      
    @PostMapping("/twitter")
    public ResponseEntity<?> loginWithTwitter(@RequestBody TwitterLoginRequest req) {
        boolean viaInvite = req.getInviteToken() != null && !req.getInviteToken().isEmpty();
        if (viaInvite && !inviteService.validate(req.getInviteToken())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid invite token"));
        }
        Optional<AuthResult> resultOpt = twitterAuthService.authenticate(
                req.getCode(),
                req.getCodeVerifier(),
                registerModeService.getRegisterMode(),
                req.getRedirectUri(),
                viaInvite);
        if (resultOpt.isPresent()) {
            AuthResult result = resultOpt.get();
            if (viaInvite && result.isNewUser()) {
                inviteService.consume(req.getInviteToken());
                return ResponseEntity.ok(Map.of(
                        "token", jwtService.generateToken(result.getUser().getUsername()),
                        "reason_code", "INVITE_APPROVED"
                ));
            }
            if (RegisterMode.DIRECT.equals(registerModeService.getRegisterMode())) {
                return ResponseEntity.ok(Map.of("token", jwtService.generateToken(result.getUser().getUsername())));
            }
            if (!result.getUser().isApproved()) {
                if (result.getUser().getRegisterReason() != null && !result.getUser().getRegisterReason().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Account awaiting approval",
                            "reason_code", "IS_APPROVING",
                            "token", jwtService.generateReasonToken(result.getUser().getUsername())
                    ));
                }
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Account awaiting approval",
                        "reason_code", "NOT_APPROVED",
                        "token", jwtService.generateReasonToken(result.getUser().getUsername())
                ));
            }

            return ResponseEntity.ok(Map.of("token", jwtService.generateToken(result.getUser().getUsername())));
        }
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid twitter code",
                "reason_code", "INVALID_CREDENTIALS"
        ));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkToken() {
        return ResponseEntity.ok(Map.of("valid", true));
    }

    @PostMapping("/forgot/send")
    public ResponseEntity<?> sendReset(@RequestBody ForgotPasswordRequest req) {
        Optional<User> userOpt = userService.findByEmail(req.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        String code = userService.generatePasswordResetCode(req.getEmail());
        emailService.sendEmail(req.getEmail(), "请填写验证码以重置密码", "您的验证码是" + code);
        return ResponseEntity.ok(Map.of("message", "Verification code sent"));
    }

    @PostMapping("/forgot/verify")
    public ResponseEntity<?> verifyReset(@RequestBody VerifyForgotRequest req) {
        boolean ok = userService.verifyPasswordResetCode(req.getEmail(), req.getCode());
        if (ok) {
            String username = userService.findByEmail(req.getEmail()).get().getUsername();
            return ResponseEntity.ok(Map.of("token", jwtService.generateResetToken(username)));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid verification code"));
    }

    @PostMapping("/forgot/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        String username = jwtService.validateAndGetSubjectForReset(req.getToken());
        try {
            userService.updatePassword(username, req.getPassword());
            return ResponseEntity.ok(Map.of("message", "Password updated"));
        } catch (FieldException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "field", e.getField(),
                    "error", e.getMessage()
            ));
        }
    }

    // DTO classes moved to com.openisle.dto package
}
