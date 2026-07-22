package com.connectit.core.repository;

import com.connectit.core.model.TicketScreenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketScreenshotRepository extends JpaRepository<TicketScreenshot, Long> {
    List<TicketScreenshot> findByTicketId(Long ticketId);
    List<TicketScreenshot> findByTicketNumber(String ticketNumber);
}
