package com.sangsangplus.productservice.saga;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SagaStateRepository extends JpaRepository<SagaState, String> {
    List<SagaState> findByStatusAndCreatedAtBefore(SagaStatus status, LocalDateTime dateTime);
}