package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entities.ScheduleRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRangeRepository extends JpaRepository<ScheduleRange,String> {
}
