package com.jitterted.mobreg.adapter.in.web;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.Arrays;
import java.util.Map;

public class OAuth2UserFactory {
    @NotNull
    public static DefaultOAuth2User createOAuth2UserWithMemberRole(String githubUsername, String... roles) {
        return new DefaultOAuth2User(
                Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList(),
                Map.of("login", githubUsername, "name", "Ted M. Young"),
                "name");
    }

    @NotNull
    public static SecurityMockMvcRequestPostProcessors.OAuth2LoginRequestPostProcessor oAuth2User(String role) {
        return SecurityMockMvcRequestPostProcessors
                .oauth2Login()
                .oauth2User(
                        createOAuth2UserWithMemberRole("tedyoung", role)
                );
    }

    @NotNull
    static OAuth2UserAuthority createGitHubUserWithLogin(String loginUsername) {
        return new OAuth2UserAuthority(
                Map.of("url", "https://api.github.com/",
                       "login", loginUsername));
    }
}
