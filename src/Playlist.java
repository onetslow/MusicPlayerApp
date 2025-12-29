import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Playlist implements Serializable {
    private final String name;
    private final List<Song> songs;

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public List<Song> getSongs() {
        return songs;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public void removeSong(int index) {
        if (index >= 0 && index < songs.size()) {
            songs.remove(index);
        } else {
            System.out.println("Ошибка: неправильный индекс песни");
        }
    }

    public void showPlaylist() {
        for (int i = 0; i < songs.size(); i++) {
            System.out.println((i+1) + ". " + songs.get(i));
        }
    }
}