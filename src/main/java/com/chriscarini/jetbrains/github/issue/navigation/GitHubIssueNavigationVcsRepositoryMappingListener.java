package com.chriscarini.jetbrains.github.issue.navigation;

import com.chriscarini.jetbrains.github.utils.GitHubUri;
import com.chriscarini.jetbrains.github.utils.GitHubUtils;
import com.google.common.collect.Lists;
import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.dvcs.repo.VcsRepositoryMappingListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.IssueNavigationConfiguration;
import com.intellij.openapi.vcs.IssueNavigationLink;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepositoryImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class GitHubIssueNavigationVcsRepositoryMappingListener implements VcsRepositoryMappingListener {

    private static final @NonNls Logger LOG = Logger.getInstance(GitHubIssueNavigationVcsRepositoryMappingListener.class);
    private final @NotNull Project project;

    public GitHubIssueNavigationVcsRepositoryMappingListener(@NotNull final Project project) {
        this.project = project;
    }

    @Override
    public void mappingChanged() {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            LOG.debug("Unit test mode; will not attempt to add GitHub Issue Navigation to project");
            return;
        }

        configureIssueNavigationConfigurations();
    }

    protected void configureIssueNavigationConfigurations() {
        final Collection<Repository> repositories = VcsRepositoryManager.getInstance(project).getRepositories();
        repositories.forEach(repository -> {
            final Collection<GitRemote> repoRemotes = ((GitRepositoryImpl) repository).getRemotes();
            repoRemotes.forEach(
                    gitRemote -> {
                        final String url = gitRemote.getFirstUrl();

                        if (url == null) {
                            LOG.debug(String.format("Null Git remote URL: %s", gitRemote));
                            return;
                        }

                        if (!GitHubUtils.isKnownGitHubDomain(url)) {
                            LOG.debug(String.format(
                                    "Git remote URL (%s) not from a known GitHub domain: %s",
                                    gitRemote,
                                    GitHubUtils.knownGitHubDomains().collect(Collectors.joining(", "))
                            ));
                            return;
                        }

                        final String cleanedUrl = GitHubUri.parseUrl(url).asHttpsFormatUrl();

                        if (existsInIssueNavConfig(project, cleanedUrl)) {
                            LOG.debug(String.format("%s is already registered.", cleanedUrl));
                            return;
                        }

                        LOG.debug(String.format("Registering [%s].", cleanedUrl));
                        addNewIssueNavigationConfiguration(project, cleanedUrl);
                    }
            );
        });
    }

    protected static boolean existsInIssueNavConfig(@NotNull final Project project, @NotNull final String url) {
        final List<String> newLinks = getIssueNavigationLinks(url)
                .stream()
                .map(IssueNavigationLink::getLinkRegexp)
                .toList();
        final List<IssueNavigationLink> matching = IssueNavigationConfiguration.getInstance(project).getLinks()
                .stream()
                .filter(issueNavigationLink -> newLinks.contains(issueNavigationLink.getLinkRegexp()))
                .toList();

        return matching.size() == newLinks.size();
    }

    protected static void addNewIssueNavigationConfiguration(@NotNull final Project project, @NotNull final String url) {
        final IssueNavigationConfiguration issueNavigationConfiguration = IssueNavigationConfiguration.getInstance(project);

        final List<IssueNavigationLink> currentRegisteredLinks = issueNavigationConfiguration.getLinks();
        final List<IssueNavigationLink> newLinks = getIssueNavigationLinks(url);

        final List<IssueNavigationLink> currentRegisteredLinksMinusNew = Lists.newArrayList(currentRegisteredLinks);
        currentRegisteredLinksMinusNew.removeAll(newLinks);

        issueNavigationConfiguration.setLinks(
                Stream.concat(
                        currentRegisteredLinksMinusNew.stream(),
                        newLinks.stream()
                ).toList());
    }

    protected static @NotNull List<IssueNavigationLink> getIssueNavigationLinks(@NotNull final String url) {
        // `(#123)`                            ( 'parenthesis' pound-number )
        final IssueNavigationLink parenPoundNum = new IssueNavigationLink("\\(#(\\d+)\\)", String.format("%s/issues/$1", url));

        // `#123`                             ( 'raw' pound-number )
        final IssueNavigationLink rawPoundNum = new IssueNavigationLink("#(\\d+)", String.format("%s/issues/$1", url));

        // `GH-123`                            ( 'GH'-prefix'd number )
        final IssueNavigationLink ghPrefix = new IssueNavigationLink("GH-(\\d+)", String.format("%s/issues/$1", url));

        // `JetBrains/intellij-community#123`  ( org/user repo pound-number )
        final IssueNavigationLink orgUserRepoNo = new IssueNavigationLink("([^/\\n ]+/[^#\\n]+)#(\\d+)", "https://github.com/$1/issues/$2");

        return List.of(parenPoundNum, rawPoundNum, ghPrefix, orgUserRepoNo);
    }
}
