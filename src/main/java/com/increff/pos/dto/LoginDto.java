package com.increff.pos.dto;

import com.increff.pos.api.ApiException;
import com.increff.pos.model.LoginForm;
import com.increff.pos.spring.AuthRole;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.UserPrincipal;
import com.increff.pos.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@Service
public class LoginDto {

    @Value("#{'${app.supervisorEmails}'.split(',')}")
    private List<String> supervisorEmails;

    private static String role;

    public ModelAndView login(HttpServletRequest req, LoginForm loginForm) throws ApiException {
        ValidationUtil.validate(loginForm);
        if (isSupervisor(loginForm)) {
            role = AuthRole.SUPERVISOR;
        } else {
            role = AuthRole.OPERATOR;
        }

        // Create authentication object
//        todo change this
        Authentication authentication = convert(role, loginForm);
        // Create new session
        HttpSession session = req.getSession(true);
        // Attach Spring SecurityContext to this new session
        SecurityUtil.createContext(session);
        // Attach Authentication object to the Security Context
        SecurityUtil.setAuthentication(authentication);

        return new ModelAndView("redirect:/ui/inventory");
    }

    private boolean isSupervisor(LoginForm loginForm) {
        return supervisorEmails.contains(loginForm.getEmail());
    }

    private Authentication convert(String role, LoginForm loginForm) {
        // Create principal
        UserPrincipal principal = new UserPrincipal();
        principal.setEmail(loginForm.getEmail());
        principal.setRole(role);

        // Create Authorities
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role));
        // you can add more roles if required

        // Create Authentication
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        return token;
    }

}
