import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.getPlaylists().clear();
    }

    @Test
    public void testCreatePlaylist() {
        assertTrue(player.getPlaylists().isEmpty());

        player.createPlaylist("Playlist1");
        assertEquals(1, player.getPlaylists().size());
        assertEquals("Playlist1", player.getPlaylists().get(0).getName());

        player.createPlaylist("Playlist2");
        assertEquals(2, player.getPlaylists().size());
        assertEquals("Playlist2", player.getPlaylists().get(1).getName());
    }

    @Test
    public void testCreatePlaylistUniqueAndValidName() {
        player.createPlaylist("Playlist1");
        assertThrows(IllegalArgumentException.class, () -> {
            player.createPlaylist("Playlist1"); //имя уже существует
        });
        assertThrows(IllegalArgumentException.class, () -> {
            player.createPlaylist(""); //имя пустое
        });
    }

    @Test
    public void testPlayPlaylist() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        player.createPlaylist("Playlist1");
        player.addSongToPlaylist(1, "Song1", "Artist1");
        player.playPlaylist(1);
        assertTrue(outContent.toString().contains("Воспроизведение плейлиста Playlist1:"));
        //assertEquals("Воспроизведение плейлиста Playlist1:", outContent.toString().trim()); //чтоб глянуть сообщение в Comparison Failure

        outContent.reset();
        player.playPlaylist(5);
        assertTrue(outContent.toString().contains("Ошибка: неправильный номер плейлиста"));
    }

    @Test
    public void testLoadPlaylist() {
        player.savePlaylist(1);
        player.loadPlaylist(1);
        assertEquals(1, player.getPlaylists().size());
    }

    @Test
    public void testLoadPlaylistNonExistentIndex() {
        player.createPlaylist("Playlist1");
        assertEquals(1, player.getPlaylists().size());

        player.loadPlaylist(5);
        assertEquals(1, player.getPlaylists().size());
    }

    @Test
    public void testSavePlaylist() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        player.createPlaylist("Playlist1");
        player.savePlaylist(1);
        //System.out.println("Actual output: " + outContent.toString().trim()); //чтоб глянуть сообщение в Comparison Failure
        assertEquals("Плейлист сохранен успешно.", outContent.toString().trim());

        outContent.reset();
        player.savePlaylist(5);
        //System.out.println("Actual output: " + outContent.toString().trim()); //чтоб глянуть сообщение в Comparison Failure
        assertEquals("Ошибка: неправильный номер плейлиста", outContent.toString().trim());
    }

    @Test
    public void testDeletePlaylist() {
        player.createPlaylist("Playlist1");
        player.deletePlaylist(1);
        assertEquals(0, player.getPlaylists().size());

        player.createPlaylist("Playlist1");
        player.deletePlaylist(3);
        assertEquals(1, player.getPlaylists().size());
    }

    @Test
    public void testAddSongToPlaylist() {
        player.createPlaylist("Playlist1");
        player.addSongToPlaylist(1, "Song1", "Artist1");
        assertEquals(1, player.getPlaylists().get(0).getSongs().size());
        try {
            player.addSongToPlaylist(5, "Song1", "Artist1");
        } catch (IndexOutOfBoundsException e) {
            assertEquals(1, player.getPlaylists().get(0).getSongs().size());
            assertTrue(e.getMessage().contains("неправильный номер плейлиста"));
        }
    }

    @Test
    public void testRemoveSongFromPlaylist() {
        player.createPlaylist("Playlist1");
        player.addSongToPlaylist(1, "Song1", "Artist1");
        player.removeSongFromPlaylist(1, 1);
        assertEquals(0, player.getPlaylists().get(0).getSongs().size());
        try {
            player.removeSongFromPlaylist(1, 2);
        } catch (IndexOutOfBoundsException e) {
            assertEquals(0, player.getPlaylists().get(0).getSongs().size());
            assertTrue(e.getMessage().contains("неправильный номер плейлиста"));
        }
    }

}