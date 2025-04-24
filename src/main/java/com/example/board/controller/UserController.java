package com.example.board.controller;

import com.example.board.domain.Map;
import com.example.board.domain.User;
import com.example.board.repository.MapRepository;
import com.example.board.repository.UserRepository;
import com.example.board.service.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    private final MapRepository mapRepository;
    private final BCryptPasswordEncoder passwordEncoder; // ✅ 여길 이렇게 변경!

    //home 
    //화면
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

    
    @PostMapping("/withdraw")
    public String deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow();
        // 관리자 계정 불러오기
        User admin = userRepository.findByUsername("admin")
            .orElseThrow(() -> new IllegalStateException("관리자 계정이 없습니다."));

        // 유저가 가진 맵의 소유권을 관리자에게 넘김
        List<Map> userMaps = mapRepository.findAllByUser(user);
        for (Map map : userMaps) {
            map.setUser(admin);
        }
        mapRepository.saveAll(userMaps);
        userRepository.deleteById(userDetails.getUser().getId());

        // 스프링 시큐리티 로그아웃 처리
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/";
    }


}
