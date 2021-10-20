package app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@Order(1)
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigProfile extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
  
      // Disable CSRF (cross site request forgery)
      http.csrf().disable();
  
      // No session will be created or used by spring security
      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  
      // We need to apply the jwt filter only to specific urls. We could apply it to all
      // urls like this
      // http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));
      // and use public void configure(WebSecurity web) to specify the public urls
      // that we don't want the jwt filter to be applied. However, in this case, if we make a
      // request at a url that is not a real endpoint (there is no handler on any controller)
      // then the jwt also gets applied and we will get a token related exception instead of
      // NoHandlerFoundException. So, we only want to apply the jwt filter to secure endpoints
      // that do exist.

      JwtTokenFilterConfigurer jwtTokenFilterConfigurer = new JwtTokenFilterConfigurer(jwtTokenProvider);

      http.antMatcher("/profile/**").apply(jwtTokenFilterConfigurer);

    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

}