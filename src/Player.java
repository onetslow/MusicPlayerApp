import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private final List<Playlist> playlists;

    public Player() {
        this.playlists = new ArrayList<>();
    }
    public List<Playlist> getPlaylists() {
        return new ArrayList<>(playlists);
    }
    private int currentPlaylistIndex = 0;
    private int currentSongIndex = 0;

    public void showSongList() {
        for (Playlist playlist : playlists) {
            System.out.println("Плейлист " + playlist.getName() + ":");
            playlist.showPlaylist();
        }
    }

    public void showPlayPlaylist(int userIndex) {
        int index = userIndex - 1;
        if (index >= 0 && index < playlists.size()) {
            Playlist playlist = playlists.get(index);
            if (playlist.getSongs().isEmpty()) {
                System.out.println("Плейлист пуст");
            } else {
                System.out.println("Воспроизведение плейлиста " + playlist.getName() + ":");
                for (Song song : playlist.getSongs()) {
                    System.out.println("'"+ song.getTitle() +"'"+ " - " + song.getArtist());
                }}
        } else {
            System.out.println("Ошибка: неправильный индекс плейлиста");
        }
    }

    public void playPlaylist(int userIndex) {
        int index = userIndex - 1;
        if (index >= 0 && index < playlists.size()) {
            currentPlaylistIndex = index;
            currentSongIndex = 0;
            Playlist playlist = playlists.get(currentPlaylistIndex);
            if (playlist.getSongs().isEmpty()) {
                System.out.println("Плейлист пуст");
            } else {
                System.out.println("Воспроизведение плейлиста " + playlist.getName() + ":");
                playSong();
            }
        } else {
            System.out.println("Ошибка: неправильный номер плейлиста");
        }
    }

    public void createPlaylist(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя плейлиста не может быть пустым");
        }
        for (Playlist existingPlaylist : playlists) {
            if (existingPlaylist.getName().equals(name)) {
                throw new IllegalArgumentException("Плейлист с таким именем уже существует");
            }
        }
        playlists.add(new Playlist(name));
    }

    public void loadPlaylist(int userIndex) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(System.getProperty("user.home") + "/Playyy/playlist" + userIndex + ".dat"))) {
            Playlist playlist = (Playlist) in.readObject();
            playlists.add(playlist);
            System.out.println("Плейлист успешно загружен.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке плейлиста: " + e.getMessage());
        }
    }

    public void savePlaylist(int userIndex) {
        int index = userIndex - 1;
        if (index < 0 || index >= playlists.size()) {
            System.out.println("Ошибка: неправильный номер плейлиста");
            return;
        }

        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(System.getProperty("user.home") + "/Playyy/playlist" + userIndex + ".dat"))) {

            out.writeObject(playlists.get(index));
            System.out.println("Плейлист сохранен успешно.");

        } catch (IOException e) {
            System.out.println("Ошибка при сохранении плейлиста");
        }
    }


    public void deletePlaylist(int userIndex) {
        int index = userIndex - 1;
        if (index >= 0 && index < playlists.size()) {
            playlists.remove(index);
        } else {
            System.out.println("Ошибка: неправильный номер плейлиста");
        }
    }

    public void addSongToPlaylist(int userIndex, String title, String artist ) {
        int index = userIndex - 1;
        if (index >= 0 && index < playlists.size()) {
            Playlist playlist = playlists.get(index);
            playlist.addSong(new Song(title, artist));
        } else {
            System.out.println("Ошибка: неправильный номер плейлиста");
        }
    }

    public void removeSongFromPlaylist(int userIndex, int songNumber) {
        int playlistIndex = userIndex - 1;
        int songIndex = songNumber - 1;
        if (playlistIndex >= 0 && playlistIndex < playlists.size()) {
            Playlist playlist = playlists.get(playlistIndex);
            playlist.removeSong(songIndex);
        } else {
            System.out.println("Ошибка: неправильный номер плейлиста");
        }
    }

    public void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            playSong();
        } else if (currentPlaylistIndex > 0) {
            currentPlaylistIndex--;
            currentSongIndex = playlists.get(currentPlaylistIndex).getSongs().size() - 1;
            playSong();
        } else {
            System.out.println("Это первая песня в первом плейлисте.");
        }
    }

    public void playNextSong() {
        if (currentSongIndex < playlists.get(currentPlaylistIndex).getSongs().size() - 1) {
            currentSongIndex++;
            playSong();
        } else if (currentPlaylistIndex < playlists.size() - 1) {
            currentPlaylistIndex++;
            currentSongIndex = 0;
            playSong();
        } else {
            System.out.println("Это последняя песня в последнем плейлисте.");
        }
    }

    public void repeatCurrentSong() {
        if (playlists.isEmpty() || playlists.get(currentPlaylistIndex).getSongs().isEmpty()) {
            System.out.println("Плейлист пуст.");
            return;
        }
        playSong();
    }

    private void playSong() {

        Playlist currentPlaylist = playlists.get(currentPlaylistIndex);
        Song currentSong = currentPlaylist.getSongs().get(currentSongIndex);
        System.out.println("Играет: " + "'" + currentSong.getTitle() + "'" + " - " + currentSong.getArtist());
    }

}