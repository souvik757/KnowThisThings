package net.souvikcodes.KnowThisThings.service.MailService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.souvikcodes.KnowThisThings.service.Implementation.MailServiceImpl;

@Disabled
@SpringBootTest
public class MailServiceImplTest {
    @Autowired
    private MailServiceImpl mailService;

    @Test
    public void sendEmailTest() {
        String to = "howai8074@gmail.com";
        String subject = "Test Email";
        String body = "This is a test email.";
        mailService.sendEmail(to, subject, body);
    }
}
