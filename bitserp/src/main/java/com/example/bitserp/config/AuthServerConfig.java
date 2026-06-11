package com.example.bitserp.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.filter.OncePerRequestFilter;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Configuration
public class AuthServerConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        httpSecurity.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(
                        authorizationServerConfigurer,
                        authServer -> authServer.oidc(Customizer.withDefaults())
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers(
//                                authorizationServerConfigurer.getEndpointsMatcher()  // ← ignore CSRF for OAuth endpoints
//                        )
//                )
                .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                        new org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint("/login"),
                        new org.springframework.security.web.util.matcher.MediaTypeRequestMatcher(
                                MediaType.TEXT_HTML
                        )
                ));
        return httpSecurity.build();
    }
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> debugRequestFilter() {
        FilterRegistrationBean<OncePerRequestFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    jakarta.servlet.http.HttpServletRequest request,
                    jakarta.servlet.http.HttpServletResponse response,
                    jakarta.servlet.FilterChain chain)
                    throws jakarta.servlet.ServletException, java.io.IOException {
                if (request.getRequestURI().contains("token")) {
                    System.out.println(">>> CONTENT TYPE: " + request.getContentType());
                    System.out.println(">>> METHOD: " + request.getMethod());
                    System.out.println(">>> GRANT TYPE PARAM: " + request.getParameter("grant_type"));
                    System.out.println(">>> ALL PARAMS: " + request.getParameterMap().keySet());
                }
                chain.doFilter(request, response);
            }
        });
        reg.addUrlPatterns("/oauth2/token");
        reg.setOrder(-100);
        return reg;
    }
//    @Bean
//    public FilterRegistrationBean<OncePerRequestFilter> csrfBypassFilter() {
//        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new OncePerRequestFilter() {
//            @Override
//            protected void doFilterInternal(
//                    jakarta.servlet.http.@NonNull HttpServletRequest request,
//                    jakarta.servlet.http.@NonNull HttpServletResponse response,
//                    jakarta.servlet.@NonNull FilterChain filterChain)
//                    throws jakarta.servlet.ServletException, java.io.IOException {
//                request.setAttribute(
//                        org.springframework.security.web.csrf.CsrfFilter.class.getName() + ".SKIP",
//                        Boolean.TRUE
//                );
//                filterChain.doFilter(request, response);
//            }
//        });
//        registration.addUrlPatterns("/oauth2/token", "/oauth2/authorize", "/oauth2/revoke");
//        registration.setOrder(-1);
//        return registration;
//    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
//        System.out.println(">>> BCRYPT HASH: " +
//                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
//                        .encode("bitserp-secret"));
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedSecret = encoder.encode("bitserp-secret");
        System.out.println(">>> HASH: " + encodedSecret);
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("bitserp-client")
                .clientSecret(encodedSecret)
                .clientAuthenticationMethods(methods -> {
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                })
                .authorizationGrantTypes(grants -> {
                    grants.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    grants.add(AuthorizationGrantType.REFRESH_TOKEN);
                    grants.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                })
                .redirectUri("http://localhost:3000/callback")
                .redirectUri("http://127.0.0.1:3000/callback")
                .postLogoutRedirectUri("http://localhost:3000")
                .scopes(scopes -> {
                    scopes.add("read");
                    scopes.add("write");
                    scopes.add("openid");
                    scopes.add("profile");
                })
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(8))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .reuseRefreshTokens(false)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }
//    @Bean
//    public ApplicationRunner debugClient(RegisteredClientRepository repo) {
//        return args -> {
//            RegisteredClient client = repo.findByClientId("bitserp-client");
//            if (client == null) {
//                System.out.println(">>> CLIENT NOT FOUND IN REPO");
//            } else {
//                System.out.println(">>> CLIENT FOUND: " + client.getClientId());
//                System.out.println(">>> SECRET: " + client.getClientSecret());
//                System.out.println(">>> GRANT TYPES: " + client.getAuthorizationGrantTypes());
//            }
//        };
//    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            if(OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getPrincipal().getAuthorities().forEach(auth -> {
                    String role = Objects.requireNonNull(auth.getAuthority()).replace("ROLE_", "");
                    context.getClaims().claim("roles", java.util.List.of(role));
                });
                context.getClaims().subject(context.getPrincipal().getName());
            }
            context.getClaims().subject(context.getPrincipal().getName());
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return org.springframework.security.oauth2.jwt.NimbusJwtDecoder
                .withJwkSetUri("http://localhost:8080/oauth2/jwks")
                .build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().issuer("http://localhost:8080").build();
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
