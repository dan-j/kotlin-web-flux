package com.danscottjones.kotlinwebflux;

import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

/**
 * We're unable to build a mock {@link WebTestClient} in kotlin due to the class visibility
 * issues highlighted in https://jira.spring.io/browse/SPR-16057, hence a static helper method in
 * java.
 */
public class WebTestClientHelper {

    static WebTestClient.Builder getSecurityMockWebTestClientBuilder(ApplicationContext ctx) {
        return WebTestClient.bindToApplicationContext(ctx)
                            .apply(springSecurity())
                            .configureClient()
                            .filter(basicAuthentication());
    }
}
