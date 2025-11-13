package miniproject;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import miniproject.VotingDataManager;

/**
 * Voter page component for casting votes and viewing live results.
 */
public class VoterPage {
    private final ObservableList<Candidate> candidates;
    private BorderPane content;
    private ListView<Candidate> candidateListView;
    private BarChart<String, Number> resultsChart;

    public VoterPage(ObservableList<Candidate> candidates) {
        this.candidates = candidates;
        createContent();
    }

    private void createContent() {
        content = new BorderPane();
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #ffffff;");

        // Left side - Voting section
        VBox votingSection = createVotingSection();
        
        // Right side - Results section
        VBox resultsSection = createResultsSection();

        content.setLeft(votingSection);
        content.setCenter(resultsSection);

        // Add listener to auto-update chart when votes change
        candidates.addListener((javafx.collections.ListChangeListener.Change<? extends Candidate> c) -> {
            updateChart();
        });
    }

    private VBox createVotingSection() {
        VBox section = new VBox(20);
        section.setPadding(new Insets(20));
        section.setPrefWidth(350);
        section.setAlignment(Pos.TOP_CENTER);
        section.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);
            -fx-background-radius: 10;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);
        """);

        Label title = new Label("Cast Your Vote");
        title.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
        """);

        Label subtitle = new Label("Select a candidate and cast your vote");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.9);");

        candidateListView = new ListView<>(candidates);
        candidateListView.setPrefHeight(400);
        candidateListView.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 5;
            -fx-border-radius: 5;
            -fx-font-size: 16px;
        """);
        candidateListView.setCellFactory(listView -> new ListCell<Candidate>() {
            @Override
            protected void updateItem(Candidate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                    setStyle("-fx-padding: 10px; -fx-font-size: 16px;");
                }
            }
        });

        Button voteButton = new Button("Vote Now");
        voteButton.setPrefWidth(200);
        voteButton.setPrefHeight(50);
        voteButton.setStyle("""
            -fx-background-color: #2ecc71;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-background-radius: 25;
            -fx-cursor: hand;
        """);
        voteButton.setOnMouseEntered(e -> voteButton.setStyle(voteButton.getStyle() + "-fx-background-color: #27ae60;"));
        voteButton.setOnMouseExited(e -> voteButton.setStyle(voteButton.getStyle().replace("-fx-background-color: #27ae60;", "-fx-background-color: #2ecc71;")));

        voteButton.setOnAction(e -> handleVote());

        section.getChildren().addAll(title, subtitle, candidateListView, voteButton);
        return section;
    }

    private VBox createResultsSection() {
        VBox section = new VBox(20);
        section.setPadding(new Insets(20));
        section.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Live Election Results");
        title.setStyle("""
            -fx-font-size: 32px;
            -fx-font-weight: bold;
            -fx-text-fill: #2c3e50;
        """);

        Label subtitle = new Label("Real-time vote count and statistics");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        resultsChart = createBarChart();
        updateChart();

        Button refreshButton = new Button("Refresh Results");
        refreshButton.setPrefWidth(200);
        refreshButton.setPrefHeight(40);
        refreshButton.setStyle("""
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-radius: 20;
            -fx-cursor: hand;
        """);
        refreshButton.setOnMouseEntered(e -> refreshButton.setStyle(refreshButton.getStyle() + "-fx-background-color: #2980b9;"));
        refreshButton.setOnMouseExited(e -> refreshButton.setStyle(refreshButton.getStyle().replace("-fx-background-color: #2980b9;", "-fx-background-color: #3498db;")));
        refreshButton.setOnAction(e -> updateChart());

        section.getChildren().addAll(title, subtitle, resultsChart, refreshButton);
        return section;
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Candidates");
        yAxis.setLabel("Votes");
        xAxis.setStyle("-fx-font-size: 14px;");
        yAxis.setStyle("-fx-font-size: 14px;");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Vote Distribution");
        chart.setLegendVisible(false);
        chart.setPrefSize(600, 450);
        chart.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);
        """);

        return chart;
    }

    private void updateChart() {
        resultsChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Candidate c : candidates) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(c.getName(), c.getVotes());
            series.getData().add(data);
        }
        resultsChart.getData().add(series);

        // Style individual bars
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.getNode().setStyle("-fx-bar-fill: #3498db;");
        }
    }

    private void handleVote() {
        Candidate selected = candidateListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                "Please select a candidate before voting.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Your Vote");
        confirm.setHeaderText("Vote Confirmation");
        confirm.setContentText("Are you sure you want to vote for:\n\n" + 
            selected.getName() + "?\n\n" +
            "This action cannot be undone.");
        
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                selected.incrementVotes();
                candidateListView.refresh();
                updateChart();
                
                // Save after voting
                VotingDataManager.saveCandidates(candidates);
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Vote Recorded");
                success.setHeaderText("Thank You!");
                success.setContentText("Your vote for " + selected.getName() + 
                    " has been successfully recorded.");
                success.showAndWait();
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getContent() {
        return content;
    }
}

