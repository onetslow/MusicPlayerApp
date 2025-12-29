import java.io.Serializable;

public class Song implements Serializable {
    private final String title;
    private final String artist;
    private String filePath;

    public Song(String title, String artist, String filePath) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return title + " - " + artist + " (Файл: " + filePath + ")";
    }
}