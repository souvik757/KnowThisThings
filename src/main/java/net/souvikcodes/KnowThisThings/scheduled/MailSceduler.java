package net.souvikcodes.KnowThisThings.scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.repository.Implementation.CustomUserRepositoryImpl;
import net.souvikcodes.KnowThisThings.service.Implementation.ExternalApiServiceImpl;
import net.souvikcodes.KnowThisThings.service.Implementation.MailServiceImpl;

@Component
@RequiredArgsConstructor
public class MailSceduler {

    private final MailServiceImpl mailService;
    private final ExternalApiServiceImpl externalApiService;
    private final CustomUserRepositoryImpl userRepository;

    // @Scheduled(cron = "${journal.mail.cron}", zone = "${journal.mail.zone}")
    // @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void sendWeeklySentimentAnalysisReport() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        List<Users> usersForSa = userRepository.findUsersForSA();
        for (Users user : usersForSa) {
            String contentOfJournal = user.getJournalEntries().stream()
                    .filter(journal -> journal.getCreatedAt() != null)
                    .filter(journal -> !journal.getCreatedAt().isBefore(sevenDaysAgo) &&
                            !journal.getCreatedAt().isAfter(now))
                    .map(journal -> journal.getContent()).collect(Collectors.joining(","));
            contentOfJournal = (contentOfJournal.isEmpty() || contentOfJournal.isBlank()) ? "Neutral"
                    : contentOfJournal;
            String sentiment = externalApiService.analyzeSentiment(contentOfJournal);
            String subject = "Your Weekly Sentiment Analysis Report";
            String body = "Hello " + user.getUsername() + ",\n\n" +
                    "Here is your sentiment analysis report for the past week:\n\n" +
                    "You were kind of " + sentiment.toLowerCase() + "\n\n" +
                    "Best regards,\n" +
                    "KnowThisThings Team";
            mailService.sendEmail(user.getEmail(), subject, body);
        }
    }
}
