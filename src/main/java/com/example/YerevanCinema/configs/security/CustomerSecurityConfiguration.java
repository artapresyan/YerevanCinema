package com.example.YerevanCinema.configs.security;


import com.example.YerevanCinema.services.detailsServices.CustomerDetailsService;
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
@Order(2)
public class CustomerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomerDetailsService customerDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomerSecurityConfiguration(CustomerDetailsService customerDetailsService, PasswordEncoder passwordEncoder) {
        this.customerDetailsService = customerDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .antMatcher("/customer/**")
                .antMatcher("/customer*")
                .authorizeRequests()
                .antMatchers("/", "/about", "/signup", "/contact", "/sessions", "/recover",
                        "/login*", "/recover/**", "/rest/api/**",
                        "/static/css/*", "/static/js/*", "/static/images/*", "/static/fonts/*", "/static/scss/*",
                        "/static/poppins/*", "/static/less/*").permitAll()
                .antMatchers("/customer*", "/customer/**").hasAuthority("ROLE_" + CUSTOMER.name())
                .antMatchers("/admin*", "/admin/**").hasAuthority("ROLE_" + ADMIN.name())

                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
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
                .userDetailsService(customerDetailsService).passwordEncoder(passwordEncoder);
    }
}
