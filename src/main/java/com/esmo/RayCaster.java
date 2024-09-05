package com.esmo;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class RayCaster {
    private double sourceX = 0;
    private double sourceY = 0;

    private int rayCount = 500;
    private int rayLength = 500;
    private double raySpread = 45;
    private double rayOffset = 0;

    public List<Line> generateRays() {
        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < rayCount; i++) {
            double angle = i * (raySpread / rayCount);
            double angleRadians = Math.toRadians(angle + rayOffset);
            double endX = sourceX + Math.cos(angleRadians) * rayLength;
            double endY = sourceY + Math.sin(angleRadians) * rayLength;
            Line line = new Line(sourceX, sourceY, endX, endY);
            line.setStroke(Color.WHITE);
            lines.add(line);
        }
        return lines;
    }

    public void rotateRays(double centerDirection) {
        double goal = -(raySpread / 2) + centerDirection;
        int turnSpeed = 9;

        double difference = goal - rayOffset;
        if (difference > 180) {
            difference -= 360;
        } else if (difference < -180) {
            difference += 360;
        }

        if (Math.abs(difference) < turnSpeed) {
            rayOffset = goal;
        } else {
            if (difference > 0) {
                rayOffset += turnSpeed;
            } else {
                rayOffset -= turnSpeed;
            }
        }
        rayOffset = (rayOffset + 360) % 360;
    }

    public double getRayOffset() {
        return rayOffset;
    }

    public void setRayOffset(double rayOffset) {
        this.rayOffset = rayOffset;
    }

    public double getRaySpread() {
        return raySpread;
    }

    public void setRaySpread(double raySpread) {
        this.raySpread = raySpread;
    }

    public double getSourceX() {
        return sourceX;
    }

    public void setSourceX(double sourceX) {
        this.sourceX = sourceX;
    }

    public double getSourceY() {
        return sourceY;
    }

    public void setSourceY(double sourceY) {
        this.sourceY = sourceY;
    }

    public int getRayLength() {
        return rayLength;
    }

    public void setRayLength(int rayLength) {
        this.rayLength = rayLength;
    }

    public int getRayCount() {
        return rayCount;
    }

    public void setRayCount(int rayCount) {
        this.rayCount = rayCount;
    }
}
