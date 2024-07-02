package models;

import javafx.scene.layout.AnchorPane;

public class ImageTextAnchorTriple extends ImageTextPair {
    private AnchorPane anchorPane;

    public void setAnchorPane(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }
}
