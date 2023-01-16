package com.star.string.controller;

import com.star.string.entity.Todo;
import com.star.string.req.TodoReq;
import com.star.string.resp.CommonResp;
import com.star.string.service.TodoMemoService;
import com.star.string.service.TodoService;
import com.star.string.util.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
@RequestMapping("/user/todos")
@RestController
public class TodoController {
    private static final Logger LOG = LoggerFactory.getLogger(SmsController.class);
    @Resource
    private TodoService todoService;
    @Resource
    private TodoMemoService memoService;

    /**
     * 获取用户某日所有todo
     */
    @GetMapping("/list/{userId}")
    public CommonResp list(@PathVariable String userId, @RequestParam String date){
        ValidatorUtil.require(userId, "用户id");
        ValidatorUtil.validYMDDate(date);

        CommonResp<List<Todo>> resp = new CommonResp();
        List<Todo> todoList = todoService.list(userId,date);
        resp.setContent(todoList);
        return resp;
    }

    /**
     * 增加todo
     */
    @PostMapping("/add/{userId}")
    public CommonResp add(@RequestBody TodoReq req, @PathVariable String userId){
        CommonResp<Todo> resp = new CommonResp();

        ValidatorUtil.require(req.getDescription(), "描述");
        ValidatorUtil.validYMDDate(req.getDate());
        ValidatorUtil.validTodoPriority(req.getPriority());

        todoService.add(req, userId);
        return resp;
    }

    /**
     * 删除待办
     */
    @DeleteMapping("/del/{userId}")
    public CommonResp del(@PathVariable String userId, @RequestParam String id , @RequestParam String y, @RequestParam String m, @RequestParam String d){
        CommonResp<Todo> resp = new CommonResp();
        todoService.del(id ,userId, y, m, d);
        return  resp;
    }

    /**
     *  将待办变成完成状态
     */
     @PostMapping("/done/{userId}")
    public CommonResp markDone(@PathVariable String userId , @RequestParam String id,  @RequestParam String y, @RequestParam String m, @RequestParam String d){
         CommonResp<Todo> resp = new CommonResp();
         todoService.markDone(id, userId, y, m, d);
         return  resp;
     }


    /**
     *  修改待办基本信息
     */
    @PostMapping("/edit/{userId}")
     public CommonResp edit(@RequestBody TodoReq req, @PathVariable String userId){
         CommonResp<Todo> resp = new CommonResp();
         ValidatorUtil.require(req.getId(), "待办id");
         ValidatorUtil.require(req.getDescription(), "描述");
         if(req.getPriority() != null){
             ValidatorUtil.validTodoPriority(req.getPriority());
         }
         todoService.update(req, userId);
         return  resp;
     }

    /**
     * 调整todo顺序
     */
    @PostMapping("/reorder/{userId}/{destIndex}")
    public CommonResp reorder(@PathVariable String userId, @PathVariable int destIndex,  @RequestBody TodoReq req ){
        LOG.info("收到调整todo顺序请求:目标位置为：{}，数据为： {}", destIndex, req);
        CommonResp<Todo> resp = new CommonResp();
        ValidatorUtil.validYMDDate(req.getDate());
        ValidatorUtil.require(req.getId(), "待办id");
        ValidatorUtil.validTodoPriority(req.getPriority());

        todoService.reorder(userId,destIndex, req);

        return  resp;
    }

    /**
     * 读取某年某月有未完成待办的日期
     */
    @GetMapping("/memo/month/{userId}")
    public CommonResp readMonthHasUndoneDates(@PathVariable String userId,@RequestParam  String y, @RequestParam String m){
        CommonResp<List<String>> resp = new CommonResp();
        List<String> dates = memoService.getMonthHasUndoneDates(userId, y,m);
        resp.setContent(dates);
        return  resp;
    }
}

