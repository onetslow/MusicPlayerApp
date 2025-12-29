import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Player {
    private final List<Playlist> playlists;
    private int currentPlaylistIndex = 0;
    private int currentSongIndex = 0;

    private Thread playbackThread;
    private AdvancedPlayer player;

    private AdvancedPlayer currentPlayer;
    private Thread playerThread;

    public Player() {
        this.playlists = new ArrayList<>();
    }
    public List<Playlist> getPlaylists() {
        return new ArrayList<>(playlists);
    }

    public void playSong(int currentPlaylistIndex, int currentSongIndex) {
        if (player != null) {
            player.close();
            player = null;
        }
        if (playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
        }
        stop();
        playbackThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Playlist currentPlaylist = playlists.get(currentPlaylistIndex);
                Song currentSong = currentPlaylist.getSongs().get(currentSongIndex);
                try (FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath())) {
                    player = new AdvancedPlayer(fileInputStream);
                    if (Thread.interrupted()) {
                        return;
                    }
                    player.play();
                    if (Thread.interrupted()) {
                        return;
                    }
                    System.out.println("Играет: " + "'" + currentSong.getTitle() + "'" + " - " + currentSong.getArtist());
                } catch (Exception e) {
                    System.out.println("Ошибка при воспроизведении файла: " + e.getMessage());
                    //break;
                }

                //}
            }

        });
        playbackThread.start();
    }

    public void play(int currentPlaylistIndex, int currentSongIndex) {

        Playlist currentPlaylist = playlists.get(currentPlaylistIndex);
        Song currentSong = currentPlaylist.getSongs().get(currentSongIndex);

        stop();

        playSong(currentPlaylistIndex, currentSongIndex);
    }

    public void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
        if (playbackThread != null) {
            playbackThread.interrupt();
            try {
                playbackThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            playbackThread = null;
        }
    }


    public void showSongList() {
        for (Playlist playlist : playlists) {
            System.out.println("Плейлист " + playlist.getName() + ":");
            playlist.showPlaylist();
        }
    }

    public void showPlayPlaylist(int index) {
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

    public void playPlaylist(int index) {
        if (index >= 0 && index < playlists.size()) {
            currentPlaylistIndex = index;
            currentSongIndex = 0;
            Playlist playlist = playlists.get(currentPlaylistIndex);
            if (playlist.getSongs().isEmpty()) {
                System.out.println("Плейлист пуст");
            } else {
                System.out.println("Воспроизведение плейлиста " + playlist.getName() + ":");
                //playSong();
            }
        } else {
            System.out.println("Ошибка: неправильный индекс плейлиста");
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


    public boolean savePlaylistWithPaths(int index) {
        if (index >= 0 && index < playlists.size()) {
            Playlist playlist = playlists.get(index);
            try (PrintWriter out = new PrintWriter(new FileOutputStream(System.getProperty("user.home") + "/Playyy/playlist" + index + ".csv"))) {
                for (Song song : playlist.getSongs()) {
                    out.printf("%s,%s,%s\n", song.getTitle(), song.getArtist(), song.getFilePath());
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean loadPlaylist(int index, String playlistName) {
        File playlistFile = new File(System.getProperty("user.home") + "/Playyy/playlist" + index + ".csv");
        if (playlistFile.exists() && playlistFile.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(playlistFile))) {
                Playlist playlist = new Playlist(playlistName);
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] songData = line.split(",");
                    if (songData.length == 3) {
                        String title = songData[0];
                        String artist = songData[1];
                        String filePath = songData[2];
                        Song song = new Song(title, artist, filePath);
                        playlist.addSong(song);
                    }
                }
                playlists.add(playlist);
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }


    public boolean deletePlaylist(int index) {
        if (index >= 0 && index < playlists.size()) {
            playlists.remove(index);
            return true;
        } else {
            return false;
        }
    }
    public boolean addSongToPlaylist(int playlistIndex, String title, String artist, String filePath) {
        if (playlistIndex >= 0 && playlistIndex < playlists.size()) {
            Playlist playlist = playlists.get(playlistIndex);
            playlist.addSong(new Song(title, artist, filePath));
            return true;
        } else {
            return false;
        }
    }

    public boolean removeSongFromPlaylist(int playlistIndex, int songIndex) {
        if (playlistIndex >= 0 && playlistIndex < playlists.size()) {
            Playlist playlist = playlists.get(playlistIndex);
            if (songIndex >= 0 && songIndex < playlist.getSongs().size()) {
                playlist.removeSong(songIndex);
                return true;
            }
        }
        return false;
    }

    public void stopSong() {
        if (playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
            playbackThread = null;
        }
    }
    public void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
        } else if (currentPlaylistIndex > 0) {
            currentPlaylistIndex--;
            currentSongIndex = playlists.get(currentPlaylistIndex).getSongs().size() - 1;
        } else {
            System.out.println("Это первая песня в первом плейлисте.");
        }
    }

    public void playNextSong() {
        if (currentSongIndex < playlists.get(currentPlaylistIndex).getSongs().size() - 1) {
            currentSongIndex++;
            //playSong();
        } else if (currentPlaylistIndex < playlists.size() - 1) {
            currentPlaylistIndex++;
            currentSongIndex = 0;
            //playSong();
        } else {
            System.out.println("Это последняя песня в последнем плейлисте.");
        }
    }
    public void repeatCurrentSong() {
        if (playlists.isEmpty() || playlists.get(currentPlaylistIndex).getSongs().isEmpty()) {
            System.out.println("Плейлист пуст.");
            return;
        }
        //playSong();
    }
    public List<Song> getPlaylistSongs(int index) {
        if (index < 0 || index >= playlists.size()) {
            throw new IndexOutOfBoundsException("Индекс плейлиста вне диапазона");
        }
        Playlist playlist = playlists.get(index);
        return playlist.getSongs();
    }
}
