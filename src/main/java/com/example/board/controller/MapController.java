package com.example.board.controller;

import com.example.board.domain.Map;
import com.example.board.domain.Quiz;
import com.example.board.domain.User;
import com.example.board.dto.MapForm;
import com.example.board.dto.QuizForm;
import com.example.board.repository.MapRepository;
import com.example.board.repository.QuizRepository;
import com.example.board.repository.UserRepository;
import com.example.board.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MapController {

    private final MapRepository mapRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    // 맵 생성 폼
    @GetMapping("/create")
    public String createMapForm(Model model) {
        return "createMap";
    }

    // 맵 생성 처리
    @PostMapping("/create")
    public String saveMap(@ModelAttribute MapForm mapForm,
     @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    
        System.out.println(user.getUsername());
        System.out.println(mapForm);
        Map map = Map.builder()
            .mapname(mapForm.getMapName())
            .user(user)
            .nQuiz(String.valueOf(mapForm.getQuizzes().size()))
            .build();

        Map savedMap = mapRepository.save(map);

        for (QuizForm quizForm : mapForm.getQuizzes()) {
            Quiz quiz = Quiz.builder()
                    .map(savedMap)
                    .question(quizForm.getQuestion())
                    .hint(quizForm.getHint())
                    .answer(quizForm.getAnswer())
                    .build();
            quizRepository.save(quiz);
        }

        return "redirect:/";
    }
}
