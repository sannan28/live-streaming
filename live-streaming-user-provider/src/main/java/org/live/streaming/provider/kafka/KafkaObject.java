package org.live.streaming.provider.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaObject {
    private String code;
    private String userId;
}
