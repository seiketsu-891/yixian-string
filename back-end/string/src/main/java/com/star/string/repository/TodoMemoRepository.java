package com.star.string.repository;

import com.star.string.entity.TodoMemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoMemoRepository extends JpaRepository<TodoMemo, String> {
    TodoMemo findByUserIdAndYearAndMonthAndDay(String userId, String y, String m, String d);

    List<TodoMemo> findByUserIdAndYearAndMonthAndAllDone(String userID, String y, String m, Boolean allDone);
}
