package com.star.string.controller;

import com.star.string.entity.TimeEntry;
import com.star.string.entity.TimeEntryTmp;
import com.star.string.entity.Todo;
import com.star.string.req.TimeEntryReq;
import com.star.string.resp.CommonResp;
import com.star.string.resp.TimeEntryCatDurResp;
import com.star.string.service.TimeEntryService;
import com.star.string.util.ValidatorUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/timeentry")
public class TimeEntryController {
    @Resource
    private TimeEntryService timeEntryService;

    /**
     * 新增时间条目：手动添加开始结束时间 / 自动计时（仅添加开始时间）
     */
    @PostMapping("/add/{userId}")
    public CommonResp add(@RequestBody TimeEntryReq req, @PathVariable String userId,  @RequestParam Boolean manual){
        CommonResp<TimeEntryTmp> resp = new CommonResp();

        if(manual){
           // 手动添加的话，验证开始和结束时间
          ValidatorUtil.validStartAndEnd(req.getStart(), req.getEnd());
          ValidatorUtil.isBeforeNow(req.getEnd());
        }else{
            // 自动添加只需验证开始时间
            ValidatorUtil.isBeforeNow(req.getStart());
        }

        TimeEntryTmp saved = timeEntryService.add(req, userId, manual);
        resp.setContent(saved);
        return resp;
    }

    /**
     * 删除时间条目
     */
    @DeleteMapping("/del/{userId}")
    public CommonResp del(@PathVariable String userId, @RequestParam String id ){
        CommonResp<Todo> resp = new CommonResp();
        timeEntryService.del(id ,userId);
        return  resp;
    }

    /**
     * 结束一个时间条目的计时
     */
    @PostMapping("/timer-stop/{userId}")
    public CommonResp timerStop( @PathVariable String userId, @RequestParam String id, @RequestParam Long end){
        CommonResp<TimeEntryTmp> resp = new CommonResp();
        ValidatorUtil.isBeforeNow(end);

        timeEntryService.endTimer(userId, id,end);
        return resp;
    }

    /**
     * 查找用户是否有正在计时的时间条目
     */
    @GetMapping("/read/{userId}")
    public CommonResp readRunningEntry(@PathVariable String userId){
        CommonResp<TimeEntryTmp> resp = new CommonResp();
        TimeEntryTmp tmp = timeEntryService.readRunningEntry(userId);
        resp.setContent(tmp);
        return resp;
    }


    /**
     * 更新时间条目
     */
    @PostMapping("/update/{userId}")
    public CommonResp update(@PathVariable String userId, @RequestBody TimeEntryReq req){
        CommonResp<TimeEntryTmp> resp = new CommonResp();

        ValidatorUtil.require(req.getId(), "条目id");

        timeEntryService.update(userId, req);
        return resp;
    }

    /**
     * 列出分类和对应的累积时间
     */
     @GetMapping("/durs/{userId}")
    public CommonResp listDur(@PathVariable String userId) {
        CommonResp<List<TimeEntryCatDurResp>> resp = new CommonResp();
        List<TimeEntryCatDurResp> catDurs =  timeEntryService.listDur(userId);
         System.out.println(catDurs);
        resp.setContent(catDurs);
        return resp;
    }

    /**
     * 列出指定时间段内的时间条目
     */
    @GetMapping("/list/{userId}")
    public CommonResp list(@PathVariable String userId, @RequestParam String start, @RequestParam String end,  @RequestParam String tz, @RequestParam Boolean all){
        CommonResp<List<TimeEntry>> resp = new CommonResp();

        ValidatorUtil.validYMDDate(start);
        ValidatorUtil.validYMDDate(end);

        List<TimeEntry> entries = timeEntryService.listWeeklyEntries(userId, start, end, tz, all);
        resp.setContent(entries);
        return resp;
    }
}
