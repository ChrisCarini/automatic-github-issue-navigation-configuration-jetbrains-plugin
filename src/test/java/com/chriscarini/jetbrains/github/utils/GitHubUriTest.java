package com.chriscarini.jetbrains.github.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GitHubUriTest {

    @Test
    public void testParseSsh() {
        // given
        final String url1 = "git@github.com:ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin.git";

        // when
        final GitHubUri uri1 = GitHubUri.parseSsh(url1);

        // then
        assertEquals(uri1.getHost(), "github.com");
        assertEquals(uri1.getOwner(), "ChrisCarini");
        assertEquals(uri1.getRepo(), "automatic-github-issue-navigation-configuration-jetbrains-plugin");

        // given
        final String url2 = "git@company.githubprivate.com:company/repo.git";

        // when
        final GitHubUri uri2 = GitHubUri.parseSsh(url2);

        // then
        assertEquals(uri2.getHost(), "company.githubprivate.com");
        assertEquals(uri2.getOwner(), "company");
        assertEquals(uri2.getRepo(), "repo");

        // given
        final String url3 = "git@company.ghe.com:company/repo.git";

        // when
        final GitHubUri uri3 = GitHubUri.parseSsh(url3);

        // then
        assertEquals(uri3.getHost(), "company.ghe.com");
        assertEquals(uri3.getOwner(), "company");
        assertEquals(uri3.getRepo(), "repo");

        // given
        final String url4 = "ssh://org-123@company.ghe.com/company/repo.git";

        // when
        final GitHubUri uri4 = GitHubUri.parseSsh(url4);

        // then
        assertEquals(uri4.getHost(), "company.ghe.com");
        assertEquals(uri4.getOwner(), "company");
        assertEquals(uri4.getRepo(), "repo");
    }

    @Test
    public void testAsHttpsFormatUrl() {
        assertEquals(
            "https://github.com/ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin",
            new GitHubUri("github.com", "ChrisCarini", "automatic-github-issue-navigation-configuration-jetbrains-plugin").asHttpsFormatUrl()
        );
        assertEquals(
            "https://company.githubprivate.com/company/repo",
            new GitHubUri("company.githubprivate.com", "company", "repo").asHttpsFormatUrl()
        );
        assertEquals(
            "https://company.ghe.com/company/repo",
            new GitHubUri("company.ghe.com", "company", "repo").asHttpsFormatUrl()
        );
        assertEquals(
            "https://company.ghe.com/company/repo",
            new GitHubUri("company.ghe.com", "company", "repo").asHttpsFormatUrl()
        );
    }
}