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
        String message = "Click the button below to verify your email address.";
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
                <body style="margin: 0; padding: 0; background-color: #000000; font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Arial, sans-serif; width: 100%% !important; -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="background-color: #000000; padding: 48px 20px;">
                        <tr>
                            <td align="center" valign="top">
                                <table role="presentation" width="560" cellspacing="0" cellpadding="0" border="0" style="background-color: #0a0a0a; border: 1px solid #1f1f1f; max-width: 560px; width: 100%%;">

                                    <!-- Header -->
                                    <tr>
                                        <td style="padding: 36px 40px 28px 40px; border-bottom: 1px solid #1f1f1f;">
                                            <p style="margin: 0; color: #ffffff; font-size: 13px; font-weight: 600; letter-spacing: 0.12em; text-transform: uppercase;">Stream</p>
                                        </td>
                                    </tr>

                                    <!-- Content -->
                                    <tr>
                                        <td style="padding: 48px 40px;">
                                            <h1 style="margin: 0 0 16px 0; color: #ffffff; font-size: 22px; font-weight: 600; line-height: 1.3;">%s</h1>
                                            <p style="margin: 0 0 36px 0; color: #71717a; font-size: 14px; line-height: 1.6;">%s</p>

                                            <!-- CTA Button -->
                                            <table role="presentation" cellspacing="0" cellpadding="0" border="0">
                                                <tr>
                                                    <td style="background-color: #ffffff;">
                                                        <a href="%s" style="display: inline-block; padding: 12px 28px; font-size: 13px; font-weight: 600; color: #000000; text-decoration: none;">
                                                            Verify email
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>

                                            <!-- Fallback link -->
                                            <p style="margin: 32px 0 0 0; color: #3f3f46; font-size: 12px;">
                                                Or paste this link in your browser:<br>
                                                <a href="%s" style="color: #71717a; text-decoration: none; word-break: break-all;">%s</a>
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="padding: 24px 40px; border-top: 1px solid #1f1f1f;">
                                            <p style="margin: 0; color: #3f3f46; font-size: 11px; line-height: 1.6;">
                                                You received this email because you signed up for Stream.<br>
                                                If you didn't, you can safely ignore this message.
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Brand footer -->
                                    <tr>
                                        <td style="padding: 20px 40px; border-top: 1px solid #1f1f1f;">
                                            <p style="margin: 0; color: #27272a; font-size: 11px;">© 2026 Stream. All rights reserved.</p>
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