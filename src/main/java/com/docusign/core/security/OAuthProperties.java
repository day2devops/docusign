package com.docusign.core.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;

@Getter
@Setter
public class OAuthProperties extends OAuth2SsoProperties {

    private String redirectUrl;

    public String getLoginPath() {
        System.out.println("***********************");
        System.out.println(super.getLoginPath() + redirectUrl);
        System.out.println("***********************");
        return super.getLoginPath() + redirectUrl;
    }
}
