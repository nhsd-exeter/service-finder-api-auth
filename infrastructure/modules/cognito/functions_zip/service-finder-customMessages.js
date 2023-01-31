exports.handler = (event, context, callback) => {

    function getEmailMessage(bodyText, uriPart) {
        const href = process.env.SF_URL + `/${uriPart}/${encodeURIComponent(event.request.userAttributes.email)}/${event.request.codeParameter}`;
        return `
            <!DOCTYPE html>
            <html>
                <head>
                    <style type="text/css">
                        body {
                            max-width: 750px !important;
                        }

                        .nhs-logo-container {
                            margin-top: 10px;
                            width: 100%;
                            height: 66px;
                            background-color: #325db9;
                        }

                        .nhs-logo {
                            width: 87px;
                            height: 36px;
                            padding: 15px;
                        }

                        .nhsuk-footer {
                            padding-bottom: 24px;
                            padding-top: 24px;
                            background-color: #d8dde0;
                            border-top: 4px solid #005eb8;
                        }

                        .nhsuk-footer:after {
                            clear: both;
                            content: '';
                            display: block;
                        }

                        @media print {
                            .nhsuk-footer {
                                display: none;
                            }
                        }

                        @media (min-width: 40.0625em) {
                            .nhsuk-footer {
                                padding-bottom: 32px;
                            }
                        }

                        .nhsuk-footer__list {
                            padding-bottom: 16px;
                            list-style-type: none;
                            margin: 0;
                            padding-left: 0;
                        }

                        @media (min-width: 40.0625em) {
                            .nhsuk-footer__list {
                                padding-bottom: 24px;
                            }
                        }

                        @media (min-width: 48.0625em) {
                            .nhsuk-footer__list {
                                float: left;
                                padding-bottom: 0;
                                width: 75%;
                            }
                        }

                        .nhsuk-footer__list-item {
                            font-weight: 400;
                            font-size: 14px;
                            font-size: 0.875rem;
                            line-height: 1.71429;
                        }

                        @media (min-width: 40.0625em) {
                            .nhsuk-footer__list-item {
                                font-size: 16px;
                                font-size: 1rem;
                                line-height: 1.5;
                            }
                        }

                        @media print {
                            .nhsuk-footer__list-item {
                                font-size: 14pt;
                                line-height: 1.2;
                            }
                        }

                        @media (min-width: 48.0625em) {
                            .nhsuk-footer__list-item {
                                float: left;
                                margin-right: 32px;
                            }
                        }

                        .nhsuk-footer__list-item-link {
                            color: #425563;
                        }

                        .nhsuk-footer__list-item-link:visited {
                            color: #425563;
                        }

                        .nhsuk-footer__copyright {
                            font-weight: 400;
                            font-size: 14px;
                            font-size: 0.875rem;
                            line-height: 1.71429;
                            color: #425563;
                            margin-bottom: 0;
                        }

                        @media (min-width: 40.0625em) {
                            .nhsuk-footer__copyright {
                                font-size: 16px;
                                font-size: 1rem;
                                line-height: 1.5;
                            }
                        }

                        @media print {
                            .nhsuk-footer__copyright {
                                font-size: 14pt;
                                line-height: 1.2;
                            }
                        }

                        .nhsuk-u-visually-hidden {
                            border: 0;
                            clip: rect(0, 0, 0, 0);
                            height: 1px;
                            margin: -1px;
                            overflow: hidden;
                            padding: 0;
                            position: absolute;
                            white-space: nowrap;
                            width: 1px;
                        }

                        .nhsuk-width-container {
                            margin: 0 16px;
                            max-width: 960px;
                        }

                        @media (min-width: 48.0625em) {
                            .nhsuk-width-container {
                                margin: 0 32px;
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="nhs-logo-container">
                        <img class="nhs-logo" alt="NHS logo" src="https://servicefinder.nhs.uk/assets/images/logotype-nhs-colour.png">
                    </div>
                    <p>${bodyText} <a href=${href}>here</a>.</p>
                    <footer role="contentinfo">
                        <div class="nhsuk-footer" id="nhsuk-footer">
                            <div class="nhsuk-width-container">
                                <h2 class="nhsuk-u-visually-hidden">Support links</h2>
                                <ul class="nhsuk-footer__list">
                                    <li class="nhsuk-footer__list-item"><a class="nhsuk-footer__list-item-link" href="https://www.nhs.uk/Pages/nhs-sites.aspx">NHS sites</a></li>
                                    <li class="nhsuk-footer__list-item"><a class="nhsuk-footer__list-item-link" href="https://www.nhs.uk/about-us">About us</a></li>
                                    <li class="nhsuk-footer__list-item"><a class="nhsuk-footer__list-item-link" href="https://www.nhs.uk/contact-us/">Contact us</a></li>
                                    <li class="nhsuk-footer__list-item"><a class="nhsuk-footer__list-item-link" href="https://www.nhs.uk/our-policies/">Our policies</a></li>
                                </ul>
                                <p class="nhsuk-footer__copyright">&copy; Crown copyright</p>
                            </div>
                        </div>
                    </footer>
                </body>
            </html>
        `;
    }

    switch (event.triggerSource) {
        case "CustomMessage_SignUp":
            event.response.emailSubject = "NHS Service Finder - registration verification";
            event.response.emailMessage = `${getEmailMessage("Thank you for signing up. Please verify your account by clicking", "register/confirmUser")}`;
            break;
        case "CustomMessage_ResendCode":
            event.response.emailSubject = "NHS Service Finder - registration verification reminder";
            event.response.emailMessage = `${getEmailMessage("You have requested a registration verification reminder. To complete your registration please verify your account by clicking", "register/confirmUser")}`;
            break;
        case "CustomMessage_ForgotPassword":
            event.response.emailSubject = "NHS Service Finder - reset password";
            event.response.emailMessage = `${getEmailMessage("Reset your password by clicking", "resetPassword")}`;
            break;
        default:
            // do nothing
            break;
    }
    callback(null, event);
};
