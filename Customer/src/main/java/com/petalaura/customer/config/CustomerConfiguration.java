package com.petalaura.customer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class CustomerConfiguration {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    CustomSuccessHandler customSuccessHandler;

    public CustomerConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/css/**", "/imgs/**", "/js/**", "/fonts/**", "/sass/**", "/**", "/address", "/login").permitAll()
                        .requestMatchers("/resetPassword", "/reset_password", "/resetPassword/**", "/reset_password/**").permitAll()
                        .requestMatchers("/shop/**").hasAuthority("CUSTOMER")
                        .requestMatchers("/dashboard/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(login ->
                            login.loginPage("/login")
  //                              login.loginPage("/home")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/home", true)
                                .permitAll()
                )

                .logout(logout ->
                        logout.invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/login?invalid")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                ;
        return http.build();
    }
}