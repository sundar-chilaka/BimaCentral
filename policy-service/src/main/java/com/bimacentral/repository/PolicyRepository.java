package com.bimacentral.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bimacentral.entity.Policy;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

	Optional<Policy> findByCustomerId(Long customerId);
}
