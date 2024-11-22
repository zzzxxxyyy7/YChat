package com.ychat.common.TestApi.Controller.Time;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/time")
public class TimeController {

    /**
     * 获取当前时间（秒级时间戳）
     * @return
     */
    @RequestMapping("/getTimeNow")
    public String getTimeNow() {
        return "当前时间（秒级时间戳）：" + ZonedDateTime.now().toEpochSecond();
    }

    /**
     * 获取当前时间（毫秒级时间戳）
     * @return
     */
    @RequestMapping("/getTimeNowMillis")
    public String getTimeNowMillis() {
        return "当前时间（毫秒级时间戳）：" + System.currentTimeMillis();
    }

    /**
     * 获取十天前的时间（毫秒级时间戳）
     * @return
     */
    @RequestMapping("/getTimeTenDaysAgoMillis")
    public String getTimeTenDaysAgoMillis() {
        ZonedDateTime tenDaysAgo = ZonedDateTime.now().minusDays(10);
        return "十天前的时间（毫秒级时间戳）：" + tenDaysAgo.toInstant().toEpochMilli();
    }

    /**
     * 获取十天后的时间（毫秒级时间戳）
     * @return
     */
    @RequestMapping("/getTimeTenDaysLaterMillis")
    public String getTimeTenDaysLaterMillis() {
        ZonedDateTime tenDaysLater = ZonedDateTime.now().plusDays(10);
        return "十天后的时间（毫秒级时间戳）：" + tenDaysLater.toInstant().toEpochMilli();
    }

    /**
     * 将时间戳（秒级或毫秒级）转换为日期时间
     * @param timestamp
     * @return
     */
    @RequestMapping("/convertTimestampToDateTime")
    public String convertTimestampToDateTime(@RequestParam long timestamp) {
        LocalDateTime dateTime;

        // 判断时间戳是秒级还是毫秒级，并转换为 LocalDateTime
        if (String.valueOf(timestamp).length() == 10) {
            dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        } else if (String.valueOf(timestamp).length() == 13) {
            dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        } else {
            return "无效的时间戳长度：" + timestamp;
        }

        // 格式化日期时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return "转换后的日期时间：" + dateTime.format(formatter);
    }
}
