package com.star.string.controller;

import com.star.string.entity.Notification;
import com.star.string.resp.CommonResp;
import com.star.string.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/notification")
public class NotificationController {
    private static final Logger LOG = LoggerFactory.getLogger(SmsController.class);
    @Resource
    private NotificationService notificationService;

    /**
     * 根据id查找所有通知
     */
    @GetMapping("/list/{userId}")
    public CommonResp list(@PathVariable String userId) {
        CommonResp<List<Notification>> resp = new CommonResp();
        List<Notification> notiList = notificationService.list(userId);
        resp.setContent(notiList);
        return resp;
    }


    /**
     * 将通知标记为已读
     */
    @PostMapping("/read/{userId}/{id}")
    public CommonResp read(@PathVariable String userId, @PathVariable String id) {
        CommonResp<List<Notification>> resp = new CommonResp();
        notificationService.markAsRead(id, userId);
        return resp;
    }


    /**
     * 将某条通知删除
     */
    @DeleteMapping("/del/{userId}/{id}")
    public CommonResp del(@PathVariable String userId, @PathVariable String id) {
        CommonResp<List<Notification>> resp = new CommonResp();
        notificationService.del(id, userId);
        return resp;
    }


    /**
     * 将某用户所有通知删除
     */
    @DeleteMapping("/delAll/{userId}")
    public CommonResp delAll(@PathVariable String userId) {
        CommonResp<List<Notification>> resp = new CommonResp();
        notificationService.delAll(userId);
        return resp;
    }

    /**
     * 检查是否有未读通知
     */
    @GetMapping("/check/{userId}")
    public CommonResp checkUnread(@PathVariable String userId) {
        CommonResp<Boolean> resp = new CommonResp();
        resp.setContent(notificationService.checkUnread(userId));
        return resp;
    }
}
