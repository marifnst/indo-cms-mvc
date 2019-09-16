package com.indocms.mvcapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class IndoCMSWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    IndoCMSAuthenticationProvider authenticationProvider;

    String[] staticResources = new String [] {
        "/static/**", "/bower_components/**", "/build/**", "/dist/**", "/documentation/**", "/font/**", 
        "/gif/**", "/js/**", "/plugins/**", "/templates/**",
        "home.html", "/pages/**"};
    // String[] staticResources = new String []{"/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(new IndoCMSPreFilter(), BasicAuthenticationFilter.class)
            .authorizeRequests()
                // .antMatchers("/", "/home").permitAll()
                // .antMatchers("/template/**").hasAnyRole("USER")
                // .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
                .and()                
            .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            .and()
                .csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers(staticResources);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // auth.inMemoryAuthentication()
        // .passwordEncoder(passwordEncoder())
        // .withUser("user").password(passwordEncoder().encode("user")).roles("USER")
        // .and()
        // .withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");

        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } 
}