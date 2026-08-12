package dev.videostreaming.microservice.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void sendVerificationEmail(String email, String verificationUri) {
        String subject = "Verify your email";
        String message = "Please click the following link to verify your email: " + verificationUri;
        sendEmail(email, verificationUri, subject, message);
    }

    private CompletableFuture<Void> sendEmail(String email, String actionUrl, String subject, String message) {
        try {
            String content = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif; background-color: #F7F9FC; width: 100%% !important; -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="background-color: #F7F9FC; padding: 40px 20px;">
                        <tr>
                            <td align="center" valign="top">
                                <!-- Main Container -->
                                <table role="presentation" width="600" cellspacing="0" cellpadding="0" border="0" style="background-color: #FFFFFF; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(58, 111, 248, 0.1); max-width: 600px; width: 100%%;">

                                    <!-- Header with Gradient -->
                                    <tr>
                                        <td align="center" style="background: linear-gradient(135deg, #3A6FF8 0%%, #6FD0C5 100%%); padding: 48px 30px;">
                                            <h1 style="margin: 0; color: #FFFFFF; font-size: 36px; font-weight: 700; letter-spacing: -1px; text-align: center;">Majestor</h1>
                                            <p style="margin: 10px 0 0 0; color: rgba(255, 255, 255, 0.95); font-size: 15px; font-weight: 400; text-align: center;">Your Academic Companion</p>
                                        </td>
                                    </tr>

                                    <!-- Main Content -->
                                    <tr>
                                        <td align="center" style="padding: 56px 40px;">
                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tr>
                                                    <td align="center">
                                                        <h2 style="margin: 0 0 20px 0; color: #121826; font-size: 28px; font-weight: 600; text-align: center; line-height: 1.3;">%s</h2>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="center" style="padding-bottom: 40px;">
                                                        <p style="margin: 0; color: #5A6275; font-size: 16px; line-height: 1.6; text-align: center; max-width: 480px;">%s</p>
                                                    </td>
                                                </tr>

                                                <!-- CTA Button -->
                                                <tr>
                                                    <td align="center" style="padding-bottom: 40px;">
                                                        <table role="presentation" cellspacing="0" cellpadding="0" border="0" style="margin: 0 auto;">
                                                            <tr>
                                                                <td align="center" style="background: linear-gradient(135deg, #3A6FF8 0%%, #5B8DFA 100%%); border-radius: 12px; box-shadow: 0 4px 12px rgba(58, 111, 248, 0.3);">
                                                                    <a href="%s" style="display: inline-block; padding: 18px 52px; font-size: 16px; font-weight: 600; color: #FFFFFF; text-decoration: none; border-radius: 12px; text-align: center;">
                                                                        Verify Your Email
                                                                    </a>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>

                                                <!-- Divider -->
                                                <tr>
                                                    <td align="center" style="padding: 0 0 40px 0;">
                                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                            <tr>
                                                                <td style="height: 1px; background-color: #E6ECF5;"></td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>

                                                <!-- Alternative Link -->
                                                <tr>
                                                    <td align="center" style="padding-bottom: 12px;">
                                                        <p style="margin: 0; color: #9E9E9E; font-size: 13px; text-align: center;">Or copy and paste this link into your browser:</p>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="center">
                                                        <p style="margin: 0; padding: 0 20px; color: #3A6FF8; font-size: 13px; word-break: break-all; text-align: center;">
                                                            <a href="%s" style="color: #3A6FF8; text-decoration: none;">%s</a>
                                                        </p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td align="center" style="background-color: #F7F9FC; padding: 32px 40px; border-top: 1px solid #E6ECF5;">
                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tr>
                                                    <td align="center">
                                                        <p style="margin: 0 0 12px 0; color: #5A6275; font-size: 14px; font-weight: 500; text-align: center;">Need help? We're here for you!</p>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="center">
                                                        <p style="margin: 0; color: #9E9E9E; font-size: 12px; line-height: 1.5; text-align: center;">This is an automated message from Majestor.<br>Please do not reply directly to this email.</p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Brand Footer -->
                                    <tr>
                                        <td align="center" style="background-color: #121826; padding: 24px 40px;">
                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tr>
                                                    <td align="center">
                                                        <p style="margin: 0 0 6px 0; color: #FFFFFF; font-size: 16px; font-weight: 700; text-align: center;">Majestor</p>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="center">
                                                        <p style="margin: 0; color: rgba(255, 255, 255, 0.6); font-size: 12px; text-align: center;">© 2026 Majestor. All rights reserved.</p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>

                                <!-- Bottom Spacing -->
                                <table role="presentation" width="600" cellspacing="0" cellpadding="0" border="0" style="max-width: 600px; width: 100%%;">
                                    <tr>
                                        <td align="center" style="padding: 24px 20px;">
                                            <p style="margin: 0; color: #9E9E9E; font-size: 11px; text-align: center; line-height: 1.5;">
                                                You received this email because you signed up for Majestor.<br>
                                                If you didn't request this, please ignore this email.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(subject, message, actionUrl, actionUrl, actionUrl);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(content, true);

            mailSender.send(mimeMessage);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

}