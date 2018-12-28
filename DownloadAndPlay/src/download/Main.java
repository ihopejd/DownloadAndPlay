package download;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Main {

    private static final String IN_FILE_TXT = "src\\download\\inFile.txt";
    private static final String OUT_FILE_TXT = "src\\download\\outFile.txt";
    private static final String PATH_TO_FILES = "C:\\Users\\BIG GUY\\IdeaProjects\\DownloadAndPlay\\src\\result\\";
    private static String Url;


    public static void main(String[] args) {

        downloadFiles();
        resultSave();
        MP3Player();
    }

    private static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
        FileOutputStream stream = new FileOutputStream(file);
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        stream.close();
        byteChannel.close();
    }

    private static void downloadFiles() {
        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
             BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_TXT))) {
            while ((Url = inFile.readLine()) != null) {
                URL url = new URL(Url);

                String resultMusic;

                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    resultMusic = bufferedReader.lines().collect(Collectors.joining("\n"));
                }
                Pattern music_pattern = Pattern.compile("\\s*(?<=data-url\\s?=\\s?\")[^>]*\\/*(?=\")");
                Matcher matcher_music = music_pattern.matcher(resultMusic);
                int i = 0;
                while (matcher_music.find() && i < 2) {
                    outFile.write(matcher_music.group() + "\r\n");
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void resultSave() {
            try (BufferedReader resultFile = new BufferedReader(new FileReader(OUT_FILE_TXT))){
                String music;
                int count = 0;
                try {
                    while ((music = resultFile.readLine()) != null) {
                        downloadUsingNIO(music, PATH_TO_FILES + String.valueOf(count) + ".mp3");
                        count++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    private static void  MP3Player() {
            try (FileInputStream download = new FileInputStream("C:\\Users\\BIG GUY\\IdeaProjects\\DownloadAndPlay\\src\\result\\0.mp3")){
                try {
                    Player player = new Player(download);
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

