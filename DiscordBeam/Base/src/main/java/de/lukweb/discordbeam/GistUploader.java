package de.lukweb.discordbeam;

import com.intellij.openapi.project.Project;

import java.io.IOException;

public interface GistUploader {

    boolean isGithubConfigured();

    String shareGist(Project project, String fileName, String content) throws IOException;

}
