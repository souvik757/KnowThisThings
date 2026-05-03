package net.souvikcodes.KnowThisThings.service;

public interface IExternalApiService {
    public String getDadJokeOfTheDay();

    public byte[] convertTextToSpeech(String id, String username);
}
