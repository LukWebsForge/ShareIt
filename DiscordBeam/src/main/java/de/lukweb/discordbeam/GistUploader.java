package de.lukweb.discordbeam;

import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.github.api.*;
import org.jetbrains.plugins.github.api.data.GithubGist;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.jetbrains.plugins.github.api.requests.GithubGistRequest.FileContent;

// https://github.com/JetBrains/intellij-community/blob/2c9b00ff4b6133e69538c2f4382444e00e99efed/plugins/github/src/org/jetbrains/plugins/github/GithubCreateGistAction.java#L114
public class GistUploader {

    public boolean isGithubConfigured() {
        GithubAuthenticationManager authManager = GithubAuthenticationManager.getInstance();
        return authManager.hasAccounts();
    }

    public String shareGist(Project project, String fileName, String content) throws IOException {
        GithubAuthenticationManager authManager = GithubAuthenticationManager.getInstance();
        GithubAccount githubAccount = authManager.getSingleOrDefaultAccount(project);

        if (githubAccount == null) {
            throw new IOException("No (default) GitHub account found!");
        }

        GithubApiRequestExecutor requestExecutor = GithubApiRequestExecutorManager
                .getInstance()
                .getExecutor(githubAccount, project);

        if (requestExecutor == null) {
            throw new IOException("Unable to create a GithubApiRequestExecutor!");
        }

        GithubServerPath server = githubAccount.getServer();
        List<FileContent> fileContents = Collections.singletonList(new FileContent(fileName, content));

        GithubApiRequest<GithubGist> gistCreate = GithubApiRequests.Gists
                .create(server, fileContents, "", false);

        return requestExecutor.execute(gistCreate).getHtmlUrl();
    }
}
