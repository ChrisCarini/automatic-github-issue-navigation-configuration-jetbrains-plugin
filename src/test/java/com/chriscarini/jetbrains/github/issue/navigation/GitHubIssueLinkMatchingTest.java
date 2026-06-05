package com.chriscarini.jetbrains.github.issue.navigation;

import com.chriscarini.jetbrains.github.utils.GitHubUri;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.IssueNavigationConfiguration;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

import org.jetbrains.annotations.NotNull;


public class GitHubIssueLinkMatchingTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final Project project = getProject();

        // configure issue navigation directly for the test repository
        final String remoteUrl = "https://github.com/ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin.git";
        final String cleanedUrl = GitHubUri.parseUrl(remoteUrl).asHttpsFormatUrl();
        GitHubIssueNavigationVcsRepositoryMappingListener.addNewIssueNavigationConfiguration(project, cleanedUrl);
    }

    @Override
    protected void tearDown() throws Exception {
        IssueNavigationConfiguration.getInstance(getProject()).setLinks(List.of());

        super.tearDown();
    }

    /**
     * NOTE: These have to be in a single test in this test class, otherwise things will fail. I tried to break them
     * apart, but I wasn't able to in the timebox I set. For some reason, breaking them apart will cause some weird
     * failure in teardown and/or some of these that _would_ otherwise pass, will fail. Single test method for now.
     */
    public void testGitHubIssueMatches() {
        // contains a solo match
        assertEquals(List.of("(#123)"), getIssueMatches("(#123)"));
        assertEquals(List.of("#123"), getIssueMatches("#123"));
        assertEquals(List.of("GH-123"), getIssueMatches("GH-123"));
        assertEquals(List.of("ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin#123"), getIssueMatches("ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin#123"));

        // contains a match with text before
        assertEquals(List.of("(#123)"), getIssueMatches("cool commit message (#123)"));
        assertEquals(List.of("#123"), getIssueMatches("fixed in #123"));
        assertEquals(List.of("GH-123"), getIssueMatches("fixed in GH-123"));
        assertEquals(List.of("ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin#123"), getIssueMatches("fixed in ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin#123"));

        // contains a match with text after
        assertEquals(List.of("(#123)"), getIssueMatches("(#123) cool commit message"));
        assertEquals(List.of("#123"), getIssueMatches("#123 is where the fix is"));
        assertEquals(List.of("GH-123"), getIssueMatches("GH-123 is where the fix is"));
        assertEquals(List.of("ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin#123"), getIssueMatches("ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin#123 is where the fix is"));

        // contains a match with text before and after
        assertEquals(List.of("(#123)"), getIssueMatches("cool commit message (#123) cool commit message"));
        assertEquals(List.of("#123"), getIssueMatches("fixed in #123 last week"));
        assertEquals(List.of("GH-123"), getIssueMatches("fixed in GH-123 last week"));
        assertEquals(List.of("ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin#123"), getIssueMatches("fixed in ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin#123 last week"));

        // contains no matches (although they are close)
        assertEquals(List.of(), getIssueMatches("cool commit message (#-123)"));
        assertEquals(List.of(), getIssueMatches("will be fixed in #-123"));
        assertEquals(List.of(), getIssueMatches("will be fixed in GITHUB-123"));
        assertEquals(List.of(), getIssueMatches("will be fixed in ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin#-123"));

        // regression tests (aka, bugs identified + fixed)
        assertEquals(List.of("https://github.com/ChrisCarini/github-repo-files-sync/pull/269", "(#123)"), getIssueMatches("Bump github/codeql-action from 3.29.1 to 3.29.2 in /github/workflows (https://github.com/ChrisCarini/github-repo-files-sync/pull/269) (#123)"));
    }

    private List<String> getIssueMatches(@NotNull final String source) {
        return IssueNavigationConfiguration.getInstance(getProject()).findIssueLinks(source).stream().map(m -> m.getRange().substring(source)).toList();
    }
}