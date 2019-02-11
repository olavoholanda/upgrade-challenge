package com.upgrade.campside.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring security configuration class. In this class is every configuration for credentials and
 * authorization for http requests. For now, is authorizing all requests.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * Configures <code>HttpSecurity</code> to permit any request for anonymous users.
   *
   * @param http a <code>HttpSecurity</code> object.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().authorizeRequests()
        .anyRequest().permitAll();
  }
}
