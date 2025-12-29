import java.util.*;

class MusicPlayerApp {
    private static final Player musicPlayer = new Player();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("Меню:");
            System.out.println("1. Создать плейлист");
            System.out.println("2. Включить плейлист");
            System.out.println("3. Сохранить плейлист");
            System.out.println("4. Загрузить плейлист");
            System.out.println("5. Удалить плейлист");
            System.out.println("6. Добавить песню в плейлист");
            System.out.println("7. Удалить песню из плейлиста");
            System.out.println("8. Включить предыдущий трек");
            System.out.println("9. Включить следующий трек");
            System.out.println("10. Повторить текущий трек");
            System.out.println("11. Показать все песни");
            System.out.println("12. Показать весь плейлист");
            System.out.println("0. Выйти из приложения");

            int choice = readInt(scanner, "Выберите действие: ");

            switch (choice) {
                case 1:
                    System.out.print("Введите название плейлиста: ");
                    String playlistName = scanner.nextLine();
                    musicPlayer.createPlaylist(playlistName);
                    break;
                case 2:
                    int playlistIndex = readInt(scanner, "Введите номер плейлиста: ");
                    musicPlayer.playPlaylist(playlistIndex);
                    break;
                case 3:
                    int playlistIndexToSave = readInt(scanner, "Введите номер плейлиста: ");
                    musicPlayer.savePlaylist(playlistIndexToSave);
                    break;
                case 4:
                    int index = readInt(scanner, "Введите номер плейлиста для загрузки: ");
                    musicPlayer.loadPlaylist(index);
                    break;
                case 5:
                    int playlistIndexToDelete = readInt(scanner, "Введите номер плейлиста для удаления: ");
                    musicPlayer.deletePlaylist(playlistIndexToDelete);
                    break;
                case 6:
                    int playlistIndexToAddSong = readInt(scanner, "Введите номер плейлиста: ");
                    System.out.print("Введите название песни: ");
                    String songTitle = scanner.nextLine();
                    System.out.print("Введите исполнителя: ");
                    String songArtist = scanner.nextLine();
                    musicPlayer.addSongToPlaylist(playlistIndexToAddSong, songTitle, songArtist);
                    break;
                case 7:
                    int playlistIndexToRemoveSong = readInt(scanner, "Введите номер плейлиста: ");
                    int songIndexToRemove = readInt(scanner, "Введите номер песни для удаления: ");
                    musicPlayer.removeSongFromPlaylist(playlistIndexToRemoveSong, songIndexToRemove);
                    break;
                case 8:
                    musicPlayer.playPreviousSong();
                    break;
                case 9:
                    musicPlayer.playNextSong();
                    break;
                case 10:
                    musicPlayer.repeatCurrentSong();
                    break;
                case 11:
                    musicPlayer.showSongList();
                    break;
                case 12:
                    int playlistIndexX = readInt(scanner, "Введите номер плейлиста: ");
                    musicPlayer.showPlayPlaylist(playlistIndexX);
                    break;
                case 0:
                    running = false;
                    System.out.println("До свидания!");
                    break;
                default:
                    System.out.println("Некорректный выбор");
                    break;
            }
        }
        scanner.close();
    }
    private static int readInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число!");
            }
        }
    }
}