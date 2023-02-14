package com.increff.pos.spring;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = Logger.getLogger(SecurityConfig.class);

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security",
                "/swagger-ui.html", "/webjars/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http//
                // Match only these URLs
                .requestMatchers()//
                .antMatchers("/api/**")//
                .antMatchers("/ui/**")//
                .and().authorizeRequests()//
                .antMatchers(HttpMethod.GET, "/api/brands/**").hasAnyAuthority(AuthRole.OPERATOR, AuthRole.SUPERVISOR)//
                .antMatchers(HttpMethod.POST, "/api/brands/search").hasAnyAuthority(AuthRole.OPERATOR, AuthRole.SUPERVISOR)//
                .antMatchers("/api/brands/**").hasAuthority(AuthRole.SUPERVISOR)
                .antMatchers(HttpMethod.GET, "/api/products/**").hasAnyAuthority(AuthRole.OPERATOR, AuthRole.SUPERVISOR)//
                .antMatchers("/api/products/**").hasAuthority(AuthRole.SUPERVISOR)
                .antMatchers(HttpMethod.GET, "/api/inventory/**").hasAnyAuthority(AuthRole.OPERATOR, AuthRole.SUPERVISOR)//
                .antMatchers("/api/inventory/**").hasAuthority(AuthRole.SUPERVISOR)
                .antMatchers("/api/orders/**").hasAnyAuthority(AuthRole.SUPERVISOR, AuthRole.OPERATOR)//
                .antMatchers("/api/**").hasAnyAuthority(AuthRole.SUPERVISOR, AuthRole.OPERATOR)
                .antMatchers("/ui/admin/**").hasAuthority(AuthRole.SUPERVISOR)
                .antMatchers("/ui/**").hasAnyAuthority(AuthRole.SUPERVISOR, AuthRole.OPERATOR)
                .antMatchers("/invoice/**").hasAnyAuthority(AuthRole.SUPERVISOR, AuthRole.OPERATOR)
                // Ignore CSRF and CORS
                .and().csrf().disable().cors().disable();
        logger.info("Configuration complete");
    }

}
