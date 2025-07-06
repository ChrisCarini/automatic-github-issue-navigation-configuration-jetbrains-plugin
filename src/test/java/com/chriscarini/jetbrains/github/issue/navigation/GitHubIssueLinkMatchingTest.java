package com.chriscarini.jetbrains.github.issue.navigation;

import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.IssueNavigationConfiguration;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsDirectoryMapping;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightPlatformTestCase;

import java.io.File;
import java.util.Collections;
import java.util.List;

import git4idea.GitVcs;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandler;
import org.jetbrains.annotations.NotNull;


public class GitHubIssueLinkMatchingTest extends LightPlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final Project project = getProject();

        // `git init`
        final String rootDirectory = project.getBasePath();
        assertNotNull("Project root directory should not be null", rootDirectory);
        final Git git = Git.getInstance();
        final GitLineHandler handler = new GitLineHandler(project, new File(rootDirectory), GitCommand.INIT);
        handler.setSilent(false);
        final GitCommandResult initResult = git.runCommand(handler);
        if (!initResult.success()) {
            fail("This shouldn't happen.");
        }

        // `git remote add origin ...`
        final String remoteUrl = "https://github.com/ChrisCarini/automatic-github-issue-navigation-configuration-jetbrains-plugin.git";
        final GitLineHandler remoteAddHandler = new GitLineHandler(project, new File(rootDirectory), GitCommand.REMOTE);
        remoteAddHandler.addParameters("add", "origin", remoteUrl);
        final GitCommandResult remoteAddResult = git.runCommand(remoteAddHandler);

        if (!remoteAddResult.success()) {
            fail("Failed to add remote: " + remoteAddResult.getErrorOutputAsJoinedString());
        }

        // register git repo
        final VcsDirectoryMapping mapping = new VcsDirectoryMapping(rootDirectory, GitVcs.NAME);
        ProjectLevelVcsManager.getInstance(project).setDirectoryMappings(Collections.singletonList(mapping));

        // trigger an update so that the repository configured above shows up
        final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(rootDirectory);
        VcsRepositoryManager.getInstance(project).getRepositoryForRoot(file);

        // configure issue navigation
        final GitHubIssueNavigationVcsRepositoryMappingListener l = new GitHubIssueNavigationVcsRepositoryMappingListener(project);
        l.configureIssueNavigationConfigurations();
    }

    @Override
    protected void tearDown() throws Exception {
        ProjectLevelVcsManager.getInstance(getProject()).setDirectoryMappings(Collections.emptyList());
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
    }

    private List<String> getIssueMatches(@NotNull final String source) {
        return IssueNavigationConfiguration.getInstance(getProject()).findIssueLinks(source).stream().map(m -> m.getRange().substring(source)).toList();
    }
}