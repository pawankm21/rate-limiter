package io.github.pawankm21.rate_limiter_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class BucketState {
    Date timeStamp;
    double tokens;

    public long getEpochMillis(){
        return timeStamp.toInstant().toEpochMilli();
    }
}
