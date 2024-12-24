package com.petalaura.customer.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();

        // Invalidate the current session to prevent session fixation attacks
        session.invalidate();
        session = request.getSession(true);  // Create a new session

        // Store user-specific information in the session
        String username = authentication.getName();  // Use user details after successful authentication
        session.setAttribute("userLoginID", username);

        // Optional: Store the original URL the user wanted to visit before login
        String redirectUrl = (String) session.getAttribute("redirectUrl");
        if (redirectUrl == null) {
            redirectUrl = "/";  // Default redirect to homepage
        }

        // Perform the redirect
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

