package com.example.demo3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
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
    private Circle producerCircle;
    private Circle consumerCircle;
    private int producerCount = 0;
    private int consumerCount = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Producer-Consumer Simulation");

        producerCircle = createCircle(Color.GREEN);
        consumerCircle = createCircle(Color.RED);

        StackPane root = new StackPane();
        root.getChildren().addAll(producerCircle, consumerCircle);

        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();


        Thread producerThread = new Thread(this::produce);
        Thread consumerThread = new Thread(this::consume);

        producerThread.start();
        consumerThread.start();
    }

    private Circle createCircle(Color color) {
        Circle circle = new Circle(50, color);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        return circle;
    }

    private void produce() {
        while (producerCount < TOTAL_OPERATIONS) {
            try {
                String product = "Product #" + Math.random();
                buffer.put(product);

                System.out.println("Produced: " + product);

                Platform.runLater(() -> producerCircle.setFill(Color.GREEN));
                Thread.sleep(500);

                Platform.runLater(() -> producerCircle.setFill(Color.TRANSPARENT));
                Thread.sleep((long) (Math.random() * 2000));
                producerCount++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void consume() {
        while (consumerCount < TOTAL_OPERATIONS) {
            try {
                String product = buffer.take();

                System.out.println("Consumed: " + product);

                Platform.runLater(() -> consumerCircle.setFill(Color.RED));
                Thread.sleep(500);

                Platform.runLater(() -> consumerCircle.setFill(Color.TRANSPARENT));
                Thread.sleep((long) (Math.random() * 2000));
                consumerCount++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Platform.exit();
    }
}
