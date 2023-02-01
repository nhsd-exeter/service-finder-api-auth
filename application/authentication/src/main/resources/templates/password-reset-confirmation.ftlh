<!DOCTYPE html>
<html>
    <head>
        <#include "style.ftlh">
        <title>${title}</title>
    </head>
    <body>
        <div class="nhs-logo-container">
            <img class="nhs-logo" src="cid:nhs-logo" alt="NHS logo">
        </div>
        <h1>${title}</h1>
        <p>The password on your account was reset at ${time} [time] on ${date} [date].</p>
        <p>If you are concerned about this, please email <a href="mailto:${helpDesk}">${helpDesk}</a> or telephone 0300 303 4034.</p>
        <p>Help desk support is available Monday-Friday, 8am-5pm, excluding bank holidays.<p>
        <#include "footer.ftlh">
    </body>
</html>
