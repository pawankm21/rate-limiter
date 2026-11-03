package io.github.pawankm21.rate_limiter_service.entity;


import io.github.pawankm21.rate_limiter_service.dto.ClientType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client {
    @Id
    private long clientId;
    @Enumerated(EnumType.STRING)
    private ClientType clientType;
}
