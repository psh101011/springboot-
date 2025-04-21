package com.example.board.repository;

import com.example.board.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import com.example.board.domain.Map;

public interface QuizRepository extends JpaRepository<Quiz, Long>{
    List<Quiz> findByMap(Map map);
}
