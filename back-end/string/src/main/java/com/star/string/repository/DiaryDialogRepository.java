package com.star.string.repository;

import com.star.string.entity.DiaryDialog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DiaryDialogRepository extends JpaRepository<DiaryDialog, String> {
    List<DiaryDialog>  findByUserIdAndYearAndMonth(String userId,String y, String m);

    DiaryDialog findByUserIdAndYearAndMonthAndDate(String userId, String y, String m, String d);

    void deleteByUserIdAndYearAndMonthAndDate(String userId, String y, String m, String d );
}
