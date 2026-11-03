package io.github.pawankm21.rate_limiter_service.repository;

import io.github.pawankm21.rate_limiter_service.entity.Client;
import io.github.pawankm21.rate_limiter_service.dto.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByClientType(ClientType clientType);

}