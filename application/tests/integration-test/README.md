This is a readme for the integration/smoke tests.

In order to understand how the framework fits together visit:<br>
https://nhsdbjss.atlassian.net/wiki/spaces/SFD/pages/513867794/Automation+Framework

Description of components:
* resources:<br>
Mappings: these are the mapping files for wiremock which get loaded in. Every test locally is executed against wiremock. All upstream interactions are stubbed.<br>
Properties: this file has the URL's in for the targets<br>
* common:<br>
Driver: wrapper for a static remote webdriver. Static because we will never run more than a minute worth Selenium.<br>
Hooks: this is before/after hooks to handle login, logout, screenshot on failure etc<br>
PropertyReader: to read the props file and return props in an easy way<br>
SharedDriver: this is to make sure the webdriver is safeguarded and no test can quit it
* *.feature: feature files, keep them lean
* pages: <br>
Page object model representation of the application. The idea is that there are two high level classes, one for actions the other for elements. This way is an element changes, it is easy to find, and it is decoupled from any actual user action - so easy to change. Actions are the methods that glue code (or stepdefs) interact with. Make sure you got the right return types etc. Never put assertions in page object models, put them in glue. <br>
User the wrappers found in ParentPageElements for finding webelements. These wrappers allow dynamic element finding, unlike the annotiation based POM. This should work quite nicely with the react single page app front end.
* glue/stepdefs:<br>
This is the code which connects the page objects with the feature files. Keep it simple. <br>
ALL ASSERTIONS GO HERE.<br>
NO LOGIC IN HERE.
* Runners: <br>
These files are the ones that get called by gradle tasks and run the tests. <br>
You can configure tags and features to run easily from here. Mind if you change it, the pipeline will change as well.

All tests in here run against wiremock. The flow of events is as follows: <br>
1. UI triggers action
2. One of the endpoints pick the action up (SFS or Auth)
3. The endpoints call to upstream - but all upstream is stubbed. (DoS/Choices)
4. Wiremock receives the call, verifies headers, and full content, then if there is a matching stub it responds.
5. The endpoints get the response and forward results to the UI.

Things to be mindful of:<br>
* this is a full docker env - so components interact by domain names
* the UI is hosted in an nginx container, as opposed do S3+CDN in prod however, this makes little difference as it is just staticially hosted files.
* wiremock literally verifies everything, the smallest typo or difference will prevent it from responding
* wiremock also issues tokens - it is always the same, but it issues it, and checks it when it receives it back
* the refresh token in the local test environments are not kept secure, in order to be able to keep the env http
