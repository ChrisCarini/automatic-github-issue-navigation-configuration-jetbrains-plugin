package com.chriscarini.jetbrains.github.utils;

import org.jetbrains.annotations.NotNull;

public class GitHubUri {
    private final String host;
    private final String owner;
    private final String repo;

    protected GitHubUri(@NotNull final String host, @NotNull final String owner, @NotNull final String repo) {
        this.host = host;
        this.owner = owner;
        this.repo = repo;
    }

    public static GitHubUri parseUrl(@NotNull final String url) {
        if (url.startsWith("https://")) {
            return parseHttps(url);
        }
        return parseSsh(url);
    }

    private static GitHubUri parseHttps(@NotNull final String url) {
        int beginDomain = url.indexOf("://");
        int beginOrg = url.indexOf("/", beginDomain + 3);
        int beginRepo = url.indexOf("/", beginOrg + 1);
        int dotGitLoc = url.contains(".git") ? url.lastIndexOf(".git") : url.length();

        final String host = url.substring(beginDomain + 3, beginOrg);
        final String owner = url.substring(beginOrg + 1, beginRepo);
        final String repo = url.substring(beginRepo + 1, dotGitLoc);
        return new GitHubUri(host, owner, repo);
    }

    private static GitHubUri parseSsh(@NotNull final String url) {
        int beginDomain = url.indexOf("@");
        int beginOrg = url.indexOf(":", beginDomain + 1);
        if (beginOrg == -1) {
            beginOrg = url.indexOf("/", beginDomain + 1);
        }
        int beginRepo = url.indexOf("/", beginOrg + 1);
        int dotGitLoc = url.lastIndexOf(".git");

        final String host = url.substring(beginDomain + 1, beginOrg);
        final String owner = url.substring(beginOrg + 1, beginRepo);
        final String repo = url.substring(beginRepo + 1, dotGitLoc);
        return new GitHubUri(host, owner, repo);
    }

    public String getHost() {
        return host;
    }

    public String getOwner() {
        return owner;
    }

    public String getRepo() {
        return repo;
    }

    @NotNull
    public String asHttpsFormatUrl() {
        return String.format("https://%s/%s/%s", this.getHost(), this.getOwner(), this.getRepo());
    }
}
