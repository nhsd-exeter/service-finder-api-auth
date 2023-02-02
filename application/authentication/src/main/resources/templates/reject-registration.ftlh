<!DOCTYPE html>
<html>
    <head>
        <title>${title}</title>
    </head>
    <body style="margin:0; padding:0;">
        <#include "banner.ftlh">
        <table cellspacing="0" cellpadding="20" width="100%">
            <tr>
                <td>
                    <p style="font-family:Arial, sans-serif; font-size:16px; line-height:22px; color:#231f20; margin:10px 0 20px 0;">Hello ${name}</p>
                    <p style="font-family:Arial, sans-serif; font-size:16px; line-height:22px; color:#231f20; margin:0 0 20px;">We've checked your completed account information and have rejected your NHS Service Finder account. This was because:</p>
                    <p style="font-family:Arial, sans-serif; font-size:16px; line-height:22px; color:#231f20; margin:0 0 20px;">${reason}</p>
                    <p style="font-family:Arial, sans-serif; font-size:16px; line-height:22px; color:#231f20; margin:0 0 20px;">If you think your account should have been approved, <a href="mailto:exeter.helpdesk@nhs.net">contact us</a> and let us know.</p>
                    <p style="font-family:Arial, sans-serif; font-size:16px; line-height:22px; color:#231f20; margin:0 0 20px;">Regards, <br/>The NHS Service Finder team</p>
                    <p style="font-family:Arial, sans-serif; font-size:12px; line-height:18px; color:#231f20; margin:0 0 20px;">If you did not request this email, ignore it. If you have any questions or problems with your account, you can contact <a href="mailto:exeter.helpdesk@nhs.net">exeter.helpdesk@nhs.net</a> or telephone <strong>0300 303 4034</strong>. Our helpdesk support is open from Monday to Friday, 8am to 5pm, and is closed on bank holidays.</p>
                </td>
            </tr>
        </table>
        <#include "foot.ftlh">
    </body>
</html>
