package com.example.cinematicket.configuratons;

import com.example.cinematicket.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // nhúng Jwtfilter
                .authorizeHttpRequests(requests -> {
                    requests

                            .requestMatchers(
                                String.format("%s/user/login", apiPrefix),
                                String.format("%s/user/register", apiPrefix),
                                String.format("%s/user/myInfo", apiPrefix),
                                String.format("%s/user", apiPrefix)
                            )
                            .permitAll();
                });
        return http.build();
    }
}
