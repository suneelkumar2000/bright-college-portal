package com.project.college_portal;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
//@ConditionalOnProperty(value = "email", havingValue = "true", matchIfMissing = false)
public class MailScheduler {
	// 1000 * 60 * 2 = 2 mins
	@Scheduled(fixedDelayString = "12000")
	public void notifyMail() {
		LocalDateTime localDate = LocalDateTime.now();
	}

	@Scheduled(cron = "0 */1 * * * *")
	public void notifyMailCron() {
		LocalDateTime localDate = LocalDateTime.now();
	}

}
