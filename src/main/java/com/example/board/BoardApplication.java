package com.example.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoardApplication {

	public static void main(String[] args) {
		System.out.println("✅ 애플리케이션 실행됨!");
		SpringApplication.run(BoardApplication.class, args);
	}

}
