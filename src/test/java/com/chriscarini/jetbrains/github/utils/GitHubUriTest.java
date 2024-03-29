package com.chriscarini.jetbrains.github.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GitHubUriTest {

    @Test
    public void testParseUrl() {
        // given - ssh
        final String url1 = "git@github.com:ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin.git";

        // when
        final GitHubUri uri1 = GitHubUri.parseUrl(url1);

        // then
        assertEquals(uri1.getHost(), "github.com");
        assertEquals(uri1.getOwner(), "ChrisCarini");
        assertEquals(uri1.getRepo(), "automatic-github-issue-navigation-configuration-jetbrains-plugin");

        // given - `*.githubprivate.com` domain
        final String url2 = "git@company.githubprivate.com:company/repo.git";

        // when
        final GitHubUri uri2 = GitHubUri.parseUrl(url2);

        // then
        assertEquals(uri2.getHost(), "company.githubprivate.com");
        assertEquals(uri2.getOwner(), "company");
        assertEquals(uri2.getRepo(), "repo");

        // given - `*.ghe.com` domain
        final String url3 = "git@company.ghe.com:company/repo.git";

        // when
        final GitHubUri uri3 = GitHubUri.parseUrl(url3);

        // then
        assertEquals(uri3.getHost(), "company.ghe.com");
        assertEquals(uri3.getOwner(), "company");
        assertEquals(uri3.getRepo(), "repo");

        // given - with org (non-`git` username)
        final String url4 = "ssh://org-123@company.ghe.com/company/repo.git";

        // when
        final GitHubUri uri4 = GitHubUri.parseUrl(url4);

        // then
        assertEquals(uri4.getHost(), "company.ghe.com");
        assertEquals(uri4.getOwner(), "company");
        assertEquals(uri4.getRepo(), "repo");

        // given - with `.git` suffix
        final String url5 = "https://github.com/ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin.git";

        // when
        final GitHubUri uri5 = GitHubUri.parseUrl(url5);

        // then
        assertEquals(uri5.getHost(), "github.com");
        assertEquals(uri5.getOwner(), "ChrisCarini");
        assertEquals(uri5.getRepo(), "automatic-github-issue-navigation-configuration-jetbrains-plugin");

        // given - without `.git` suffix
        final String url6 = "https://github.com/ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin";

        // when
        final GitHubUri uri6 = GitHubUri.parseUrl(url6);

        // then
        assertEquals(uri6.getHost(), "github.com");
        assertEquals(uri6.getOwner(), "ChrisCarini");
        assertEquals(uri6.getRepo(), "automatic-github-issue-navigation-configuration-jetbrains-plugin");

        // given - with org (non-`git` username)
        final String url7 = "ssh://org-123@github.com/org/repo.git";

        // when
        final GitHubUri uri7 = GitHubUri.parseUrl(url7);

        // then
        assertEquals(uri7.getHost(), "github.com");
        assertEquals(uri7.getOwner(), "org");
        assertEquals(uri7.getRepo(), "repo");
    }

    @Test
    public void testParseHttps() {
        // given
        for (String uri : new String[]{
                "https://github.com/ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin.git",
                "https://github.com/ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin",
                "https://company.githubprivate.com/company/repo",
                "https://company.ghe.com/company/repo",
                "https://github.com/company/repo",
        }) {
            // when
            final GitHubUri result = GitHubUri.parseHttps(uri);

            // then
            assertNotNull(result);
            assertNotNull(result.getHost());
            assertNotNull(result.getOwner());
            assertNotNull(result.getRepo());
        }
    }

    @Test
    public void testParseSsh() {
        // given
        for (String uri : new String[]{
                "git@github.com:ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin.git",
                "git@github.com:ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin",
                "git@company.githubprivate.com:company/repo.git",
                "git@company.githubprivate.com:company/repo",
                "git@company.ghe.com:company/repo.git",
                "git@company.ghe.com:company/repo",
                "ssh://org-123@github.com/company/repo.git",
                "ssh://org-123@github.com/company/repo",
        }) {
            // when
            final GitHubUri result = GitHubUri.parseSsh(uri);

            // then
            assertNotNull(result);
            assertNotNull(result.getHost());
            assertNotNull(result.getOwner());
            assertNotNull(result.getRepo());
        }
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