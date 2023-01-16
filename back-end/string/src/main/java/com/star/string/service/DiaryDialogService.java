package com.star.string.service;

import com.star.string.entity.DiaryDialog;
import com.star.string.repository.DiaryDialogRepository;
import com.star.string.util.TimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DiaryDialogService {
    @Resource
    private DiaryDialogRepository repository;

    /**
     * 增加一条对话备份
     */
    public void add(String dialogs, String userId, String date) {
        DiaryDialog diaryDialog = new DiaryDialog();
        diaryDialog.setId(String.valueOf(UUID.randomUUID()));
        diaryDialog.setUserId(userId);

        String[] ymd = TimeUtil.spitYMD(date);
        diaryDialog.setYear(ymd[0]);
        diaryDialog.setMonth(ymd[1]);
        diaryDialog.setDate(ymd[2]);

        diaryDialog.setCreateTime(new Date());
        diaryDialog.setDialogs(dialogs);

         this.save(diaryDialog);
    }


    /**
     * 保存条目到数据库
     */
    private  DiaryDialog save(DiaryDialog diaryDialog){
        return   repository.save(diaryDialog);
    }

    /**
     * 读取某年某月在数据库中有记录的日期
     */
    public List<String> readMonthHistoryDates(String userId, String y, String m) {
        List<String> dates = new ArrayList<>();
       List<DiaryDialog>  dialogs = repository.findByUserIdAndYearAndMonth(userId, y,m);
       for (DiaryDialog d : dialogs){
           dates.add(d.getDate());
       }
       return dates;
    }

    /**
     * 根据id和日期读取历史对话信息
     */
    public String getDialogs(String userId, String date) {
        System.out.println(userId);
        System.out.println(date);
        String[] ymd = TimeUtil.spitYMD(date);
        System.out.println(ymd.toString());
        DiaryDialog dialogDb = repository.findByUserIdAndYearAndMonthAndDate(userId, ymd[0], ymd[1], ymd[2]);
        if(dialogDb == null){
            return "[]";
        }
        return dialogDb.getDialogs();
    }

    /**
     * 根据id和日期删除对话记录
     */
    public void delByUserIdAndDate(String userId, String date) {
        String[] ymd = TimeUtil. spitYMD(date);
        repository.deleteByUserIdAndYearAndMonthAndDate(userId, ymd[0], ymd[1], ymd[2]);
    }
}
