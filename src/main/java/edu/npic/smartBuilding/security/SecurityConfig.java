package edu.npic.smartBuilding.security;

import edu.npic.smartBuilding.config.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final ApiKeyFilter apiKeyFilter;

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt -> {
            String id = jwt.getId();
            log.info("JWT Token: {}", jwt.getTokenValue());
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(id);
            log.info("AUTHORITIES: {}", userDetails.getAuthorities());
            return userDetails.getAuthorities()
                    .stream()
                    .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
                    .collect(Collectors.toList());
        };

        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider(@Qualifier("jwtDecoderRefreshToken") JwtDecoder jwtDecoderRefreshToken){
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoderRefreshToken);
        provider.setJwtAuthenticationConverter(jwtAuthenticationConverter());
        return provider;
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

    @Bean
    SecurityFilterChain configFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(
                jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder)
        ));

        http.cors(Customizer.withDefaults());

        // Disabled CSRF Token
        http.csrf(AbstractHttpConfigurer::disable);

        // Make API stateless
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Handle 403 , 401
        http.exceptionHandling(e -> e.accessDeniedHandler(customAccessDeniedHandler).authenticationEntryPoint(customAuthenticationEntryPoint));

//        http.oauth2Login(oauth -> oauth.successHandler(oAuth2LoginSuccessHandler));

        http.addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://localhost:4173", "https://sbs.iot.npic.edu.kh",  "http://192.168.100.15:3000", "https://smart-building-api.iot.npic.edu.kh"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(List.of("authorization","content-type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
