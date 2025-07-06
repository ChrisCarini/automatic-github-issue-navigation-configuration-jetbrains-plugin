package com.chriscarini.jetbrains.github.issue.navigation;


import com.intellij.openapi.vcs.IssueNavigationConfiguration;
import com.intellij.openapi.vcs.IssueNavigationLink;
import com.intellij.testFramework.LightPlatformTestCase;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;


public class TestGitHubIssueNavigationVcsRepositoryMappingListenerTest extends LightPlatformTestCase {

    public void testExistsInIssueNavConfig() {
        // given
        final String expectedUrl = "MY_FAKE_URL";
        final String unexpectedUrl = "MY_UNEXPECTED_FAKE_URL";
        final List<IssueNavigationLink> myIssueNavigationLinks = GitHubIssueNavigationVcsRepositoryMappingListener.getIssueNavigationLinks(expectedUrl);

        IssueNavigationConfiguration.getInstance(getProject()).setLinks(myIssueNavigationLinks);

        // expect
        assert GitHubIssueNavigationVcsRepositoryMappingListener.existsInIssueNavConfig(getProject(), expectedUrl);
        assert !GitHubIssueNavigationVcsRepositoryMappingListener.existsInIssueNavConfig(getProject(), unexpectedUrl);
    }

    public void testAddNewIssueNavigationConfiguration() {
        // given
        final String expectedUrl = "MY_FAKE_URL";
        final List<IssueNavigationLink> links = IssueNavigationConfiguration.getInstance(getProject()).getLinks();
        assertEmpty(links.stream().map(IssueNavigationLink::toString).collect(Collectors.joining(",")), links);

        // when
        GitHubIssueNavigationVcsRepositoryMappingListener.addNewIssueNavigationConfiguration(getProject(), expectedUrl);

        // then
        final List<IssueNavigationLink> issues = IssueNavigationConfiguration.getInstance(getProject())
            .getLinks()
            .stream()
            .filter(issueNavigationLink -> issueNavigationLink.getLinkRegexp().contains(expectedUrl))
            .toList();
        assertEquals(3, issues.size());
    }
}
