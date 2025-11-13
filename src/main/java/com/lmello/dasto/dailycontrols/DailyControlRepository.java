package com.lmello.dasto.dailycontrols;

import com.lmello.dasto.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyControlRepository extends JpaRepository<DailyControl, Long> {

    Optional<DailyControl> findByUserAndDate(User user, LocalDate localDate);
}
