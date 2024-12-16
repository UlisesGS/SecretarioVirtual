package com.SecretarioVirtual.main.repositories;

import com.SecretarioVirtual.main.entities.ScheduleRange;
import com.SecretarioVirtual.main.entities.enums.Days;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRangeRepository extends JpaRepository<ScheduleRange,String> {

    List<ScheduleRange> findScheduleRangeByDay(Days day);
}
