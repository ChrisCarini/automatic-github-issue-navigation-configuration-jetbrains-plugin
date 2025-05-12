package com.chriscarini.jetbrains.github.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitHubUtils {

    public static Stream<String> knownGitHubDomains() {
        return Stream.of(
            "github.com",
            "ghe.com",
            "githubprivate.com"
        );
    }

    public static boolean isKnownGitHubDomain(@NotNull final String url) {
        final List<String> result = knownGitHubDomains()
            .filter(url::contains)
            .toList();

        return !result.isEmpty();
    }
}
