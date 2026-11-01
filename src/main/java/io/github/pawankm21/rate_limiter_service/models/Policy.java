package io.github.pawankm21.rate_limiter_service.models;

import lombok.Getter;

@Getter

public class Policy {
   private long policyId;
   private ClientType clientType;
   private long maxRequests;
   private long timeWindow;
}
