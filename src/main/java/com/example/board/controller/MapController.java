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

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {

    private final MapRepository mapRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    // 맵 생성 폼
    @GetMapping("/create")
    public String createMapForm(Model model) {
        return "createMap";
    }

    @GetMapping("/my_maps")
    public String myMaps(@AuthenticationPrincipal CustomUserDetails userDetails,
                     @RequestParam(defaultValue = "0") int page,
                     Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending()); // 최신순 10개씩
        Page<Map> mapPage = mapRepository.findByUser(user, pageable);

        model.addAttribute("mapPage", mapPage);
        model.addAttribute("currentPage", page);
        return "myMaps";
    }


    // 맵 생성 처리
    @PostMapping("/create")
    public String saveMap(@ModelAttribute MapForm mapForm,
     @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

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


    @PostMapping("/delete/{mapId}")
    public String deleteMap(@PathVariable Long mapId,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map map = mapRepository.findById(mapId).orElseThrow();

        // 본인 맵만 삭제 가능
        if (!map.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        List<Quiz> quizzes = quizRepository.findByMap(map);
        quizRepository.deleteAll(quizzes);
        mapRepository.delete(map);
        return "redirect:/map/my_maps";
    }

    @GetMapping("/edit/{mapId}")
    public String editMapForm(@PathVariable Long mapId,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        Model model) {
        Map map = mapRepository.findById(mapId).orElseThrow();

        // 본인 맵만 삭제 가능
        if (!map.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        List<Quiz> quizzes = quizRepository.findByMap(map);
        model.addAttribute("map", map);
        model.addAttribute("quizzesEdit", quizzes);

        return "editMap";
    }


    // 맵 수정 처리
    @Transactional
    @PostMapping("/edit/{mapId}")
    public String editMap(@PathVariable Long mapId,
        @ModelAttribute MapForm mapForm,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
            
        Map map = mapRepository.findById(mapId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 맵입니다."));

        // 본인 맵만 수정 가능
        if (!map.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        map.setMapname(mapForm.getMapName());
        map.setNQuiz(String.valueOf(mapForm.getQuizzes().size()));


        Map savedMap = mapRepository.save(map);

        quizRepository.deleteByMap(map); 

        for (QuizForm quizForm : mapForm.getQuizzes()) {
            Quiz quiz = Quiz.builder()
                    .map(savedMap)
                    .question(quizForm.getQuestion())
                    .hint(quizForm.getHint())
                    .answer(quizForm.getAnswer())
                    .build();
            quizRepository.save(quiz);
        }

        return "redirect:/map/my_maps";
    }

}
