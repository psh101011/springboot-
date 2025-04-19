package com.example.board.controller;

import com.example.board.domain.User;
import com.example.board.repository.UserRepository;
import com.example.board.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // ✅ 여길 이렇게 변경!

    //home 화면
    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails,Model model) {
        if (userDetails != null) {
            model.addAttribute("user", userDetails.getUsername());
        } else {
            model.addAttribute("user", null);
        }
        return "home";
    }
    


    //회원가입
    //화면
    @GetMapping("/register")
    public String registerForm(@AuthenticationPrincipal CustomUserDetails userDetails,Model model) {
        if(userDetails != null){
            return "redirect:/";
        }
        model.addAttribute("user", new User());

        return "register";
    }
    //회원가입 처리
    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model){
        
        try {
            // 1. 아이디 중복 체크
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                model.addAttribute("errorMessage", "이미 사용 중인 아이디입니다.");
                return "register"; // 다시 회원가입 페이지로
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            System.out.println("✅ 사용자 저장 완료!");
            return "redirect:/login";
        } catch (Exception e) {
            System.out.println("❌ 저장 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return "register";
        }
    }

    

    // 로그인
    //화면
    @GetMapping("/login")
    public String loginForm(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if(userDetails != null){
            return "redirect:/";
        }
        return "login";
    }

    


}
