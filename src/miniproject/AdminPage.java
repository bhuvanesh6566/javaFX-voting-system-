package miniproject;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.function.Consumer;
import miniproject.VotingDataManager;

/**
 * Admin page component for managing candidates and viewing detailed results.
 */
public class AdminPage {
    private final ObservableList<Candidate> candidates;
    private final Stage owner;
    private final Consumer<AlertInfo> alertHandler;
    private final String ADMIN_PASSWORD = "admin";
    
    private VBox authPane;
    private BorderPane adminPanel;
    private TableView<Candidate> candidateTable;
    private BarChart<String, Number> adminChart;
    private Runnable onLoginSuccess;

    public AdminPage(ObservableList<Candidate> candidates, Stage owner, 
                     Consumer<AlertInfo> alertHandler) {
        this.candidates = candidates;
        this.owner = owner;
        this.alertHandler = alertHandler;
        createAuthPane();
        createAdminPanel();
    }

    private void createAuthPane() {
        authPane = new VBox(20);
        authPane.setPadding(new Insets(40));
        authPane.setAlignment(Pos.CENTER);
        authPane.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #f093fb 0%, #f5576c 100%);
            -fx-background-radius: 10;
        """);

        Label title = new Label("Admin Access");
        title.setStyle("""
            -fx-font-size: 32px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
        """);

        Label subtitle = new Label("Enter your administrator password");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: rgba(255,255,255,0.9);");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(300);
        passwordField.setPrefHeight(40);
        passwordField.setStyle("""
            -fx-font-size: 16px;
            -fx-background-radius: 20;
            -fx-padding: 10px;
        """);

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(200);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("""
            -fx-background-color: #2ecc71;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-background-radius: 25;
            -fx-cursor: hand;
        """);

        loginButton.setOnAction(e -> {
            if (ADMIN_PASSWORD.equals(passwordField.getText())) {
                passwordField.clear();
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Access Denied", 
                    "Incorrect password. Please try again.");
                passwordField.clear();
            }
        });

        // Allow Enter key to submit
        passwordField.setOnAction(e -> loginButton.fire());

        VBox formBox = new VBox(15, subtitle, passwordField, loginButton);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(20));

        authPane.getChildren().addAll(title, formBox);
    }

    private void createAdminPanel() {
        adminPanel = new BorderPane();
        adminPanel.setPadding(new Insets(20));
        adminPanel.setStyle("-fx-background-color: #f8f9fa;");

        // Top controls
        HBox controls = createControlPanel();
        adminPanel.setTop(controls);

        // Center table
        candidateTable = createCandidateTable();
        adminPanel.setCenter(candidateTable);

        // Right chart
        VBox chartSection = createChartSection();
        adminPanel.setRight(chartSection);
    }

    private HBox createControlPanel() {
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(15));
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
        """);

        Label title = new Label("Admin Dashboard");
        title.setStyle("""
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            -fx-text-fill: #2c3e50;
        """);

        TextField nameField = new TextField();
        nameField.setPromptText("Candidate name");
        nameField.setPrefWidth(200);
        nameField.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

        Button addBtn = createStyledButton("Add Candidate", "#3498db");
        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Name", 
                    "Please enter a candidate name.");
                return;
            }
            // Check for duplicates
            boolean exists = candidates.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
            if (exists) {
                showAlert(Alert.AlertType.WARNING, "Duplicate Candidate", 
                    "A candidate with this name already exists.");
                return;
            }
            candidates.add(new Candidate(name));
            nameField.clear();
            updateChart();
            VotingDataManager.saveCandidates(candidates);
        });

        Button removeBtn = createStyledButton("Remove", "#e74c3c");
        removeBtn.setOnAction(e -> {
            Candidate selected = candidateTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                    "Please select a candidate to remove.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Removal");
            confirm.setHeaderText("Remove Candidate");
            confirm.setContentText("Are you sure you want to remove:\n" + 
                selected.getName() + "?\n\nAll votes for this candidate will be lost.");
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    candidates.remove(selected);
                    updateChart();
                    VotingDataManager.saveCandidates(candidates);
                }
            });
        });

        Button resetBtn = createStyledButton("Reset Votes", "#f39c12");
        resetBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Reset All Votes");
            confirm.setHeaderText("Warning");
            confirm.setContentText("This will reset all votes to zero.\nThis action cannot be undone.\n\nContinue?");
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    candidates.forEach(c -> c.setVotes(0));
                    candidateTable.refresh();
                    updateChart();
                    VotingDataManager.saveCandidates(candidates);
                    showAlert(Alert.AlertType.INFORMATION, "Votes Reset", 
                        "All votes have been reset to zero.");
                }
            });
        });

        Button exportBtn = createStyledButton("Export CSV", "#27ae60");
        exportBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export Results as CSV");
            chooser.setInitialFileName("voting_results.csv");
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = chooser.showSaveDialog(owner);
            if (file != null) {
                if (VotingDataManager.exportToFile(candidates, file)) {
                    showAlert(Alert.AlertType.INFORMATION, "Export Successful", 
                        "Results exported to:\n" + file.getAbsolutePath());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Export Failed", 
                        "Could not export to file.");
                }
            }
        });

        Button logoutBtn = createStyledButton("Logout", "#95a5a6");
        logoutBtn.setOnAction(e -> {
            TabPane parent = (TabPane) owner.getScene().getRoot();
            Tab adminTab = parent.getTabs().get(1);
            adminTab.setContent(authPane);
        });

        controls.getChildren().addAll(title, new Separator(), nameField, addBtn, 
            removeBtn, resetBtn, exportBtn, new Separator(), logoutBtn);
        return controls;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefHeight(35);
        btn.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-background-radius: 18;
            -fx-cursor: hand;
            -fx-padding: 8px 16px;
        """, color));
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + 
            String.format("-fx-background-color: %s;", darkenColor(color))));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(
            String.format("-fx-background-color: %s;", darkenColor(color)),
            String.format("-fx-background-color: %s;", color))));
        return btn;
    }

    private String darkenColor(String color) {
        // Simple darkening - in production, use proper color manipulation
        return color.replace("#", "#80");
    }

    private TableView<Candidate> createCandidateTable() {
        TableView<Candidate> table = new TableView<>(candidates);
        table.setEditable(false);
        table.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
        """);

        TableColumn<Candidate, String> nameCol = new TableColumn<>("Candidate Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(300);
        nameCol.setStyle("-fx-font-size: 14px;");

        TableColumn<Candidate, Integer> votesCol = new TableColumn<>("Votes");
        votesCol.setCellValueFactory(new PropertyValueFactory<>("votes"));
        votesCol.setPrefWidth(150);
        votesCol.setStyle("-fx-font-size: 14px;");
        votesCol.setCellFactory(column -> new TableCell<Candidate, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item));
                    setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                }
            }
        });

        table.getColumns().setAll(nameCol, votesCol);
        return table;
    }

    private VBox createChartSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(15));
        section.setPrefWidth(400);
        section.setAlignment(Pos.TOP_CENTER);
        section.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
        """);

        Label title = new Label("Vote Statistics");
        title.setStyle("""
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            -fx-text-fill: #2c3e50;
        """);

        adminChart = createBarChart();
        updateChart();

        Button refreshBtn = createStyledButton("Refresh Chart", "#3498db");
        refreshBtn.setOnAction(e -> updateChart());

        section.getChildren().addAll(title, adminChart, refreshBtn);
        return section;
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Candidates");
        yAxis.setLabel("Votes");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Vote Distribution");
        chart.setLegendVisible(false);
        chart.setPrefSize(350, 400);
        return chart;
    }

    private void updateChart() {
        adminChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Candidate c : candidates) {
            series.getData().add(new XYChart.Data<>(c.getName(), c.getVotes()));
        }
        adminChart.getData().add(series);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        if (alertHandler != null) {
            alertHandler.accept(new AlertInfo(type, title, message));
        } else {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    public VBox getAuthPane() {
        return authPane;
    }

    public BorderPane getAdminPanel() {
        return adminPanel;
    }

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    public static class AlertInfo {
        public final Alert.AlertType type;
        public final String title;
        public final String message;

        public AlertInfo(Alert.AlertType type, String title, String message) {
            this.type = type;
            this.title = title;
            this.message = message;
        }
    }
}

