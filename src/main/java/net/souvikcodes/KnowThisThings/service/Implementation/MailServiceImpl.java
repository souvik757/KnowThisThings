package net.souvikcodes.KnowThisThings.service.Implementation;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.souvikcodes.KnowThisThings.service.IMailService;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService {

    private final JavaMailSender mailSender;

    @Override
    public boolean sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Mail sent successfully !");
        } catch (Exception e) {
            log.warn("Exception occuried while trying to send mail : " + e.getMessage());
            throw new RuntimeException("Failed to send email. Please try again later.");
        }
        return true;
    }
}
