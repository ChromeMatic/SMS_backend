package com.schManSys.sms.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Checks to see if the path is the login path.
        if(request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/RefreshToken")){
            // All this does it pass the request down the filter chain.
            filterChain.doFilter(request,response);
        } else{
            //Creates tokens
            BuildTokens(request,response,filterChain);
        }

    }

    private void BuildTokens(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain filterChain)
            throws ServletException, IOException {
        //Checking for Key
        String authHeader = request.getHeader(AUTHORIZATION);

        //Checking token form headers
        if (authHeader != null && authHeader.startsWith("Bearer ")){

            try{
                String token = authHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);

                String username = decodedJWT.getSubject();
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

                stream(roles).forEach(role ->{
                    authorities.add(new SimpleGrantedAuthority(role));
                });

                UsernamePasswordAuthenticationToken token1 =
                        new UsernamePasswordAuthenticationToken(username,null,authorities);

                SecurityContextHolder.getContext().setAuthentication(token1);

                filterChain.doFilter(request,response);

            }catch (Exception exception){
                log.error("Error logging in: {}",exception.getMessage());
                response.setHeader("Error",exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                //response.sendError(FORBIDDEN.value());
                Map<String,String> error = new HashMap<>();
                error.put("error_message",exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);

            }

        }else{
            filterChain.doFilter(request,response);
        }
    }

}
