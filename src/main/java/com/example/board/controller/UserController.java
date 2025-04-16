package com.example.board.controller;

import com.example.board.domain.User;
import com.example.board.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ✅ 여길 이렇게 변경!

    //home 화면
    @GetMapping("/")
    public String home(HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "home";
    }
    


    //회원가입
    //화면
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    //회원가입 처리
    @PostMapping("/register")
    public String register(@ModelAttribute User user){
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            System.out.println("✅ 사용자 저장 완료!");
        } catch (Exception e) {
            System.out.println("❌ 저장 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/login";
    }

    

    // 로그인
    //화면
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    //로그인 처리
    @PostMapping("/login")
public String login(@RequestParam String username,
                    @RequestParam String password,
                    HttpSession session,
                    Model model)
{
    System.out.println("입력된 ID: " + username);
    System.out.println("입력된 PW: " + password);

    Optional<User> userOptional = userRepository.findByUsername(username);

    if(userOptional.isPresent()){
        User user = userOptional.get();
        System.out.println("DB PW: " + user.getPassword());

        if(passwordEncoder.matches(password, user.getPassword())){
            session.setAttribute("user", user);
            System.out.println("✅ 로그인 성공");
            return "redirect:/";
        } else {
            System.out.println("❌ 비밀번호 불일치");
        }
    } else {
        System.out.println("❌ 아이디 없음");
    }

    model.addAttribute("loginError", true);
    return "login";
}


}
