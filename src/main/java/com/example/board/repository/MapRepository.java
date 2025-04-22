package com.example.board.repository;

import com.example.board.domain.Map;
import com.example.board.domain.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface MapRepository extends JpaRepository<Map, Long>{
    Optional<Map> findByMapname(String mapname);
    List<Map> findAllByUser(User user);
    Page<Map> findByUser(User user, Pageable pageable);
    

}
