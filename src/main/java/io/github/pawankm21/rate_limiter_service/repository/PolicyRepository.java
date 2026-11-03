package io.github.pawankm21.rate_limiter_service.repository;

import io.github.pawankm21.rate_limiter_service.dto.ClientType;
import io.github.pawankm21.rate_limiter_service.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findByClientType(ClientType clientType);
}
