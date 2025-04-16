package com.example.board.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user")  // 테이블 이름 명시했는지도 확인
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role = "USER";
}