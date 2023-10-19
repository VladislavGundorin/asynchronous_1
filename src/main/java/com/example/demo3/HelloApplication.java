package com.example.demo3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class HelloApplication extends Application {
    private static final int BUFFER_SIZE = 5;
    private static final int TOTAL_OPERATIONS = 15;
    private BlockingQueue<String> buffer = new ArrayBlockingQueue<>(BUFFER_SIZE);
    private int producerCount = 0;
    private int consumerCount = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Producer-Consumer");

        Circle producerCircle = createCircle(Color.GREEN);
        Circle consumerCircle = createCircle(Color.RED);

        HBox hbox = new HBox(20);
        hbox.getChildren().addAll(producerCircle, consumerCircle);

        StackPane root = new StackPane();
        root.getChildren().add(hbox);

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();

        Thread producerThread = new Thread(() -> produce(producerCircle));
        Thread consumerThread = new Thread(() -> consume(consumerCircle));

        producerThread.start();
        consumerThread.start();


        primaryStage.setOnCloseRequest(event -> {
            producerThread.interrupt();
            consumerThread.interrupt();
            Platform.exit();
        });
    }

    private Circle createCircle(Color color) {
        Circle circle = new Circle(50, color);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        return circle;
    }

    private void produce(Circle circle) {
        while (producerCount < TOTAL_OPERATIONS) {
            try {
                String product = "Product #" + Math.random();
                Thread.sleep((long) (Math.random() * 1000));
                Platform.runLater(() -> circle.setFill(Color.BLUE));
                Thread.sleep(5);
                buffer.put(product);

                System.out.println("Produced: " + product);

                Platform.runLater(() -> circle.setFill(Color.GREEN));
                Thread.sleep(5);

                Platform.runLater(() -> circle.setFill(Color.TRANSPARENT));
                Thread.sleep((long) (Math.random() * 2000));
                producerCount++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void consume(Circle rectangle) {
        while (consumerCount < TOTAL_OPERATIONS) {
            try {
                Platform.runLater(() -> rectangle.setFill(Color.RED));
                Thread.sleep(5);

                String product = buffer.take();
                Platform.runLater(() -> rectangle.setFill(Color.GREEN));
                Thread.sleep(5);

                System.out.println("Consumed: " + product);

                Platform.runLater(() -> rectangle.setFill(Color.TRANSPARENT));
                Thread.sleep((long) (Math.random() * 2000)+1000);
                consumerCount++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        Platform.exit();
    }
}
