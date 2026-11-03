package io.github.pawankm21.rate_limiter_service.entity;

import io.github.pawankm21.rate_limiter_service.dto.ClientType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "policies")
public class Policy {
   @Id
   @GeneratedValue(strategy =  GenerationType.IDENTITY)
   private long policyId;
   @Enumerated(EnumType.STRING)
   private ClientType clientType;
   private long maxRequests;
   private long timeWindow;
}
