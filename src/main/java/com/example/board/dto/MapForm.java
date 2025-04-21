package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MapForm {
    private String mapName;
    private List<QuizForm> quizzes = new ArrayList<>();
}
