package com.example.demo.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.entities.User;
import com.example.demo.services.UserService;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;
    
    private boolean isUriPublic(String uriPath) {   	
        boolean isUriPublic = false;
    	List<String> uris = new ArrayList<String>();
    	
    	uris.add("/login");
    	uris.add("/register");
    	
    	for(String uri : uris) {
    		if(uri.equals(uriPath)) {
    			isUriPublic = true;
    		}
    	}
    	
    	System.out.println("Publica " + isUriPublic);
    	return isUriPublic;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {
        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        String userName = null;
        
        
        if(isUriPublic(httpServletRequest.getRequestURI())) {
        	filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            if(null != authorization && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
                try {
                	userName = jwtUtil.getUsernameFromToken(token);
                }catch(Exception e) {
                	ObjectMapper mapper = new ObjectMapper();
                	httpServletResponse.getWriter().write(e.getMessage());
                	httpServletResponse.setStatus(403);
                	return;
                }
                
            } else {
            	ObjectMapper mapper = new ObjectMapper();
            	httpServletResponse.setStatus(400);
            	mapper.writeValue(httpServletResponse.getWriter(),"Authentication header not found");
            	//throw new ServletException("Authentication header not found");
            }

            if(null != userName && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails
                        = userService.loadUserByUsername(userName);
               
                
                if(jwtUtil.validateToken(token,userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    
                    usernamePasswordAuthenticationToken.setDetails(
                            userDetails.getId()
                    );

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    
                } 

            }
            
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

}
