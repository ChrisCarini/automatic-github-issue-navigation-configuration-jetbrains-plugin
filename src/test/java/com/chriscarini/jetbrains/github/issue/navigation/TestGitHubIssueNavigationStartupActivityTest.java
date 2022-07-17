package com.chriscarini.jetbrains.github.issue.navigation;


import com.intellij.openapi.vcs.IssueNavigationConfiguration;
import com.intellij.openapi.vcs.IssueNavigationLink;
import com.intellij.testFramework.LightPlatformTestCase;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;


public class TestGitHubIssueNavigationStartupActivityTest extends LightPlatformTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testExistsInIssueNavConfig() {
        // given
        final String expectedUrl = "MY_FAKE_URL";
        final String unexpectedUrl = "MY_UNEXPECTED_FAKE_URL";
        final IssueNavigationLink myIssueNavigationLink = new IssueNavigationLink("FAKE_REGEX", expectedUrl);

        IssueNavigationConfiguration.getInstance(getProject()).setLinks(List.of(myIssueNavigationLink));

        // expect
        assert GitHubIssueNavigationStartupActivity.existsInIssueNavConfig(getProject(), expectedUrl);
        assert !GitHubIssueNavigationStartupActivity.existsInIssueNavConfig(getProject(), unexpectedUrl);
    }

    @Test
    public void testAddNewIssueNavigationConfiguration() {
        // given
        final String expectedUrl = "MY_FAKE_URL";
        assert IssueNavigationConfiguration.getInstance(getProject()).getLinks().size() == 0;

        // when
        GitHubIssueNavigationStartupActivity.addNewIssueNavigationConfiguration(getProject(), expectedUrl);

        // then
        final List<IssueNavigationLink> issues = IssueNavigationConfiguration.getInstance(getProject())
            .getLinks()
            .stream()
            .filter(issueNavigationLink -> issueNavigationLink.getLinkRegexp().contains(expectedUrl))
            .collect(Collectors.toList());
        assertEquals(1, issues.size());
    }
}
