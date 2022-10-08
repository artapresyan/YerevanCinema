package com.example.YerevanCinema.configs.security;


import com.example.YerevanCinema.services.detailsServices.AdminDetailsService;
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
public class AdminSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AdminDetailsService adminDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AdminSecurityConfiguration(AdminDetailsService adminDetailsService, PasswordEncoder passwordEncoder) {
        this.adminDetailsService = adminDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .antMatcher("/admin/**")
                .antMatcher("/admin*")
                .authorizeRequests()
                .antMatchers("/", "/about", "/signup", "/contact", "/sessions", "/recover",
                        "/login*", "/recover/**", "/rest/api/**",
                        "/static/css/*", "/static/js/*", "/static/images/*", "/static/fonts/*", "/static/scss/*",
                        "/static/poppins/*", "/static/less/*").permitAll()
                .antMatchers("/admin*", "/admin/**").hasAuthority("ROLE_" + ADMIN.name())
                .antMatchers("/customer*","/customer/").hasAuthority("ROLE_"+CUSTOMER.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .and()
                .logout()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(adminDetailsService).passwordEncoder(passwordEncoder);
    }
}
