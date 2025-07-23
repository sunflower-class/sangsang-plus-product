package com.sangsangplus.productservice.config;

import com.sangsangplus.productservice.security.JwtUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class) &&
               (parameter.getParameterType().equals(Long.class) || 
                parameter.getParameterType().equals(JwtUserDetails.class));
    }
    
    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, 
                                WebDataBinderFactory binderFactory) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof JwtUserDetails) {
            
            JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
            
            if (parameter.getParameterType().equals(Long.class)) {
                return userDetails.getUserId();
            } else if (parameter.getParameterType().equals(JwtUserDetails.class)) {
                return userDetails;
            }
        }
        return null;
    }
}