package utility;

import java.nio.file.Path;

public enum Directory {
    WORKING(System.getProperty("user.dir")),
    KEYWORDS(WORKING.getPathString() + "/keywords"),
    MEMBERS(KEYWORDS.getPathString() + "/members"),
    POLITICAL_PARTIES(KEYWORDS.getPathString() + "/politicalParties"),
    SPEECHES(KEYWORDS.getPathString() + "/speeches"),
    SIMILARITIES(WORKING.getPathString() + "/similarities"),
    CLUSTERS(WORKING.getPathString() + "/clusters"),
    SEARCH("src/main/resources/search"),
    ANSWERS(SEARCH.getPathString() + "/answers");

    private final String path;

    Directory(String path) {
        this.path = path;
    }

    public String getPathString() {
        return this.path;
    }

    public Path getPath() {
        return Path.of(this.path);
    }
}
