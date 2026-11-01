package io.github.pawankm21.rate_limiter_service.algorithms;

import io.github.pawankm21.rate_limiter_service.models.Policy;

import java.util.Date;
import java.util.Map;

public class FixedWindowCounter {
    private Map<String,Long> counter;


    public String getKey(Date timeStamp, Policy policy, long clientId){
        long currentSecond = timeStamp.toInstant().getEpochSecond();
        long windowSeconds = policy.getTimeWindow();
        long windowId = currentSecond / windowSeconds;
        return String.join(":", String.valueOf(clientId), String.valueOf(windowId));

    }

    public boolean isRequestAllowed(long clientId, Date timeStamp){
        // get policy TODO: to implement

        Policy policy  = new Policy();

        String key = getKey(timeStamp, policy, clientId );

        if(counter.getOrDefault(key, 0L)< policy.getMaxRequests()){
            counter.compute(key, (k,val)->{
                if(val==null) return 1L;
                return val+1L;
            });
            return true;
        }
        return false;
    }



}
