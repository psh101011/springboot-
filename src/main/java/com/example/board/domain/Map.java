package com.example.board.domain;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "map")  // 테이블 이름 명시했는지도 확인
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable =false)
    private String mapname;

    @Column(nullable = false)
    private String nQuiz;


}
