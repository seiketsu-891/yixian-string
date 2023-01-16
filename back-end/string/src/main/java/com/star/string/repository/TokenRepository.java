package com.star.string.repository;

import com.star.string.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
    Token  findByUserIdAndContent(String userId, String content);
}
