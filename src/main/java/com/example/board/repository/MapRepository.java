package com.example.board.repository;

import com.example.board.domain.Map;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface MapRepository extends JpaRepository<Map, Long>{
    Optional<Map> findByMapname(String mapname);
    
}
