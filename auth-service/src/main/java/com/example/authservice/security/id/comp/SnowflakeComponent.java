package com.example.authservice.security.id.comp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeComponent {
    private final long epoch = 1704067200000L; // Jan 1, 2024
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long sequenceBits = 12L;

    // Load these from environment variables or config files
    @Value("${app.node.worker-id:1}")
    private long workerId;
    @Value("${app.node.datacenter-id:1}")
    private long datacenterId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards!");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & 4095; // sequenceBits mask
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << 22)
                | (datacenterId << 17)
                | (workerId << 12)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
