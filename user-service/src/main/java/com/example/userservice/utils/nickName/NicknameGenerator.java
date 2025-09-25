package com.example.userservice.utils.nickName;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class NicknameGenerator {

    private final ResourceLoader resourceLoader;
    private final List<String> adjectives = new ArrayList<>();
    private final List<String> nouns = new ArrayList<>();
    private final Random random = new Random();

    public NicknameGenerator(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        loadWords("classpath:adjective.txt", adjectives);
        loadWords("classpath:noun.txt", nouns);
    }

    public String generate(String userName) {
        if (adjectives.isEmpty() && nouns.isEmpty()) {
            return "멋진 친구";
        }
        String adjective = adjectives.get(random.nextInt(adjectives.size()));
        if (random.nextInt(100) < 50) {
            return adjective+userName;
        }
        else {
            String noun = nouns.get(random.nextInt(nouns.size()));
            return adjective + noun;
        }
    }


    private void loadWords(String resourcePath, List<String> words) throws IOException {
        Resource resource = resourceLoader.getResource(resourcePath);
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            words.addAll(reader.lines()
                    .filter(line -> line != null && !line.trim().isEmpty()).collect(Collectors.toList()));
        }
    }
}
