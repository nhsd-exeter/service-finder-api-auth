package uk.nhs.digital.uec.api.testsupport;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;

/**
 * Test factory for the creation of {@link AuthenticationResultType}s in known states for testing purposes
 */
public class AuthenticationResultTypeTestFactory {

    public static final String ACCESS_TOKEN_WITH_SEARCH_GROUP = "eyJraWQiOiJZV3A4djNLeWNGeHUzOGNGTVowdkJqRFdpYXhzWkJrTEppT01WcEFWa2FNPSIsImFsZyI6IlJTM"
        + "jU2In0.eyJzdWIiOiJlMWVkZTU4OS1jYWVhLTQ0NmEtOTU1NS1lZmFhOWY5MTBjMGMiLCJjb2duaXRvOmdyb3VwcyI6WyJTRUFSQ0giXSwiZXZlbnRfaWQiOiJjNDczMjc4NC05NWQzLT"
        + "RhMDktODMzZS0xNjJkNzRhMGJmNDgiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIiwiYXV0aF90aW1lIjoxNTY4MjcwNTc5"
        + "LCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuZXUtd2VzdC0yLmFtYXpvbmF3cy5jb21cL2V1LXdlc3QtMl93R1FtT1psNFUiLCJleHAiOjE1NjgyNzQxNzksImlhdCI6MTU2ODI3MD"
        + "U3OSwianRpIjoiOGUyYjUwMjYtNTAxNS00ODU1LWExYmMtNGZkNDA0NmRkYjUxIiwiY2xpZW50X2lkIjoiaWFuOWI2YzgwaDRqc3N1YXVkcDR1MnNycyIsInVzZXJuYW1lIjoiZTFlZGU1"
        + "ODktY2FlYS00NDZhLTk1NTUtZWZhYTlmOTEwYzBjIn0.aJLdoqdVmoKQXZMeFAJEle1Nni2_oij6Y3bD0GZGi-KQ46KgfnW0FdC17qIjNn7QcYiwSUAn-l3PaSaoIy5aVi-J5iiYjM8Ya4"
        + "cKXRBs8ghwn85nJSTQoJj2YrdUxSYQ9Z5I11sjTpzFIjnaVX04Q2txGZ6KU4_71O9pF2THtw4IllWVx45EtFUMINAGY2r7w4Gf_lxpFCwRUDKiY4g1Ma3tPlTvcQiECBSZRrPmGmLYKCBgo"
        + "phaEekU3QGpFH9VJRCTGLwIO7iojfJzOsDgH2IxhmF1QetsUB-BMcg35eDQ56l9RRAxl8wnXqSHCZbhCNVqHORKZR5xGHgpg6_6bg";

    public static final String ACCESS_TOKEN_SUB = "e1ede589-caea-446a-9555-efaa9f910c0c";

    public static final String REFRESH_TOKEN_VALUE = "TestRefreshTokenValue";

    public static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN=eyJraWQiOiJZV3A4djNLeWNGeHUzOGNGTVowdkJqRFdpYXhzWkJrTEppT01WcEFWa2FNPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJlMWVkZTU4OS1jYWVhLTQ0NmEtOTU1NS1lZmFhOWY5MTBjMGMiLCJjb2duaXRvOmdyb3VwcyI6WyJTRUFSQ0giXSwiZXZlbnRfaWQiOiJjNDczMjc4NC05NWQzLTRhMDktODMzZS0xNjJkNzRhMGJmNDgiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIiwiYXV0aF90aW1lIjoxNTY4MjcwNTc5LCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuZXUtd2VzdC0yLmFtYXpvbmF3cy5jb21cL2V1LXdlc3QtMl93R1FtT1psNFUiLCJleHAiOjE1NjgyNzQxNzksImlhdCI6MTU2ODI3MDU3OSwianRpIjoiOGUyYjUwMjYtNTAxNS00ODU1LWExYmMtNGZkNDA0NmRkYjUxIiwiY2xpZW50X2lkIjoiaWFuOWI2YzgwaDRqc3N1YXVkcDR1MnNycyIsInVzZXJuYW1lIjoiZTFlZGU1ODktY2FlYS00NDZhLTk1NTUtZWZhYTlmOTEwYzBjIn0.aJLdoqdVmoKQXZMeFAJEle1Nni2_oij6Y3bD0GZGi-KQ46KgfnW0FdC17qIjNn7QcYiwSUAn-l3PaSaoIy5aVi-J5iiYjM8Ya4cKXRBs8ghwn85nJSTQoJj2YrdUxSYQ9Z5I11sjTpzFIjnaVX04Q2txGZ6KU4_71O9pF2THtw4IllWVx45EtFUMINAGY2r7w4Gf_lxpFCwRUDKiY4g1Ma3tPlTvcQiECBSZRrPmGmLYKCBgophaEekU3QGpFH9VJRCTGLwIO7iojfJzOsDgH2IxhmF1QetsUB-BMcg35eDQ56l9RRAxl8wnXqSHCZbhCNVqHORKZR5xGHgpg6_6bg; SameSite=None";
    public static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN=TestRefreshTokenValue; SameSite=None";

    public static AuthenticationResultTypeBuilder atestAuthenticationResultType() {
        return new AuthenticationResultTypeBuilder()
            .withAccessToken(ACCESS_TOKEN_WITH_SEARCH_GROUP)
            .withRefreshToken(REFRESH_TOKEN_VALUE);
    }

    public static class AuthenticationResultTypeBuilder {

        private String accessToken;

        private String refreshToken;

        public AuthenticationResultTypeBuilder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public AuthenticationResultTypeBuilder withRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AuthenticationResultType build() {
            AuthenticationResultType authenticationResultType = new AuthenticationResultType();
            authenticationResultType.setAccessToken(accessToken);
            authenticationResultType.setRefreshToken(refreshToken);
            return authenticationResultType;
        }

    }

}
