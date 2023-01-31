exports.handler = (event, context, callback) => {

    function getEmailMessage(bodyText, uriPart, linkText1, linkText2) {
        const href = process.env.SF_URL + `/${uriPart}/${encodeURIComponent(event.request.userAttributes.email)}/${event.request.codeParameter}`;
        return `
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="utf-8">
                </head>
                <body style="margin:0; padding:0;">
                    <table cellspacing="0" cellpadding="20" width="100%">
                        <tr>
                            <td BGCOLOR="#005EB8" width="88" style="padding:20px 5px 20px 20px;">
                                <img src="https://servicefinder.nhs.uk/assets/images/logotype-nhs-colour@2x.png" alt="NHS" align="left" style="margin-right:10px;" width="88" />
                            </td>
                            <td BGCOLOR="#005EB8" style="padding:20px 0;">
                                <h1 style="font-family:Arial, sans-serif; font-size:28px; line-height:28px; margin:0; padding:0; padding-top:5px; color:white;">Service Finder</h1>
                            </td>
                        </tr>
                    </table>
                    <table cellspacing="0" cellpadding="0" width="100%">
                        <tr>
                            <td BGCOLOR="#DDD" HEIGHT="110" style="background-image:url(https://servicefinder.nhs.uk/assets/images/splash-banner-big2.png); background-size:auto 110px;">&nbsp;</td>
                        </tr>
                    </table>
                    <table cellspacing="0" cellpadding="20" width="100%">
                        <tr>
                            <td>
                                <p style="font-family:Arial, sans-serif; font-size:24px; line-height:36px; color:#231f20; margin:0; margin-top:10px; margin-bottom:20px;"><b>${bodyText}</b></p>
                                <p style="font-family:Arial, sans-serif; font-size:18px; line-height:24px; color:#231f20; margin:0; margin-bottom:20px;"><b>Please <a href="${href}" style="color:#005EB8">${linkText1}</a> ${linkText2}</b></p>
                                <p style="font-family:Arial, sans-serif; font-size:18px; line-height:24px; color:#231f20; margin:0; margin-bottom:20px;">If the link above does not work, please copy and paste the following URL into your browser’s address bar: <nowrap><i>${href}</i></nowrap></p>
                                <p style="font-family:Arial, sans-serif; font-size:14px; line-height:20px; color:#231f20; margin:0; margin-bottom:40px;">If you did not request this email then please ignore it. If you have any questions or encounter any problems with the registration process then please contact <a href="mailto:exeter.helpdesk@nhs.net" style="color:#005EB8">exeter.helpdesk@nhs.net</a> or telephone <b>0300 303 4034</b>. Helpdesk support is available Monday-Friday, 8am-5pm, excluding bank holidays.</p>
                            </td>
                        </tr>
                    </table>
                    <table cellspacing="0" cellpadding="2" width="100%" border="0">
                        <tr>
                            <td BGCOLOR="#005EB8"></td>
                        </tr>
                        <tr>
                            <td BGCOLOR="#E8EDEE" HEIGHT="80"></td>
                        </tr>
                    </table>
                </body>
            </html>
        `;
    }

    switch (event.triggerSource) {
        case "CustomMessage_SignUp":
            event.response.emailSubject = "NHS Service Finder - registration verification";
            event.response.emailMessage = `${getEmailMessage(
                "Thank you for registering to use NHS Service Finder",
                "register/confirmUser",
                "verify your email address",
                "to complete the registration process."
            )}`;
            break;
        case "CustomMessage_ResendCode":
            event.response.emailSubject = "NHS Service Finder - registration verification reminder";
            event.response.emailMessage = `${getEmailMessage(
                "You have requested a registration verification reminder",
                "register/confirmUser",
                "verify your email address",
                "to complete the registration process."
            )}`;
            break;
        case "CustomMessage_ForgotPassword":
            event.response.emailSubject = "NHS Service Finder - reset password";
            event.response.emailMessage = `${getEmailMessage(
                "Reset your password by clicking",
                "resetPassword",
                "use this link",
                "to choose a new password."
            )}`;
            break;
        default:
            // do nothing
            break;
    }
    callback(null, event);
};
