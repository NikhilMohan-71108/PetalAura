package com.petalaura.admin.config;

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
        HttpSession session = request.getSession(false); // Get the existing session (false means don't create a new one if none exists)

        if (session != null) {
            // Invalidate the current session to prevent session fixation attacks
            session.invalidate();
        }

        // Create a new session to avoid session fixation attack
        session = request.getSession(true); // true to create a new session

        // Store user-specific information in the session
        String username = authentication.getName();  // Use user details after successful authentication
        session.setAttribute("userLoginID", username);  // Store the username in session

        // Optional: Store the original URL the user wanted to visit before login
        String redirectUrl = (String) session.getAttribute("redirectUrl");
        if (redirectUrl == null) {
            redirectUrl = "/";  // Default redirect to homepage if no prior URL
        }

        // Perform the redirect
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}


