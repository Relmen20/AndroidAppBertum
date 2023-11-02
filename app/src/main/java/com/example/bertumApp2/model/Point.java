package com.example.bertumApp2.model;

public class Point {
    private float x;
    private float y;

    public Point(Float x, Float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "\nPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
