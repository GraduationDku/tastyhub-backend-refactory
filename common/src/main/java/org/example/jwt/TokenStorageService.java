package org.example.jwt;

public interface TokenStorageService {

    void saveData(String key, String value, long expiration);

    String getData(String key);

    Boolean deleteData(String key);
}
