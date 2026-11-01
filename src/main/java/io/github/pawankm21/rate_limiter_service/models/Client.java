package io.github.pawankm21.rate_limiter_service.models;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@Repository("")
public class Client {
    private long clientId;
    private ClientType  clientType;
}
