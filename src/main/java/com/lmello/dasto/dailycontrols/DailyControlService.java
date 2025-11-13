package com.lmello.dasto.dailycontrols;

import com.lmello.dasto.budget.Budget;
import com.lmello.dasto.budget.BudgetService;
import com.lmello.dasto.user.User;
import com.lmello.dasto.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class DailyControlService {

    private final DailyControlRepository dailyControlRepository;

    private final BudgetService budgetService;
    private final UserService userService;

    public DailyControlService(
            DailyControlRepository dailyControlRepository,
            BudgetService budgetService,
            UserService userService
    ) {
        this.dailyControlRepository = dailyControlRepository;

        this.budgetService = budgetService;
        this.userService = userService;
    }

    @Transactional
    public DailyControl getOrCreateDailyControl(UUID userId, LocalDate date) {
        User user = userService.getUserById(userId);

        return dailyControlRepository.findByUserAndDate(user, date)
                .orElseGet(() -> createDailyControl(user, date));
    }

    @Transactional
    public DailyControl getOrCreateDailyControl(User user, LocalDate date) {
        return dailyControlRepository.findByUserAndDate(user, date)
                .orElseGet(() -> createDailyControl(user, date));
    }

    @Transactional
    public void recalculateFromDate(UUID userId, LocalDate startDate) {
        User user = userService.getUserById(userId);
        LocalDate currentDate = startDate;
        LocalDate today = LocalDate.now();

        while (!currentDate.isAfter(today)) {
            Optional<DailyControl> controlOpt = dailyControlRepository
                    .findByUserAndDate(user, currentDate);

            if (controlOpt.isPresent()) {
                DailyControl control = controlOpt.get();

                if (!currentDate.equals(startDate)) {
                    BigDecimal carriedOver = getPreviousDayCarryover(user, currentDate);
                    control.setCarriedOver(carriedOver);
                }

                control.recalculate();
                dailyControlRepository.save(control);
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    @Transactional
    public void recalculateFromDate(User user, LocalDate startDate) {
        LocalDate currentDate = startDate;
        LocalDate today = LocalDate.now();

        while (!currentDate.isAfter(today)) {
            Optional<DailyControl> controlOpt = dailyControlRepository
                    .findByUserAndDate(user, currentDate);

            if (controlOpt.isPresent()) {
                DailyControl control = controlOpt.get();

                if (!currentDate.equals(startDate)) {
                    BigDecimal carriedOver = getPreviousDayCarryover(user, currentDate);
                    control.setCarriedOver(carriedOver);
                }

                control.recalculate();
                dailyControlRepository.save(control);
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    private DailyControl createDailyControl(User user, LocalDate date) {
        Budget budget = budgetService.getActiveBudgetAtDate(user, date);

        int daysInMonth = date.lengthOfMonth();
        BigDecimal dailyLimit = budget.getAvailableAmount()
                .divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);

        BigDecimal carriedOver = getPreviousDayCarryover(user, date);

        DailyControl control = new DailyControl();
        control.setDate(date);
        control.setBudget(budget);
        control.setUser(user);
        control.setDailyLimit(dailyLimit);
        control.setCarriedOver(carriedOver);
        control.recalculate();

        return dailyControlRepository.save(control);
    }

    private BigDecimal getPreviousDayCarryover(User user, LocalDate date) {
        LocalDate previousDay = date.minusDays(1);

        return dailyControlRepository.findByUserAndDate(user, previousDay)
                .map(DailyControl::getCarryoverNext)
                .orElse(BigDecimal.ZERO);
    }
}
