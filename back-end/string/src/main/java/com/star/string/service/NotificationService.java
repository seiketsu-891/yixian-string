package com.star.string.service;

import com.star.string.entity.Notification;
import com.star.string.enums.NotificationTypeEnum;
import com.star.string.exception.BusinessException;
import com.star.string.exception.BusinessExceptionCode;
import com.star.string.repository.NotificationRepository;
import com.star.string.req.NotificationReq;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.net.DatagramSocket;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class NotificationService {
    @Resource
    private NotificationRepository notificationRepository;


    /**
     * 根据用户id查找所有通知
     */
    public List<Notification> list(String userId){
        return notificationRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 将通知标记为已读
     */
    public void markAsRead(String id, String userId) {
      Integer marked=  notificationRepository.updateByIdAndUserId(id, userId);

      if(marked == 0){
          throw new BusinessException(BusinessExceptionCode.NOTIFICATION_NOT_EXISTS);
      }
    }

    /**
     * 删除某条通知
     */
    public void del(String id, String userId) {
       Integer deleted=   notificationRepository.deleteByIdAndUserId(id, userId);
        if (deleted == 0) {
            throw new BusinessException(BusinessExceptionCode.NOTIFICATION_NOT_EXISTS);
        }
    }

    /**
     * 删除所有通知
     */
    public void delAll(String userId) {
        notificationRepository.deleteAllByUserId(userId);
    }

    /**
     * 检查某用户是否有未读通知
     */
    public boolean checkUnread(String userId) {
     return notificationRepository.existsByUserIdAndStatus(userId, false);
    }

    /**
     * 为新用户生成欢迎信息
     */
    public void welcomeMessage(String userId) {
        final String WELCOME_MSG_TITLE =  "欢迎加入易弦";
        final String WELCOME_MSG_CONTENT = "感谢您选择易弦，希望易弦能帮助您获得自我提升。";
        this.generateMessage(userId, NotificationTypeEnum.WELCOME.getCode(), WELCOME_MSG_TITLE, WELCOME_MSG_CONTENT);
    }

    /**
     * 根据指定信息生成通知对象
     */
    private void generateMessage(String userId,String type, String title, String content ){
        Notification notification = new Notification();
        notification.setId(String.valueOf(UUID.randomUUID()));
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setStatus(false);
        notification.setType(type);
        notification.setContent(content);
        notification.setCreateTime(new Date());
        notificationRepository.save(notification);
    }

}
