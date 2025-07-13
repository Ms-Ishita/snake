
import java.io.*;

public class MaxScoreManager {
    private static final String FILE_NAME = "highscore.txt";

    public static int readMaxScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = reader.readLine();
            return line != null ? Integer.parseInt(line.trim()) : 0;
        } catch (IOException | NumberFormatException e) {
            return 0; 
        }
    }

    public static void writeMaxScore(int newScore) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(String.valueOf(newScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateMaxScoreIfNeeded(int currentScore) {
        int maxScore = readMaxScore();
        if (currentScore > maxScore) {
            writeMaxScore(currentScore);
        }
    }
}