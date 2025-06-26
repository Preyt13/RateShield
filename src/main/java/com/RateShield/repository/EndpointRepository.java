package com.RateShield.repository;

import com.RateShield.model.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EndpointRepository extends JpaRepository<Endpoint, UUID> {

    List<Endpoint> findByServiceId(UUID serviceId);

    Optional<Endpoint> findByServiceIdAndPathAndMethod(UUID serviceId, String path, String method);

    List<Endpoint> findByGroupLabel(String groupLabel); // For creating groups
}
