package com.star.string.controller;

import com.star.string.entity.Diary;
import com.star.string.req.DiaryContentReq;
import com.star.string.req.DiaryReq;
import com.star.string.resp.CommonResp;
import com.star.string.service.DiaryDialogService;
import com.star.string.service.DiaryService;
import com.star.string.util.ValidatorUtil;
import org.springframework.validation.annotation.ValidationAnnotationUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/user/grid-diary")
@RestController
public class DiaryController {
    @Resource
    private DiaryService diaryService;
    @Resource
    private DiaryDialogService dialogService;

    /**
     * 增加日记
     */
    @PostMapping("/add/{userId}")
    public CommonResp add(@RequestBody DiaryReq diaryReq,  @PathVariable String userId , @RequestParam String date) {
        CommonResp resp = new CommonResp();

        ValidatorUtil.validYMDDate(date);
        String dialogs = diaryReq.getDialogs();
        ValidatorUtil.require(dialogs, "对话内容");


        List<DiaryContentReq> contents= diaryReq.getContent();
        for (DiaryContentReq d : contents) {
            System.out.println(d);
            ValidatorUtil.require(d.getAnswer(), "日记内容");
            ValidatorUtil.require(d.getQuestion(), "日记问题");
            ValidatorUtil.require(d.getOrder(),"条目顺序");
        }

        diaryService.add( contents, dialogs , userId, date );
        return resp;
    }

    /**
     * 列出某用户某天所有日记
     */
    @GetMapping("/list/{userId}")
    public CommonResp<List<Diary>> list(@PathVariable String userId , @RequestParam String date) {
        CommonResp<List<Diary>> resp = new CommonResp();

        ValidatorUtil.validYMDDate(date);
        List<Diary>  diaries = diaryService.list(userId, date);

        resp.setContent(diaries);
        return resp;
    }

    /**
     * 获取用户某月有日记的日期
     */
     @GetMapping("/history/month/{userId}")
    public CommonResp readMonthHistoryDates(@PathVariable String userId, @RequestParam String y, @RequestParam String m){
         CommonResp<List<String>> resp = new CommonResp();
         List<String> dates = dialogService.readMonthHistoryDates(userId, y, m);
         resp.setContent(dates);
         return resp;
     }

    /**
     * 获取用户的当天聊天记录（日记已写完的情况下）
     */
     @GetMapping("/history-dialogs/{userId}")
     public CommonResp getHistoryDialogs(@PathVariable String userId,@RequestParam String date){
         CommonResp<String> resp = new CommonResp();
         ValidatorUtil.validYMDDate(date);
         String  dialogs = dialogService.getDialogs(userId,date);
         resp.setContent(dialogs);
         return resp;
     }

    /**
     * 更新日记内容
     */
     @PostMapping("/ans/update/{userId}")
     public CommonResp updateAnswer(@PathVariable String userId, @RequestBody DiaryContentReq req){
         CommonResp<String> resp = new CommonResp();
         ValidatorUtil.require(req.getId(), "日记条目id");
         ValidatorUtil.require(req.getAnswer(), "日记回答");
         ValidatorUtil.length(req.getAnswer(), "日记回答", 1, 300);

         diaryService.updateAnswer(userId, req);
         return  resp;
     }

    /**
     * 删除日记
     */
     @DeleteMapping("/del/{userId}")
     public  CommonResp delDiaryEntry(@PathVariable String userId, @RequestParam String id, @RequestParam String date){
         CommonResp<String> resp = new CommonResp();
         diaryService.delEntry(userId,id, date);
         return  resp;
     }
}
