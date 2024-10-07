package de.lukweb.discordbeam.uploaders;

import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor;
import org.jetbrains.plugins.github.api.GithubApiRequests;
import org.jetbrains.plugins.github.api.GithubServerPath;
import org.jetbrains.plugins.github.api.data.GithubGist;
import org.jetbrains.plugins.github.authentication.GHAccountsUtil;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.github.util.GHCompatibilityUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.jetbrains.plugins.github.api.data.request.GithubGistRequest.FileContent;

// https://github.com/JetBrains/intellij-community/blob/23d6e544a8d4ed3d17c52019b149fb7e6fc25e2f/plugins/github/src/org/jetbrains/plugins/github/GithubCreateGistAction.java#L129
public class GistUploaderImpl implements GistUploader {

    public boolean isGithubConfigured() {
        return !GHAccountsUtil.getAccounts().isEmpty();
    }

    public String shareGist(Project project, String fileName, String content) throws IOException {
        GithubAccount githubAccount = GHAccountsUtil.getSingleOrDefaultAccount(project);
        if (githubAccount == null) {
            throw new IOException("No (default) GitHub account found!");
        }

        String token = GHCompatibilityUtil.getOrRequestToken(githubAccount, project);
        if (token == null) {
            throw new IOException("Unable to get a token for your GitHub account!");
        }

        GithubServerPath server = githubAccount.getServer();
        List<FileContent> fileContents = Collections.singletonList(new FileContent(fileName, content));

        GithubApiRequest<GithubGist> gistCreate = GithubApiRequests.Gists
                .create(server, fileContents, "", false);

        return GithubApiRequestExecutor.Factory.getInstance()
                .create(server, token)
                .execute(gistCreate)
                .getHtmlUrl();
    }
}
