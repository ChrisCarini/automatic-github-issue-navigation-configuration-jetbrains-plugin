package com.chriscarini.jetbrains.github.utils;

import org.junit.Test;

public class GitHubUtilsTest {

    @Test
    public void testIsKnownGitHubDomain() {
        assert GitHubUtils.isKnownGitHubDomain("https://github.com/ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin");
        assert GitHubUtils.isKnownGitHubDomain("git@github.com:ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin.git");

        assert GitHubUtils.isKnownGitHubDomain("https://company.githubprivate.com/company/repo");
        assert GitHubUtils.isKnownGitHubDomain("git@company.githubprivate.com:company/repo.git");

        assert GitHubUtils.isKnownGitHubDomain("https://company.ghe.com/company/repo");
        assert GitHubUtils.isKnownGitHubDomain("git@company.ghe.com:company/repo.git");
        assert GitHubUtils.isKnownGitHubDomain("ssh://org-123@company.ghe.com/company/repo.git");
    }
}