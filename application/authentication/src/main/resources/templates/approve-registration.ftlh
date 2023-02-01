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
                    <p style="font-family:Arial, sans-serif; font-size:16px; line-height:22px; color:#231f20; margin:0 0 20px;">We've checked your completed account information and have approved your NHS Service Finder account.</p>
                    <p style="font-family:Arial, sans-serif; font-size:16px; line-height:22px; color:#231f20; margin:0 0 20px;">You can now <a href="${homeUrl}">sign in and start using NHS Service Finder.</a></p>
                    <p style="font-family:Arial, sans-serif; font-size:16px; line-height:22px; color:#231f20; margin:0 0 20px;"><strong>Before you start</strong> <br/>As a new user, you should <a  href="${helpUrl}">find out how to use NHS Service Finder</a> to make sure you get the best results from your searches.</p>
                    <p style="font-family:Arial, sans-serif; font-size:16px; line-height:22px; color:#231f20; margin:0 0 20px;">Regards, <br/>The NHS Service Finder team</p>
                    <p style="font-family:Arial, sans-serif; font-size:12px; line-height:18px; color:#231f20; margin:0 0 20px;">If you did not request this email, ignore it. If you have any questions or problems with your account, you can contact <a href="mailto:exeter.helpdesk@nhs.net">exeter.helpdesk@nhs.net</a> or telephone <strong>0300 303 4034</strong>. Our helpdesk support is open from Monday to Friday, 8am to 5pm, and is closed on bank holidays.</p>
                </td>
            </tr>
        </table>
        <#include "foot.ftlh">
    </body>
</html>
