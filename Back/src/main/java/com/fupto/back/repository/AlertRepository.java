package com.fupto.back.repository;

import com.fupto.back.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository  extends JpaRepository<Alert, Long> {
}
