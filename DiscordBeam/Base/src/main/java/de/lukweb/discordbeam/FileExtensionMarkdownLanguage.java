package de.lukweb.discordbeam;

public enum FileExtensionMarkdownLanguage {
	KOTLIN("kt", "kotlin");

	private final String extension;
	private final String language;

	FileExtensionMarkdownLanguage(String extension, String language) {
		this.extension = extension;
		this.language = language;
	}

	public static FileExtensionMarkdownLanguage getByExtension(String extension) {
		for (FileExtensionMarkdownLanguage fileExtensionMarkdownLanguage : FileExtensionMarkdownLanguage.values()) {
			if (fileExtensionMarkdownLanguage.extension.equals(extension)) {
				return fileExtensionMarkdownLanguage;
			}
		}

		return null;
	}

	public String getExtension() {
		return extension;
	}

	public String getLanguage() {
		return language;
	}
}
