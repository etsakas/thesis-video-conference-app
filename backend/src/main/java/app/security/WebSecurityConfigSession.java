package app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigSession extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
  
      // Disable CSRF (cross site request forgery)
      http.csrf().disable();
  
      // No session will be created or used by spring security
      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  
      JwtTokenFilterConfigurer jwtTokenFilterConfigurer = new JwtTokenFilterConfigurer(jwtTokenProvider);

      http.antMatcher("/sessions/**").apply(jwtTokenFilterConfigurer);

    }

}