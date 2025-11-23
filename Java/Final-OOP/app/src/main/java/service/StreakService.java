package service;

import java.time.LocalDate;
import java.util.List;

public class StreakService {
    
    public static int calculateStreak(List<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastCheckIn = dates.get(0);
        
        if (!lastCheckIn.equals(today) && !lastCheckIn.equals(yesterday)) return 0;
        
        LocalDate expectedDate = lastCheckIn;
        for (LocalDate date : dates) {
            if (date.equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1); 
            } else {
                break;
            }
        }
        return streak;
    }
}
