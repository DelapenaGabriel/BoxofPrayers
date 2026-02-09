package com.server.security;

import com.server.security.jwt.JwtFilter;
import com.server.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final UserModelDetailsService userModelDetailsService;
    private final JwtFilter jwtFilter;

    public WebSecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler,
            UserModelDetailsService userModelDetailsService,
            JwtFilter jwtFilter
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.userModelDetailsService = userModelDetailsService;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Needed when authentication provider compares passwords
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure paths and requests that should be ignored by Spring Security
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      // Use setAllowedOriginPatterns to support wildcards
      configuration.setAllowedOriginPatterns(Arrays.asList(
          "http://localhost:8080",
          "http://localhost:5173",
          "http://localhost:8100",
              "http://192.168.0.114:5173",
          "capacitor://localhost",
          "http://localhost",
          "https://*.netlify.app",  // Allow all Netlify subdomains
          "https://boxofprayers.com",  // Production domain
          "http://boxofprayers.com",
          "https://www.boxofprayers.com",
          "http://www.boxofprayers.com"    // Production domain (http)
      ));
      configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
      configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
      configuration.setAllowCredentials(true);  // Important for cookies/auth headers
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
    }

    /**
     * Configure com.techelevator.auctions.security settings
     *
     * Usage of new lambda expression configuration:
     * https://docs.spring.io/spring-security/reference/migration-7/configuration.html#_use_the_lambda_dsl
     *
     * Migrating deprecated methods:
     * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configure com.techelevator.auctions.security settings
     *
     * Usage of new lambda expression configuration:
     * https://docs.spring.io/spring-security/reference/migration-7/configuration.html#_use_the_lambda_dsl
     *
     * Migrating deprecated methods:
     * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // CSRF not needed because we're stateless and not using cookies
                .csrf(crsf -> crsf.disable())
                .cors(cors -> {}) // Enable CORS

                // Remove any default session functionality
                // https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#_things_to_consider_when_moving_away_from_sessionmanagementfilter
                .sessionManagement(sessionManagement -> {
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })

                // https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/configurers/ExceptionHandlingConfigurer.html
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                    exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler);
                })
                
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(new AntPathRequestMatcher("/api/login"), new AntPathRequestMatcher("/api/register/**")).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/prayers", "/api/prayers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/prayer-requests", "/api/prayer-requests/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bible-verses/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/leaderboard").permitAll()
                        .anyRequest().authenticated()
                )

                // Add the JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}

