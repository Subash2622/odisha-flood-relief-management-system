package com.odisha.floodrelief.service;

import com.odisha.floodrelief.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationDispatchService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:noreply@odishafloodrelief.org}")
    private String mailFrom;

    @Value("${app.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${app.otp.dev-mode:false}")
    private boolean otpDevMode;

    public NotificationDispatchService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    /**
     * Sends real email OTP when mail is configured.
     * @return true if delivered (or accepted by SMTP), false if only logged in dev mode
     */
    public boolean sendEmailOtp(String toEmail, String otp) {
        String subject = "Password Reset OTP - Odisha Flood Relief";
        String body = "Your password reset OTP is: " + otp
                + "\n\nThis OTP is valid for 10 minutes."
                + "\nIf you did not request this, please ignore this email.";

        if (!mailEnabled) {
            if (otpDevMode) {
                log.info("[DEV EMAIL OTP] To: {} | OTP: {}", toEmail, otp);
                return false;
            }
            throw new BadRequestException("Email OTP is not configured. Set app.mail.enabled=true and Gmail SMTP settings.");
        }

        if (mailFrom != null && mailFrom.contains("CHANGE_ME")) {
            throw new BadRequestException(
                    "Gmail not configured yet. Set spring.mail.username and spring.mail.password (Gmail App Password) in application.properties, then restart.");
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BadRequestException("Mail sender is not available. Check spring.mail.* settings.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Password reset OTP email sent to {}", maskEmail(toEmail));
            return true;
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", maskEmail(toEmail), e);
            if (otpDevMode) {
                log.warn("[DEV FALLBACK EMAIL OTP] To: {} | OTP: {}", toEmail, otp);
                return false;
            }
            throw new BadRequestException(
                    "Could not send OTP email. Check Gmail App Password / SMTP settings. (" + e.getMessage() + ")");
        }
    }

    public boolean sendSmsOtp(String phone, String otp) {
        String text = "Odisha Flood Relief OTP: " + otp + ". Valid for 10 minutes.";

        if (!smsEnabled) {
            if (otpDevMode) {
                log.info("[DEV SMS OTP] To: {} | OTP: {} | Message: {}", phone, otp, text);
                return false;
            }
            throw new BadRequestException(
                    "SMS OTP needs a paid gateway (Twilio/MSG91/Fast2SMS). Use Email OTP for free, or enable SMS with API keys.");
        }

        // Paid provider hook (Twilio / MSG91 / Fast2SMS) — not free for production volume.
        log.warn("SMS enabled but no provider implemented yet. Phone={}", maskPhone(phone));
        if (otpDevMode) {
            log.info("[DEV SMS OTP] To: {} | OTP: {}", phone, otp);
            return false;
        }
        throw new BadRequestException("SMS provider is not implemented. Use Email channel for free OTP.");
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        int at = email.indexOf('@');
        return email.charAt(0) + "***" + email.substring(at);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "******" + phone.substring(phone.length() - 4);
    }
}
