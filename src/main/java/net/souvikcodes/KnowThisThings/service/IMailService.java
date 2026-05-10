package net.souvikcodes.KnowThisThings.service;

public interface IMailService {
    public boolean sendEmail(String to, String subject, String body);
}
