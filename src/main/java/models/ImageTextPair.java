package models;

import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ImageTextPair {
    private ImageView imageView;
    private Text text;

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Text getText() {
        return text;
    }
}
