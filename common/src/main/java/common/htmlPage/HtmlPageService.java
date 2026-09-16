package common.htmlPage;


public class HtmlPageService {

    // Shared CSS injected into every page
    private static final String BASE_STYLES = """
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Arial, sans-serif;
                background-color: #000000;
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 24px;
            }
            .card {
                background-color: #0a0a0a;
                border: 1px solid #1f1f1f;
                max-width: 480px;
                width: 100%%;
            }
            .card-header {
                padding: 28px 32px;
                border-bottom: 1px solid #1f1f1f;
            }
            .brand {
                color: #ffffff;
                font-size: 12px;
                font-weight: 600;
                letter-spacing: 0.12em;
                text-transform: uppercase;
            }
            .card-body {
                padding: 48px 32px;
                text-align: center;
            }
            .icon {
                width: 48px;
                height: 48px;
                margin: 0 auto 24px;
            }
            .icon svg {
                width: 48px;
                height: 48px;
                stroke: #71717a;
                stroke-width: 1.5;
                fill: none;
            }
            h2 {
                color: #ffffff;
                font-size: 20px;
                font-weight: 600;
                margin-bottom: 12px;
            }
            p {
                color: #71717a;
                font-size: 14px;
                line-height: 1.6;
                max-width: 360px;
                margin: 0 auto;
            }
            .btn {
                display: inline-block;
                margin-top: 32px;
                background-color: #ffffff;
                color: #000000;
                padding: 11px 28px;
                font-size: 13px;
                font-weight: 600;
                text-decoration: none;
                border: none;
                cursor: pointer;
                font-family: inherit;
            }
            .btn:hover { background-color: #e4e4e7; }
            .card-footer {
                padding: 20px 32px;
                border-top: 1px solid #1f1f1f;
            }
            .card-footer p {
                color: #27272a;
                font-size: 11px;
                max-width: none;
            }
            """;

    public String getVerificationLandingPage(String token) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verify Email — Stream</title>
                <style>
                    %s
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="card-header">
                        <p class="brand">Stream</p>
                    </div>
                    <div class="card-body">
                        <h2>Verify your email</h2>
                        <p>Click the button below to confirm your email address and activate your account.</p>
                        <form action="/api/v1/user/verify" method="POST">
                            <input type="hidden" name="verificationToken" value="%s">
                            <button type="submit" class="btn">Verify email</button>
                        </form>
                    </div>
                    <div class="card-footer">
                        <p>© 2026 Stream. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(BASE_STYLES, token);
    }

    public String getVerificationSuccessPage() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email Verified — Stream</title>
                <style>
                    %s
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="card-header">
                        <p class="brand">Stream</p>
                    </div>
                    <div class="card-body">
                        <div class="icon">
                            <svg viewBox="0 0 24 24">
                                <polyline points="20 6 9 17 4 12"></polyline>
                            </svg>
                        </div>
                        <h2>Email verified</h2>
                        <p>Your email has been confirmed. You can now close this page and sign in.</p>
                    </div>
                    <div class="card-footer">
                        <p>© 2026 Stream. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(BASE_STYLES);
    }

    public String getVerificationErrorPage(String errorMessage) {
        String displayMessage = "This link is invalid or has expired. Please request a new verification email from the app.";
        if (errorMessage != null && errorMessage.contains("already verified")) {
            displayMessage = "This email has already been verified. You can close this page and sign in.";
        }

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verification Failed — Stream</title>
                <style>
                    %s
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="card-header">
                        <p class="brand">Stream</p>
                    </div>
                    <div class="card-body">
                        <div class="icon">
                            <svg viewBox="0 0 24 24">
                                <line x1="18" y1="6" x2="6" y2="18"></line>
                                <line x1="6" y1="6" x2="18" y2="18"></line>
                            </svg>
                        </div>
                        <h2>Verification failed</h2>
                        <p>%s</p>
                    </div>
                    <div class="card-footer">
                        <p>© 2026 Stream. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(BASE_STYLES, displayMessage);
    }

    public String getPasswordResetSuccessPage() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset — Stream</title>
                <style>
                    %s
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="card-header">
                        <p class="brand">Stream</p>
                    </div>
                    <div class="card-body">
                        <div class="icon">
                            <svg viewBox="0 0 24 24">
                                <polyline points="20 6 9 17 4 12"></polyline>
                            </svg>
                        </div>
                        <h2>Password updated</h2>
                        <p>Your password has been reset. You can now close this page and sign in with your new password.</p>
                    </div>
                    <div class="card-footer">
                        <p>© 2026 Stream. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(BASE_STYLES);
    }

    public String getPasswordResetErrorPage(String errorMessage) {
        String displayMessage = "This link is invalid or has expired. Please request a new password reset from the app.";

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reset Failed — Stream</title>
                <style>
                    %s
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="card-header">
                        <p class="brand">Stream</p>
                    </div>
                    <div class="card-body">
                        <div class="icon">
                            <svg viewBox="0 0 24 24">
                                <line x1="18" y1="6" x2="6" y2="18"></line>
                                <line x1="6" y1="6" x2="18" y2="18"></line>
                            </svg>
                        </div>
                        <h2>Reset failed</h2>
                        <p>%s</p>
                    </div>
                    <div class="card-footer">
                        <p>© 2026 Stream. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(BASE_STYLES, displayMessage);
    }

    public String sendAddToCartEmail(String email, String productName) {
        return "";
    }
}
