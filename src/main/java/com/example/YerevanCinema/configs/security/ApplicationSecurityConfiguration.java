package com.example.YerevanCinema.configs.security;

import com.example.YerevanCinema.filters.JwtAuthorizationFilter;
import com.example.YerevanCinema.services.detailsServices.CustomerDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.YerevanCinema.enums.UserRole.ADMIN;
import static com.example.YerevanCinema.enums.UserRole.CUSTOMER;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomerDetailsService customerDetailsService;

    public ApplicationSecurityConfiguration(CustomerDetailsService customerDetailsService) {
        this.customerDetailsService = customerDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterAfter(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "/about", "/signup", "/contact", "/sessions", "/recover",
                        "/login*", "/recover/**", "/rest/api/**",
                        "/static/css/*", "/static/js/*", "/static/images/*", "/static/fonts/*", "/static/scss/*",
                        "/static/poppins/*", "/static/less/*").permitAll()
                .antMatchers("/admin*", "/admin/**").hasAuthority("ROLE_" + ADMIN.name())
                .antMatchers("/customer*", "/customer/**").hasAuthority("ROLE_" + CUSTOMER.name())
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customerDetailsService);
    }
}
