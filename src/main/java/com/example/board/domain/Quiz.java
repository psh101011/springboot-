package com.example.board.domain;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz")  // 테이블 이름 명시했는지도 확인
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", nullable = false)
    private Map map;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String hint;

    @Column(nullable = false)
    private String answer;

}
