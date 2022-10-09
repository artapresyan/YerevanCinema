package com.example.YerevanCinema.configs.security;


import com.example.YerevanCinema.services.detailsServices.UserDetailsServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.YerevanCinema.enums.UserRole.ADMIN;
import static com.example.YerevanCinema.enums.UserRole.CUSTOMER;

@Configuration
@EnableWebSecurity
@Order(1)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public ApplicationSecurityConfiguration(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/about", "/signup", "/contact", "/sessions", "/recover",
                        "/recover/**", "/rest/api/**",
                        "/static/css/*", "/static/js/*", "/static/images/*", "/static/fonts/*", "/static/scss/*",
                        "/static/poppins/*", "/static/less/*").permitAll()
                .antMatchers("/customer*", "/customer/**").hasAuthority("ROLE_" + CUSTOMER.name())
                .antMatchers("/admin*", "/admin/**").hasAuthority("ROLE_" + ADMIN.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home").permitAll()
                .and()
                .logout().permitAll()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
