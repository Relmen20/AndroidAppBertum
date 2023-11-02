package com.example.bertumApp2.model;

import java.util.List;
import java.util.Random;

public class Polygon {
    private final String name;
    private final String rusName;
    private final String article;
    private final String color;
    private List<Point> points;
    private float area;
    private boolean isVisible, isPicked;

    public Polygon(String name, String article, List<Point> points, String rusName) {
        this.name = name;
        this.article = article;
        this.points = points;
        this.rusName = rusName;
        this.color = getRandomColor();
        this.area = calculateArea(points);
        this.isVisible = true;
        this.isPicked = false;
    }

    private float calculateArea(List<Point> points) {

        return 2f;

    }

    public String getRusName() {
        return rusName;
    }

    public String getName() {
        return name;
    }

    public String getArticle() {
        return article;
    }

    public String getColor() {
        return color;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "name='" + name + '\'' +
                ", article='" + article + '\'' +
                ", color='" + color + '\'' +
                ", points=" + points +
                ", area=" + area +
                ", isVisible=" + isVisible +
                ", isPicked=" + isPicked +
                '}';
    }

    private String getRandomColor() {
        String letters = "0123456789ABCDEF";
        StringBuilder color = new StringBuilder("#");
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(16);
            color.append(letters.charAt(index));
        }
//       (int) Long.parseLong(color.substring(1), 16)
        return color.toString();
    }
}