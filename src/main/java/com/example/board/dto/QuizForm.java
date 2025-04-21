package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QuizForm {
    private String question;
    private String hint;
    private String answer;
}
