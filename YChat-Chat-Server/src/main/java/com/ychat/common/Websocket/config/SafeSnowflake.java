package com.ychat.common.Websocket.Config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SafeSnowflake {

    private final long workerId = 3;

    private final long datacenterId = 6;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private final long twepoch = 1288834974657L;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long sequenceBits = 12L;

    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    public SafeSnowflake() {
    }

    public synchronized int nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & (-1L ^ (-1L << sequenceBits));
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        // 将生成的 ID 映射到 Integer 范围，使用安全算法避免冲突
        long id = ((timestamp - twepoch << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence);
        return (int) ((id & Integer.MAX_VALUE) + sequence); // 使用 & 运算确保范围内并加上序列号以降低冲突概率
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
