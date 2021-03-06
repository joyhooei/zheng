package com.zheng.cms.job.jms;

import com.zheng.cms.dao.model.User;
import com.zheng.cms.service.UserService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MQ消费者
 * Created by ZhangShuzheng on 2016/11/24.
 */
public class defaultQueueMessageListener implements MessageListener {

    private static Logger _log = LoggerFactory.getLogger(defaultQueueMessageListener.class);

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    UserService userService;

    public void onMessage(final Message message) {
        // 使用线程池多线程处理
        threadPoolTaskExecutor.execute(new Runnable() {
            public void run() {
                TextMessage textMessage = (TextMessage) message;
                try {
                    String text = textMessage.getText();
                    JSONObject json = JSONObject.fromObject(text);
                    User user = (User) JSONObject.toBean(json, User.class);
                    if (user.getUsername().equals("1")) {
                        _log.info("消费开始时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    }
                    if (user.getUsername().equals("1000")) {
                        _log.info("消费结束时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    }
                    userService.getMapper().insertSelective(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
