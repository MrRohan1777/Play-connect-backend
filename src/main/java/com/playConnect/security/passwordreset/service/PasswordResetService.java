package com.playConnect.security.passwordreset.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.playConnect.security.passwordreset.entity.PasswordResetToken;
import com.playConnect.security.passwordreset.repository.PasswordResetTokenRepository;
import com.playConnect.user.entity.User;
import com.playConnect.user.repository.UserRepository;

@Service
public class PasswordResetService {

  private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

  private final UserRepository userRepository;
  private final PasswordResetTokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JavaMailSender mailSender;

  @Value("${app.password-reset.frontend-url:http://localhost:5173/reset-password}")
  private String frontendResetUrl;

  @Value("${app.password-reset.token-ttl-minutes:30}")
  private long tokenTtlMinutes;

  @Value("${app.mail.from:no-reply@playconnect.local}")
  private String fromEmail;

  private final SecureRandom secureRandom = new SecureRandom();

  public PasswordResetService(
      UserRepository userRepository,
      PasswordResetTokenRepository tokenRepository,
      PasswordEncoder passwordEncoder,
      JavaMailSender mailSender
  ) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.mailSender = mailSender;
  }

  @Transactional
  public void sendResetLink(String email) {
    Optional<User> userOpt = userRepository.findByEmail(email);

    // Always return success from API to avoid user enumeration.
    if (userOpt.isEmpty()) {
      return;
    }

    User user = userOpt.get();

    String token = generateToken();
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(tokenTtlMinutes);

    PasswordResetToken resetToken = new PasswordResetToken();
    resetToken.setToken(token);
    resetToken.setUser(user);
    resetToken.setExpiresAt(expiresAt);
    resetToken.setUsed(false);

    tokenRepository.save(resetToken);

    String link = frontendResetUrl + "?token=" + urlEncode(token);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(user.getEmail());
    message.setFrom(StringUtils.hasText(fromEmail) ? fromEmail : "no-reply@playconnect.local");
    message.setSubject("PlayConnect Password Reset");
    message.setText(
        "We received a request to reset your password.\n\n"
            + "Reset link (valid for " + tokenTtlMinutes + " minutes):\n"
            + link + "\n\n"
            + "If you didn't request this, you can ignore this email."
    );

    try {
      mailSender.send(message);
    } catch (MailException ex) {
      // Do not fail the endpoint: keep behavior consistent and avoid leaking details to clients.
      log.warn("Failed to send password reset email for {}: {}", email, ex.getMessage());
    }
  }

  @Transactional
  public void resetPassword(String token, String newPassword) {
    PasswordResetToken resetToken = tokenRepository.findByToken(token)
        .orElseThrow(() -> new RuntimeException("Invalid reset token"));

    if (resetToken.isUsed()) {
      throw new RuntimeException("Reset token already used");
    }

    if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new RuntimeException("Reset token expired");
    }

    User user = resetToken.getUser();
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    resetToken.setUsed(true);
    tokenRepository.save(resetToken);
  }

  private String generateToken() {
    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private String urlEncode(String value) {
    // Simple, safe encoding for URL query (Base64Url is already URL-safe; this is extra defensive).
    return new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
  }
}

