/**
 * Voting System Module
 * A professional JavaFX application for managing elections and votes.
 */
module miniproject {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;

    opens miniproject to javafx.graphics;
    exports miniproject;
}
