package com.example.thegalary2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application
{
    private Stage galleryStage;  // Second stage
    private List<String> imagePaths;
    private GridPane galleryGrid;
    private int currentIndex = 0;
    private ImageView fullImageView;

    @Override
    public void start(Stage primaryStage)
    {
        showWelcomeStage(primaryStage);
    }

    // FIRST STAGE - Welcome Screen
    private void showWelcomeStage(Stage primaryStage) {
        primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.UTILITY);

        Text hello = new Text("Hello There!");
        hello.setFont(new Font("Arial", 48));
        hello.setStyle("-fx-fill: linear-gradient(to right, #87CEEB, #FFFFFF); -fx-font-weight: bold;");

        Text welcome = new Text("Welcome To My Art Gallery.");
        welcome.setFont(new Font("Verdana", 24));
        welcome.setStyle("-fx-fill: #E0FFFF;");

        Button enterButton = new Button("Enter Gallery");
        enterButton.setStyle("""
                -fx-font-size: 16px;
                -fx-padding: 12px 24px;
                -fx-background-color: linear-gradient(to bottom, #4682B4, #20B2AA);
                -fx-text-fill: white;
                -fx-background-radius: 25px;
                -fx-border-radius: 8px;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 0, 5);
            """);

        enterButton.setOnMouseEntered(e -> enterButton.setStyle("""
                -fx-font-size: 18px;
                -fx-padding: 14px 28px;
                -fx-background-color: linear-gradient(to bottom, #20B2AA, #4682B4);
                -fx-text-fill: white;
                -fx-background-radius: 25px;
                -fx-border-radius: 8px;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 12, 0, 0, 6);
            """));

        enterButton.setOnMouseExited(e -> enterButton.setStyle("""
                -fx-font-size: 16px;
                -fx-padding: 12px 24px;
                -fx-background-color: linear-gradient(to bottom, #4682B4, #20B2AA);
                -fx-text-fill: white;
                -fx-background-radius: 25px;
                -fx-border-radius: 8px;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 0, 5);
            """));

        enterButton.setOnAction(e -> showGallery());

        VBox layout = new VBox(20, hello, welcome, enterButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1C1C1C, #282828);");

        Scene welcomeScene = new Scene(layout, 800, 600);
        primaryStage.setTitle("Welcome");
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }
    // SECOND STAGE - Image Gallery
    private void showGallery()
    {
        galleryStage = new Stage();
        galleryStage.initStyle(StageStyle.UTILITY);//removes maximum button

        imagePaths = loadImages("images");

        galleryGrid = new GridPane();
        galleryGrid.setHgap(11);
        galleryGrid.setVgap(11);
        galleryGrid.setPadding(new Insets(20));
        galleryGrid.setAlignment(Pos.CENTER);

        int col = 0, row = 0;
        for (String path : imagePaths)
        {
            ImageView thumbnail = createThumbnail(path);
            galleryGrid.add(thumbnail, col, row);
            col++;
            if (col >= 3) { col = 0; row++; }
        }

        VBox root = new VBox(15, galleryGrid);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

        Scene galleryScene = new Scene(root, 800, 600);
        galleryStage.setTitle("Image Gallery");
        galleryStage.setScene(galleryScene);
        galleryStage.show();
    }

    // Create thumbnails
    private ImageView createThumbnail(String imagePath)
    {
        Image image = new Image(new File(imagePath).toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setStyle("-fx-cursor: hand;");
        imageView.setFitWidth(150); //Set them to same Width
        imageView.setFitHeight(150); //Set them to same Height
        imageView.setPreserveRatio(false);  // Force same size
        imageView.setSmooth(true);

        imageView.setOnMouseEntered(e -> imageView.setScaleX(1.1));//makes Thumbnails hover
        imageView.setOnMouseExited(e -> imageView.setScaleX(1.0));//makes Thumbnails return to normal size

        imageView.setOnMouseClicked(e -> showFullImageView(imagePath));
        return imageView;
    }

    // Show full image
    private void showFullImageView(String imagePath) {
        currentIndex = imagePaths.indexOf(imagePath);
        fullImageView = new ImageView();
        updateFullImage();

        Button prevButton = new Button("◀");
        prevButton.setOnAction(e -> showPreviousImage());

        Button nextButton = new Button("▶");
        nextButton.setOnAction(e -> showNextImage());

        Button backButton = new Button("Back to Gallery");
        backButton.setOnAction(e -> showGallery());

        HBox navigation = new HBox(10, prevButton, backButton, nextButton);
        navigation.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: black; -fx-padding: 20px;");

        // Arrange thumbnails in a single row (using GridPane with one row)
        GridPane thumbnailLayout = new GridPane();
        thumbnailLayout.setHgap(10);
        thumbnailLayout.setAlignment(Pos.CENTER);

        int col = 0;
        for (javafx.scene.Node node : galleryGrid.getChildren()) {
            if (node instanceof ImageView) {
                ImageView originalThumbnail = (ImageView) node;
                // Create a copy of the thumbnail
                ImageView thumbnailCopy = new ImageView(originalThumbnail.getImage());
                thumbnailCopy.setFitWidth(75);
                thumbnailCopy.setFitHeight(75);

                // Make the thumbnail copy clickable
                final int index = col;
                thumbnailCopy.setOnMouseClicked(e -> {
                    currentIndex = index;
                    updateFullImage();
                });

                // Add hover effect and hand cursor
                thumbnailCopy.setOnMouseEntered(e -> {
                    thumbnailCopy.setScaleX(1.1);
                    thumbnailCopy.setScaleY(1.1);
                });
                thumbnailCopy.setOnMouseExited(e -> {
                    thumbnailCopy.setScaleX(1.0);
                    thumbnailCopy.setScaleY(1.0);
                });
                thumbnailCopy.setCursor(javafx.scene.Cursor.HAND);

                thumbnailLayout.add(thumbnailCopy, col, 0);
                col++;
            }
        }

        mainLayout.getChildren().add(fullImageView);
        mainLayout.getChildren().add(thumbnailLayout);
        mainLayout.getChildren().add(navigation);

        Scene scene = new Scene(mainLayout, 800, 600);
        galleryStage.setScene(scene);
    }

    // Update full image view
    private void updateFullImage()
    {
        if (currentIndex >= 0 && currentIndex < imagePaths.size())
        {
            Image image = new Image(new File(imagePaths.get(currentIndex)).toURI().toString());
            fullImageView.setImage(image);
            fullImageView.setFitWidth(400);
            fullImageView.setFitHeight(400);
            fullImageView.setPreserveRatio(false);
        }
    }

    // Navigate images
    private void showPreviousImage()
    {
        if (currentIndex > 0)
        {
            currentIndex--;
            updateFullImage();
        }
        else
        {
            currentIndex = imagePaths.size() - 1; // Loop back to the last image
        }
    }

    private void showNextImage() {
        if (currentIndex < imagePaths.size() - 1)
        {
            currentIndex++;
            updateFullImage();
        }
        else
        {
            currentIndex = 0; // Loop back to the last image
        }
    }

    // Load images from folder
    private List<String> loadImages(String folderPath)
    {
        File folder = new File(folderPath);
        List<String> paths = new ArrayList<>();
        if (folder.exists() && folder.isDirectory())
        {
            for (File file : folder.listFiles())
            {
                if (file.isFile() && file.getName().matches(".*\\.(jpg|png|jpeg)"))
                {
                    paths.add(file.getAbsolutePath());
                }
            }
        }
        return paths;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}