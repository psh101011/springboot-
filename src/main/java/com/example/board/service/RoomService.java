package com.example.board.service;

import com.example.board.domain.User;
import com.example.board.domain.Map;
import com.example.board.dto.RoomInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RedisTemplate<String, Object> redisTemplate;

    public String createRoom(String roomName, String password, User host, Map map) {
        String roomId = UUID.randomUUID().toString();

        RoomInfo room = RoomInfo.builder()
                .roomId(roomId)
                .roomName(roomName)
                .hostUsername(host.getUsername())
                .mapName(map.getMapname())
                .password(password)
                .currentCount(1)
                .maxCount(4)
                .build();
                
        redisTemplate.opsForHash().put("GAME_ROOMS", roomId, room);
        System.out.println("레디스 테스트");
        System.out.println("✅ Redis에 저장된 방: " + redisTemplate.opsForHash().entries("GAME_ROOMS"));

        return roomId;
    }

    public RoomInfo getRoom(String roomId) {
        return (RoomInfo) redisTemplate.opsForHash().get("GAME_ROOMS", roomId);
    }

    public void deleteRoom(String roomId) {
        redisTemplate.opsForHash().delete("GAME_ROOMS", roomId);
    }
}
