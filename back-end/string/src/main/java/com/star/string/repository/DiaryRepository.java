package com.star.string.repository;

import com.star.string.entity.Diary;
import com.star.string.entity.DiaryQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, String> {
    List<Diary> findByUserIdAndDateOrderByContentOrderAsc(String userId, String date);

    Integer deleteByIdAndUserId(String id, String userId);

    Diary findByIdAndUserId(String id, String userId);

    Integer countByUserIdAndDate(String userId, String date);
}
