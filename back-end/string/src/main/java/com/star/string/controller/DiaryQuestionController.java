package com.star.string.controller;

import com.star.string.entity.DiaryQuestion;
import com.star.string.resp.CommonResp;
import com.star.string.service.DiaryQuestionService;
import com.star.string.util.ValidatorUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/grid-diary/ques")
public class DiaryQuestionController {
    @Resource
    private DiaryQuestionService service;

    /**
     * 增加日记问题
     */
    @PostMapping("/add/{userId}")
    public CommonResp add(@RequestBody DiaryQuestion diaryQues, @PathVariable String userId){
        CommonResp resp = new CommonResp();

        ValidatorUtil.require(diaryQues.getDescription(), "日记问题内容");

        service.add(diaryQues, userId);
        return resp;
    }

    /**
     * 列出某用户所有的日记问题
     */
    @GetMapping("/list/{userId}")
    public CommonResp add( @PathVariable String userId){
        CommonResp<List<DiaryQuestion>> resp = new CommonResp();
        List<DiaryQuestion> ques =   service.listInUseQues( userId);
        resp.setContent(ques);
        return resp;
    }

    /**
     * 删除日记问题
     */
    @DeleteMapping ("/del/{userId}")
    public CommonResp del( @PathVariable String userId, @RequestParam String id){
        CommonResp resp = new CommonResp();

        service.setInUse(userId, id , false);
        return resp;
    }
}
