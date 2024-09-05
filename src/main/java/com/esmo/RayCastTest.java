package com.esmo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RayCastTest extends Application {
    private int numberOfSquares = 20;
    private Pane pane;
    private List<Node> blocks = new ArrayList<>();
    private boolean up;
    private boolean left;
    private boolean down;
    private boolean right;
    private int movementAngle;
    private boolean lookUp;
    private boolean lookLeft;
    private boolean lookDown;
    private boolean lookRight;
    private RayCaster rays = new RayCaster();
    private boolean useKeyboard;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        pane = new Pane();
        Scene scene = new Scene(pane);
        scene.setFill(Color.rgb(40, 40, 40));
        scene.setFill(Color.BLACK);

        scene.setOnMouseMoved(this::handleMouse);
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        primaryStage.setScene(scene);
        primaryStage.setTitle("RayCastTest");
        primaryStage.show();

        blocks = generateBlocks();
        AnimationTimer animationTimer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                calculateRayDirection();
                moveObject();
                render();
            }

        };

        animationTimer.start();
    }

    private void moveObject() {
        if (up) {
            moveUp();
        }
        if (down) {
            moveDown();
        }
        if (left) {
            moveLeft();
        }
        if (right) {
            rightMove();
        }
    }

    private void rightMove() {
        boolean collide = false;
        double newX = rays.getSourceX() + 5;
        for (Node node : blocks) {
            Rectangle block = (Rectangle) node;
            if (rays.getSourceY() > block.getY() && rays.getSourceY() < block.getY() + block.getHeight()) {
                if (newX > block.getX() && newX < block.getX() + block.getWidth()) {
                    collide = true;
                }
            }
        }
        if (!collide) {
            rays.setSourceX(newX);
        }
    }

    private void moveLeft() {
        boolean collide = false;
        double newX = Math.max(rays.getSourceX() - 5, 0);
        for (Node node : blocks) {
            Rectangle block = (Rectangle) node;
            if (rays.getSourceY() > block.getY() && rays.getSourceY() < block.getY() + block.getHeight()) {
                if (newX > block.getX() && newX < block.getX() + block.getWidth()) {
                    collide = true;
                }
            }
        }
        if (!collide) {
            rays.setSourceX(newX);
        }
    }

    private void moveDown() {
        boolean collide = false;
        double newY = rays.getSourceY() + 5;
        for (Node node : blocks) {
            Rectangle block = (Rectangle) node;
            if (rays.getSourceX() > block.getX() && rays.getSourceX() < block.getX() + block.getWidth()) {
                if (newY > block.getY() && newY < block.getY() + block.getHeight()) {
                    collide = true;
                }
            }
        }
        if (!collide) {
            rays.setSourceY(newY);
        }
    }

    private void moveUp() {
        boolean collide = false;
        double newY = Math.max(rays.getSourceY() - 5, 0);
        for (Node node : blocks) {
            Rectangle block = (Rectangle) node;
            if (rays.getSourceX() > block.getX() && rays.getSourceX() < block.getX() + block.getWidth()) {
                if (newY > block.getY() && newY < block.getY() + block.getHeight()) {
                    collide = true;
                }
            }
        }
        if (!collide) {
            rays.setSourceY(newY);
        }
    }

    private void calculateRayDirection() {
        if (lookDown || lookLeft || lookRight || lookUp) {
            if (lookUp && lookLeft) {
                movementAngle = 225;
            } else if (lookUp && lookRight) {
                movementAngle = 315;
            } else if (lookDown && lookLeft) {
                movementAngle = 135;
            } else if (lookDown && lookRight) {
                movementAngle = 45;
            } else if (lookUp) {
                movementAngle = 270;
            } else if (lookDown) {
                movementAngle = 90;
            } else if (lookLeft) {
                movementAngle = 180;
            } else if (lookRight) {
                movementAngle = 360;
            }
            return;
        }
        if (up && left) {
            movementAngle = 225;
        } else if (up && right) {
            movementAngle = 315;
        } else if (down && left) {
            movementAngle = 135;
        } else if (down && right) {
            movementAngle = 45;
        } else if (up) {
            movementAngle = 270;
        } else if (down) {
            movementAngle = 90;
        } else if (left) {
            movementAngle = 180;
        } else if (right) {
            movementAngle = 360;
        }
    }

    private void render() {
        pane.getChildren().clear();
        pane.getChildren().addAll(blocks);
        rays.rotateRays(movementAngle);

        for (Line line : rays.generateRays()) {
            // normal blocks
            handleNormalBlocks(line);

            pane.getChildren().add(line);

        }

        // location marker
        Circle mark = new Circle(5, Color.GREEN);
        pane.getChildren().add(mark);
        mark.setCenterX(rays.getSourceX());
        mark.setCenterY(rays.getSourceY());

    }

    private void handleNormalBlocks(Line line) {
        for (Node node : blocks) {
            if (line.intersects(node.getBoundsInLocal())) {
                Point2D newEnd = findIntersect(line, (Rectangle) node);
                Point2D start = new Point2D(line.getStartX(), line.getStartY());
                Point2D end = new Point2D(line.getEndX(), line.getEndY());
                if (newEnd != null && start.distance(newEnd) < start.distance(end)) {
                    line.setEndX(newEnd.getX());
                    line.setEndY(newEnd.getY());
                    line.setStroke(Color.RED);
                    line.setStroke(Color.WHITE);
                }
            }
        }
    }

    private void handleKeyPressed(KeyEvent key) {
        switch (key.getCode()) {
            case R:
                pane.getChildren().clear();
                blocks = generateBlocks();
                break;
            case ADD:
            case EQUALS:
                rays.setRayCount(rays.getRayCount() + 20);
                break;
            case MINUS:
            case UNDERSCORE:
                rays.setRayCount(Math.max(rays.getRayCount() - 20, 0));
                break;
            case CLOSE_BRACKET:
            case BRACERIGHT:
                rays.setRaySpread(Math.min(rays.getRaySpread() + 1, 360));
                break;
            case OPEN_BRACKET:
            case BRACELEFT:
                rays.setRaySpread(Math.max(rays.getRaySpread() - 1, 0));
                break;
            case BACK_SLASH:
                if (rays.getRaySpread() != 360) {
                    rays.setRaySpread(360);
                } else {
                    rays.setRaySpread(45);
                }
                break;
            case SEMICOLON:
            case COLON:
                rays.setRayOffset(rays.getRayOffset() + 1);
                break;
            case QUOTE:
            case QUOTEDBL:
                rays.setRayOffset(rays.getRayOffset() - 1);
                break;
            case COMMA:
            case LESS:
                rays.setRayLength(Math.max(rays.getRayLength() - 10, 0));
                break;
            case PERIOD:
            case GREATER:
                rays.setRayLength(rays.getRayLength() + 10);
                break;
            case W:
                if (useKeyboard) {
                    up = true;
                }
                break;
            case A:
                if (useKeyboard) {
                    left = true;
                }
                break;
            case S:
                if (useKeyboard) {
                    down = true;
                }
                break;
            case D:
                if (useKeyboard) {
                    right = true;
                }
                break;
            case UP:
                if (useKeyboard) {
                    lookUp = true;
                }
                break;
            case DOWN:
                if (useKeyboard) {
                    lookDown = true;
                }
                break;
            case LEFT:
                if (useKeyboard) {
                    lookLeft = true;
                }
                break;
            case RIGHT:
                if (useKeyboard) {
                    lookRight = true;
                }
                break;
            case T:
                useKeyboard = !useKeyboard;
            default:
                break;
        }
    }

    private void handleKeyReleased(KeyEvent key) {
        switch (key.getCode()) {
            case W:
                if (useKeyboard) {
                    up = false;
                }
                break;
            case A:
                if (useKeyboard) {
                    left = false;
                }
                break;
            case S:
                if (useKeyboard) {
                    down = false;
                }
                break;
            case D:
                if (useKeyboard) {
                    right = false;
                }
                break;
            case UP:
                if (useKeyboard) {
                    lookUp = false;
                }
                break;
            case DOWN:
                if (useKeyboard) {
                    lookDown = false;
                }
                break;
            case LEFT:
                if (useKeyboard) {
                    lookLeft = false;
                }
                break;
            case RIGHT:
                if (useKeyboard) {
                    lookRight = false;
                }
                break;
            default:
                break;
        }
    }

    private List<Node> generateBlocks() {
        List<Node> blocks = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numberOfSquares; i++) {
            double x = random.nextDouble() * pane.getWidth();
            double y = random.nextDouble() * pane.getHeight();
            double width = random.nextDouble() * 200 + 20;
            double height = random.nextDouble() * 200 + 20;
            Rectangle rect = new Rectangle(x, y, width, height);
            rect.setFill(Color.BLACK);
            blocks.add(rect);
        }
        return blocks;
    }

    private void handleMouse(MouseEvent mouse) {
        if (!useKeyboard) {
            rays.setSourceX(mouse.getX());
            rays.setSourceY(mouse.getY());
        }
    }

    private Point2D findIntersect(Line line, Rectangle node) {

        Point2D startPoint = new Point2D(line.getStartX(), line.getStartY());
        double boxLeftX = node.getX();
        double boxTopY = node.getY();
        double boxRightX = node.getX() + node.getWidth() - .1;
        double boxBottomY = node.getY() + node.getHeight() - .1;

        double lineSlope = (line.getEndY() - line.getStartY()) / (line.getEndX() - line.getStartX());
        double yintercept = line.getStartY() - lineSlope * line.getStartX();

        Point2D left = new Point2D(boxLeftX, lineSlope * boxLeftX + yintercept);
        Point2D right = new Point2D(boxRightX, lineSlope * boxRightX + yintercept);
        Point2D top = lineSlope == Double.POSITIVE_INFINITY ? new Point2D(line.getStartX(), boxTopY)
                : new Point2D((boxTopY - yintercept) / lineSlope, boxTopY);
        Point2D bottom = lineSlope == Double.NEGATIVE_INFINITY ? new Point2D(line.getStartX(), boxBottomY)
                : new Point2D((boxBottomY - yintercept) / lineSlope, boxBottomY);

        double leftDistance = startPoint.distance(left);
        double rightDistance = startPoint.distance(right);
        double topDistance = startPoint.distance(top);
        double bottomDistance = startPoint.distance(bottom);

        Point2D closestIntersection = null;
        double shortestDistance = Double.MAX_VALUE;

        if (node.contains(left) && leftDistance < shortestDistance) {
            closestIntersection = left;
            shortestDistance = leftDistance;
        }
        if (node.contains(top) && topDistance < shortestDistance) {
            closestIntersection = top;
            shortestDistance = topDistance;
        }
        if (node.contains(right) && rightDistance < shortestDistance) {
            closestIntersection = right;
            shortestDistance = rightDistance;
        }
        if (node.contains(bottom) && bottomDistance < shortestDistance) {
            closestIntersection = bottom;
            shortestDistance = bottomDistance;
        }

        return closestIntersection;
    }

}