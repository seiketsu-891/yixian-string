package com.star.string.repository;

import com.star.string.entity.TimeEntryCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeEntryCategoryRepository extends JpaRepository<TimeEntryCategory, String> {
      List<TimeEntryCategory> findAllByUserIdOrderByCreateTimeAsc(String userId);
      boolean existsByUserIdAndName(String userId, String username);
      TimeEntryCategory findByIdAndUserId(String id, String userId);

      Integer deleteByIdAndUserId(String id, String userId);

      boolean existsByIdAndUserId(String id, String userid);



}
