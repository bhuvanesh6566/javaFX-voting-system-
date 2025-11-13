package miniproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * Main application class for the Voting System.
 * Provides a modern, multi-page interface for voting and administration.
 */
public class VotingApplication extends Application {

    private final ObservableList<Candidate> candidates = FXCollections.observableArrayList();
    private Stage primaryStage;
    private TabPane mainTabPane;
    private VoterPage voterPage;
    private AdminPage adminPage;

    private static final String ADMIN_PASSWORD = "admin";
    private static final String APP_TITLE = "Voting System - Professional Edition";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);

        // Load candidates from file
        loadCandidates();

        // Create main tab pane
        mainTabPane = new TabPane();
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create pages
        voterPage = new VoterPage(candidates);
        adminPage = new AdminPage(candidates, primaryStage, this::showAlert);

        // Create tabs
        Tab voterTab = new Tab("Vote", voterPage.getContent());
        voterTab.setClosable(false);

        Tab adminTab = new Tab("Admin", adminPage.getAuthPane());
        adminTab.setClosable(false);

        mainTabPane.getTabs().addAll(voterTab, adminTab);

        // Handle admin authentication
        adminPage.setOnLoginSuccess(() -> {
            Tab adminTabRef = mainTabPane.getTabs().get(1);
            adminTabRef.setContent(adminPage.getAdminPanel());
        });

        // Apply modern styling
        applyStyles();

        Scene scene = new Scene(mainTabPane, 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add listener to auto-save when candidates list changes
        candidates.addListener((javafx.collections.ListChangeListener.Change<? extends Candidate> change) -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved()) {
                    Platform.runLater(() -> VotingDataManager.saveCandidates(candidates));
                }
            }
        });

        // Save on exit
        primaryStage.setOnCloseRequest(e -> {
            VotingDataManager.saveCandidates(candidates);
            Platform.exit();
        });
    }

    private void loadCandidates() {
        List<Candidate> loaded = VotingDataManager.loadCandidates();
        candidates.setAll(loaded);
    }

    private void applyStyles() {
        String style = """
            -fx-font-family: 'Segoe UI', Arial, sans-serif;
            -fx-base: #3498db;
            -fx-background: #f5f5f5;
        """;
        mainTabPane.setStyle(style);
    }

    private void showAlert(AdminPage.AlertInfo alertInfo) {
        Alert alert = new Alert(alertInfo.type);
        alert.setTitle(alertInfo.title);
        alert.setHeaderText(null);
        alert.setContentText(alertInfo.message);
        alert.showAndWait();
    }

    public ObservableList<Candidate> getCandidates() {
        return candidates;
    }
}

