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
import java.time.temporal.ChronoUnit;

@Slf4j
@RestController
@RequestMapping("/time")
public class TimeController {

    /**
     * 获取当前时间
     * @return
     */
    @RequestMapping("/getTimeNow")
    public String getTimeNow() {
        return "当前时间：" + ZonedDateTime.now().toEpochSecond();
    }

    /**
     * 获取十天前的时间
     * @return
     */
    @RequestMapping("/getTimeTenDaysAgo")
    public String getTimeTenDaysAgo() {
        ZonedDateTime tenDaysAgo = ZonedDateTime.now().minusDays(10);
        return "十天前的时间：" + tenDaysAgo.toEpochSecond();
    }

    /**
     * 获取十天后的时间
     * @return
     */
    @RequestMapping("/getTimeTenDaysLater")
    public String getTimeTenDaysLater() {
        ZonedDateTime tenDaysLater = ZonedDateTime.now().plus(10, ChronoUnit.DAYS);
        return "十天后的时间：" + tenDaysLater.toEpochSecond();
    }

    /**
     * 将时间戳转换为日期时间
     * @param timestamp
     * @return
     */
    @RequestMapping("/convertTimestampToDateTime")
    public String convertTimestampToDateTime(@RequestParam long timestamp) {
        // 将时间戳转换为 LocalDateTime
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        // 格式化日期时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "转换后的日期时间：" + dateTime.format(formatter);
    }

}
