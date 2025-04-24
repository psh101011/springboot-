package com.example.board.dto;

import java.io.Serializable;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInfo implements Serializable{
    private String roomId;
    private String roomName;
    private String hostUsername;
    private String mapName;
    private String password;  // null 가능
    private int currentCount;
    private int maxCount;
}
