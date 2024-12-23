<!DOCTYPE html>
<html lang = "en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Password Reset Email</title>
    <style>
        body {
margin: 0;
padding: 0;
background-color: #1A1B1E;
font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen-Sans, Ubuntu, Cantarell, "Helvetica Neue", sans-serif;
}

.main {
background-color: #1A1B1E;
}

.container {
margin: 0 auto;
padding: 40px 20px;
max-width: 560px;
}

.logo-section {
margin-bottom: 24px;
text-align: center;
}

.logo-image {
display: block;
margin: 0 auto;
width: 64px;
height: 64px;
}

.logo-text {
color: #fff;
font-size: 24px;
font-weight: bold;
text-align: center;
margin: 16px 0 0;
}

.title {
color: #fff;
font-size: 24px;
font-weight: bold;
text-align: center;
margin: 30px 0;
padding: 0;
}

.text {
color: #AAAAAA;
font-size: 14px;
line-height: 24px;
text-align: left;
}

.button-container {
text-align: center;
margin: 30px 0;
}

.button {
background-color: #6366F1;
border-radius: 6px;
color: #fff;
font-size: 14px;
text-decoration: none;
text-align: center;
display: inline-block;
padding: 14px 32px;
border: none;
}

.hr {
border: 1px solid #2E2E2E;
margin: 30px 0;
}

.footer {
color: #666666;
font-size: 12px;
line-height: 24px;
text-align: left;
}
</style>
</head>
<body>
<div class = "main">
    <div class="container">
        <div class="logo-section">
            <img
                    src="cid:logo"
                    alt="Crypto Planet Logo"
                    class="logo-image"
            >
        </div>

        <p class="logo-text">Crypto Planet</p>

        <h1 class="title">Reset Your Password</h1>

        <p class="text">
            We received a request to reset the password for your account (user@example.com).
            Click the button below to reset it.
        </p>

        <div class="button-container">
            <a href="${resetUrl}" class="button">Reset Password</a>
        </div>

        <p class="text">
            If you didn't request this, you can safely ignore this email.
            The link will expire in 24 hours.
        </p>

        <hr class="hr">

        <p class="footer">
            This email was sent by Crypto Planet. Please do not reply to this email.
        </p>
    </div>
</div>
</body>
</html>
