import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PlayerGUI {
    private JFrame frame;
    private JList<String> songList;
    private DefaultListModel<String> playlistModel;
    private DefaultListModel<String> songModel;
    private final Player musicPlayer;
    private int currentPlaylistIndex = -1;
    private int currentSongIndex = -1;
    private boolean isPlaying = false;

    public PlayerGUI(Player player) {
        this.musicPlayer = player;
        initGUI();
    }
    private void initGUI() {
        frame = new JFrame("Music Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();

                // Градиент сверху вниз: синий → фиолетовый
                GradientPaint gp = new GradientPaint(0, 0, Color.BLUE, 0, height, new Color(128, 0, 128));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        panel.setLayout(new FlowLayout()); // или другой layout

        JButton playStopButton = new JButton("▶️"); // стартовая иконка Play
        playStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) {
                    // Воспроизведение
                    play();
                    playStopButton.setText("⏹️"); // меняем на Stop
                    isPlaying = true;
                } else {
                    // Остановка
                    musicPlayer.stop();
                    songModel.clear();
                    songList.setModel(songModel);
                    playStopButton.setText("▶️"); // меняем обратно на Play
                    isPlaying = false;
                }
            }
        });
        panel.add(playStopButton);

        JButton nextSongButton = createButton("⏭️", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextSong();
            }
        });

        JButton prevSongButton = createButton("⏮️", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prevSong();
            }
        });

        JButton repSongButton = createButton("🔁", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repeatSong();
            }
        });

        JButton addSongButton = createButton("➕ Song", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSong();
            }
        });

        JButton removeSongButton = createButton("Remove Song", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSong();
            }
        });

        JButton createPlaylistButton = createButton("Create Playlist", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPlaylist();
            }
        });

        JButton deletePlaylistButton = createButton("Delete Playlist", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePlaylist();
            }
        });

        JButton openButton = createButton("📂", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPlaylist();
            }
        });

        JButton saveButton = createButton("💾", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePlaylist();
            }
        });

        JButton showAllSongs = createButton("All Songs", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllSongs();
            }
        });

        JButton showSongsPlaylist = createButton("Playlist Songs", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPlaylistSongs();
            }
        });

        panel.add(playStopButton);
        panel.add(prevSongButton);
        panel.add(nextSongButton);
        panel.add(repSongButton);
        panel.add(addSongButton);
        panel.add(removeSongButton);
        panel.add(createPlaylistButton);
        panel.add(deletePlaylistButton);
        panel.add(openButton);
        panel.add(saveButton);
        panel.add(showAllSongs);
        panel.add(showSongsPlaylist);

        playlistModel = new DefaultListModel<>();
        JList<String> playlistList = new JList<>(playlistModel);
        panel.add(new JScrollPane(playlistList));

        songModel = new DefaultListModel<>();
        songList = new JList<>(songModel);
        panel.add(new JScrollPane(songList));

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    private JButton createButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);
        return button;
    }
    private void play() {
        String input = JOptionPane.showInputDialog(frame, "Enter playlist index to play: ");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int playlistIndex = Integer.parseInt(input);
            if (playlistIndex < 0 || playlistIndex >= musicPlayer.getPlaylists().size()) {
                showError("Wrong playlist index.");
                return;
            }
            currentPlaylistIndex = playlistIndex;
            currentSongIndex = 0;
            Playlist playlist = musicPlayer.getPlaylists().get(playlistIndex);
            songModel.clear();
            if (!playlist.getSongs().isEmpty()) {
                Song firstSong = playlist.getSongs().getFirst();
                songModel.addElement("Play playlist " + playlist.getName() + ":");
                songModel.addElement(firstSong.getTitle() + " - " + firstSong.getArtist());
                musicPlayer.play(playlistIndex, 0);
            } else {
                songModel.addElement("Playlist " + playlist.getName() + " is empty");
            }
            songList.setModel(songModel);
        } catch (NumberFormatException ex) {
            showError("It's not a number.");
        }
    }

    private void nextSong() { changeSong(1); }
    private void prevSong() { changeSong(-1); }
    private void repeatSong() { changeSong(0); }

    private void changeSong(int delta) {
        if (currentPlaylistIndex == -1 || currentSongIndex == -1) {
            showError("Playlist not selected");
            return;
        }
        Playlist playlist = musicPlayer.getPlaylists().get(currentPlaylistIndex);
        if (playlist.getSongs().isEmpty()) {
            songModel.clear();
            songModel.addElement("Playlist " + playlist.getName() + " is empty");
            songList.setModel(songModel);
            return;
        }
        musicPlayer.stop();
        currentSongIndex = (currentSongIndex + delta + playlist.getSongs().size()) % playlist.getSongs().size();
        Song song = playlist.getSongs().get(currentSongIndex);
        songModel.clear();
        songModel.addElement("Playlist " + playlist.getName());
        songModel.addElement(song.getTitle() + " - " + song.getArtist());
        songList.setModel(songModel);
        musicPlayer.play(currentPlaylistIndex, currentSongIndex);
    }

    private void addSong() {
        try {
            int playlistIndex = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter playlist index to add song: "));
            String title = JOptionPane.showInputDialog(frame, "Enter name of song: ");
            String artist = JOptionPane.showInputDialog(frame, "Enter artist of song: ");
            String path = JOptionPane.showInputDialog(frame, "Enter file path of song: ");
            boolean added = musicPlayer.addSongToPlaylist(playlistIndex, title, artist, path);
            if (added) {
                Playlist pl = musicPlayer.getPlaylists().get(playlistIndex);
                songModel.clear();
                songModel.addElement("Playlist " + pl.getName() + ":");
                songModel.addElement(title + " - " + artist);
                songList.setModel(songModel);
                JOptionPane.showMessageDialog(frame, "Song was added.");
            } else showError("Wrong playlist or song index.");
        } catch (NumberFormatException ex) { showError("It's not a number."); }
    }

    private void removeSong() {
        try {
            int plIndex = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter playlist index to remove song: "));
            int songIndex = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter song index to remove: "));

            // Проверяем, удаляется ли сейчас играющий трек
            if (plIndex == currentPlaylistIndex && songIndex == currentSongIndex) {
                musicPlayer.stop(); // остановить воспроизведение
                currentSongIndex = -1;
            }

            boolean removed = musicPlayer.removeSongFromPlaylist(plIndex, songIndex);
            if (removed) {
                displayPlaylistSongs(musicPlayer.getPlaylists().get(plIndex));
                JOptionPane.showMessageDialog(frame, "Song was deleted from playlist.");
            } else showError("Wrong playlist or song index.");
        } catch (NumberFormatException ex) {
            showError("It's not a number.");
        }
    }


    private void createPlaylist() {
        String name = JOptionPane.showInputDialog(frame, "Enter playlist name: ");
        if (name == null || name.trim().isEmpty()) { showError("Playlist name cannot be empty."); return; }
        for (Playlist pl : musicPlayer.getPlaylists())
            if (pl.getName().equalsIgnoreCase(name.trim())) { showError("Playlist name is already used"); return; }
        musicPlayer.createPlaylist(name.trim());
        displayAllPlaylists();
    }

    private void deletePlaylist() {
        try {
            int index = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter playlist index to delete:"));

            if (index < 0 || index >= musicPlayer.getPlaylists().size()) {
                showError("Wrong playlist index.");
                return;
            }

            // Если удаляем текущий воспроизводимый плейлист
            if (index == currentPlaylistIndex) {
                musicPlayer.stop();
                currentPlaylistIndex = -1;
                currentSongIndex = -1;
            }

            musicPlayer.deletePlaylist(index);
            displayAllPlaylists();
            songModel.clear(); // очищаем список песен
            JOptionPane.showMessageDialog(frame, "Playlist was deleted.");
        } catch (NumberFormatException ex) {
            showError("It's not a number.");
        }
    }


    private void loadPlaylist() {
        try {
            int index = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter playlist index to load: "));
            String name = JOptionPane.showInputDialog(frame, "Enter playlist name: ");
            if (name == null || name.trim().isEmpty()) { showError("Missing information."); return; }
            boolean loaded = musicPlayer.loadPlaylist(index, name);
            if (loaded) JOptionPane.showMessageDialog(frame, "Playlist '" + name + "' was loaded.");
            else showError("Could not load the playlist.");
        } catch (NumberFormatException ex) { showError("Index is not a number."); }
    }

    private void savePlaylist() {
        try {
            int index = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter playlist index to save:"));
            boolean saved = musicPlayer.savePlaylistWithPaths(index);
            if (saved) JOptionPane.showMessageDialog(frame, "Playlist was saved.");
            else showError("Wrong playlist index.");
        } catch (NumberFormatException ex) { showError("It's not a number."); }
    }

    private void showAllSongs() {
        songModel.clear();
        List<Playlist> playlists = musicPlayer.getPlaylists();
        if (playlists.isEmpty()) songModel.addElement("All playlists are empty");
        else for (Playlist pl : playlists) displayPlaylistSongs(pl);
    }

    private void showPlaylistSongs() {
        try {
            int index = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter playlist index to show songs list: "));
            if (index < 0 || index >= musicPlayer.getPlaylists().size()) { showError("Wrong playlist index."); return; }
            displayPlaylistSongs(musicPlayer.getPlaylists().get(index));
        } catch (NumberFormatException ex) { showError("It's not a number."); }
    }

    private void displayPlaylistSongs(Playlist playlist) {
        songModel.clear();
        if (playlist.getSongs().isEmpty()) songModel.addElement("Playlist " + playlist.getName() + " is empty");
        else {
            songModel.addElement("Playlist " + playlist.getName() + ":");
            for (Song s : playlist.getSongs()) songModel.addElement(s.getTitle() + " - " + s.getArtist());
        }
        songList.setModel(songModel);
    }

    private void displayAllPlaylists() {
        playlistModel.clear();
        for (Playlist pl : musicPlayer.getPlaylists()) playlistModel.addElement("Playlist: " + pl.getName());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        Player player = new Player();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PlayerGUI(player);
            }
        });
    }
}