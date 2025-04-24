package com.example.board.controller;

import com.example.board.domain.Map;
import com.example.board.domain.User;
import com.example.board.dto.RoomInfo;
import com.example.board.repository.MapRepository;
import com.example.board.repository.UserRepository;
import com.example.board.service.CustomUserDetails;
import com.example.board.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final MapRepository mapRepository;
    private final UserRepository userRepository;
    private final RoomService roomService;

    // 1. 맵 선택 + 방 생성 폼 (페이지네이션 + 검색 포함)
    @GetMapping("/create")
    public String showRoomCreatePage(Model model,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Map> mapPage;

        if (keyword != null && !keyword.isEmpty()) {
            mapPage = mapRepository.findByMapnameContaining(keyword, pageable);
        } else {
            mapPage = mapRepository.findAll(pageable);
        }

        model.addAttribute("mapPage", mapPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "room/createRoom";
    }

    // 2. 방 생성 처리
    @PostMapping("/create")
    public String createRoom(@RequestParam String roomname,
                             @RequestParam(required = false) String password,
                             @RequestParam Long mapId,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        User host = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Map selectedMap = mapRepository.findById(mapId).orElseThrow();

        String roomId = roomService.createRoom(roomname, password, host, selectedMap);

        return "redirect:/rooms/waiting/" + roomId;
    }

    // 3. 대기방 화면
    @GetMapping("/waiting/{roomId}")
    public String showWaitingRoom(@PathVariable String roomId, Model model) {
        RoomInfo roomInfo = roomService.getRoom(roomId);
        if (roomInfo == null) {
            return "redirect:/rooms/create?error=notfound";
        }
        model.addAttribute("room", roomInfo);
        return "room/waitingRoom";
    }
}
