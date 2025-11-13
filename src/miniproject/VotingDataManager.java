package miniproject;

import javafx.collections.ObservableList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages data persistence for the voting system.
 */
public class VotingDataManager {
    private static final String DEFAULT_DATA_FILE = "votes.csv";

    /**
     * Loads candidates from CSV file.
     */
    public static List<Candidate> loadCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        Path filePath = Paths.get(DEFAULT_DATA_FILE);
        
        if (!Files.exists(filePath)) {
            // Return default candidates if file doesn't exist
            candidates.add(new Candidate("Dinesh"));
            candidates.add(new Candidate("Priya"));
            candidates.add(new Candidate("Vetri"));
            return candidates;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine && line.trim().equalsIgnoreCase("Name,Votes")) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    try {
                        int votes = Integer.parseInt(parts[1].trim());
                        candidates.add(new Candidate(name, votes));
                    } catch (NumberFormatException e) {
                        candidates.add(new Candidate(name, 0));
                    }
                } else if (parts.length == 1 && !parts[0].trim().isEmpty()) {
                    candidates.add(new Candidate(parts[0].trim(), 0));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading candidates: " + e.getMessage());
            // Return default candidates on error
            if (candidates.isEmpty()) {
                candidates.add(new Candidate("Dinesh"));
                candidates.add(new Candidate("Priya"));
                candidates.add(new Candidate("Vetri"));
            }
        }
        return candidates;
    }

    /**
     * Saves candidates to CSV file.
     */
    public static void saveCandidates(ObservableList<Candidate> candidates) {
        Path filePath = Paths.get(DEFAULT_DATA_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("Name,Votes\n");
            for (Candidate candidate : candidates) {
                writer.write(candidate.getName() + "," + candidate.getVotes() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving candidates: " + e.getMessage());
        }
    }

    /**
     * Exports candidates to a specified file.
     */
    public static boolean exportToFile(ObservableList<Candidate> candidates, File file) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.append("Name,Votes\n");
            for (Candidate c : candidates) {
                fw.append(c.getName()).append(",").append(String.valueOf(c.getVotes())).append("\n");
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to file: " + e.getMessage());
            return false;
        }
    }
}

