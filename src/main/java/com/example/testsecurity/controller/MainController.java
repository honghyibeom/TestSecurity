package com.example.testsecurity.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.Iterator;

@Controller
public class MainController {

    @GetMapping("/")
    public String mainP(Model model) {

        // 로그인한 사용자의 id값 을 받아오는 로직
        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인한 사용자의 Role 값을 받아내는 로직(role를 이용하여 다양한 기능 수행가능)
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        model.addAttribute("id", id);
        model.addAttribute("role",role);

        return "main";
    }
}