package app.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import app.exception.TokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtTokenFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {
        
        // Save initial path. Since CustomErrorController always uses /error path
        // we can't retrieve the initial path using httpRequest.getRequestURI() method
        httpServletRequest.setAttribute("initialPath", httpServletRequest.getRequestURI());

        if (httpServletRequest.getHeader("Authorization") == null) {
            throw new TokenException("Authorization field is empty.");
        }
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        if (token == null) {
            throw new TokenException("Non Bearer Token");
        }

        // throws TokenException
        jwtTokenProvider.validateToken(token);
        // throws NoUserFoundException
        Authentication auth = jwtTokenProvider.getAuthentication(token);

        SecurityContextHolder.getContext().setAuthentication(auth);
            
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
