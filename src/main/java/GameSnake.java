/**
 * Simple Game Snake
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

public class GameSnake {

    final String TITLE_OF_PROGRAM = "Classic Game Snake";
    final String GAME_OVER_MSG = "GAME OVER";
    final int POINT_RADIUS = 20; // радиус точки в пикселях
    final int FIELD_WIDTH = 30; // ширина поля в точках
    final int FIELD_HEIGHT = 20;
    final int FIELD_DX = 6;
    final int FIELD_DY = 28;
    final int START_LOCATION = 200;
    final int START_SNAKE_SIZE = 6;
    final int START_SNAKE_X = 10;
    final int START_SNAKE_Y = 10;
    final int SHOW_DELAY = 150;

    final int LEFT = 37;
    final int RIGHT = 39;
    final int UP = 38;
    final int DOWN = 40;
    final int START_DIRECTION = RIGHT;

    final Color DEFAULT_COLOR = Color.black;
    final Color FOOD_COLOR = Color.green;
    final Color POISON_COLOR = Color.red;

    Snake snake;
    Food food;

    JFrame frame;
    Canvas canvasPanel;
    Random random = new Random();
    boolean gameOver = false;


    public static void main(String[] args) {
        new GameSnake().go();
    }

    public void go() {
        frame = new JFrame(TITLE_OF_PROGRAM + " : " + START_SNAKE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH * POINT_RADIUS + FIELD_DX, FIELD_HEIGHT * POINT_RADIUS + FIELD_DY);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);

        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                snake.setDirection(e.getKeyCode());
            }
        });

        frame.setVisible(true);

        snake = new Snake(START_SNAKE_X, START_SNAKE_Y, START_SNAKE_SIZE, START_DIRECTION);
        food = new Food();
        while (!gameOver) {
            snake.move(); // змейка пошла
            if (food.isEaten()) { // если была еда
                food.next(); // едим и даем новые координаты еде
            }
            canvasPanel.repaint();
            try {
                Thread.sleep(SHOW_DELAY); // задержка анимации
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Snake {
        List<Point> snake = new ArrayList<>();
        int direction;

        public Snake(int x, int y, int length, int direction) {
            for (int i = 0; i < length; i++) {
                Point point = new Point(x - i, y);
                snake.add(point);
            }
            this.direction = direction;
        }

        void paint(Graphics q) {
            for (Point point : snake) {
                point.paint(q);
            }
        }

        public boolean isInsideSnake(int x, int y) { // если часть змейки находится на переданной координате
            for (Point point : snake) {
                if ((point.getX() == x) && (point.getY() == y)) {
                    return true;
                }
            }
            return false;
        }

        boolean isFood(Point food) { // попала ли голова змейки на еду
            return (snake.get(0).getX() == food.getX() && snake.get(0).getY() == food.getY());
        }

        void move() {
            int x = snake.get(0).getX();
            int y = snake.get(0).getY();

            if (direction == LEFT) x--;
            if (direction == RIGHT) x++;
            if (direction == UP) y--;
            if (direction == DOWN) y++;

            if (x > FIELD_WIDTH - 1) x = 0; // если змейка заходит за границу поля, то она появляется из противоположной стороны
            if (x < 0) x = FIELD_WIDTH - 1;
            if (y > FIELD_HEIGHT - 1) y = 0;
            if (y < 0) y = FIELD_HEIGHT - 1;

            gameOver = isInsideSnake(x, y); // не укусила ли змейка саму себя
            snake.add(0, new Point(x, y)); // добавляем к змейке клетку по направлению

            if (isFood(food)) { // при этом если данная клетка была едой, то мы ее не укоротим сзади
                food.eat();
                frame.setTitle(TITLE_OF_PROGRAM + " : " + snake.size());
            } else {
                snake.remove(snake.size() - 1); // укоротим сзади
            }
        }

        public void setDirection(int direction) { // метод работает так, что нельзя двигаться в противоположном направлении
            if ((direction >= LEFT) && (direction <= DOWN)) {
                if (Math.abs(this.direction - direction) != 2) {
                    this.direction = direction;
                }
            }
        }
    }

    class Point {
        int x, y;
        Color color = DEFAULT_COLOR;

        public Point(int x, int y) {
            this.setXY(x, y);
        }

        void paint(Graphics q) {
            q.setColor(color);
            q.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class Food extends Point {
        public Food() {
            super(-1, -1);
            this.color = FOOD_COLOR;
        }

        public void eat() {
            this.setXY(-1, -1);

        }

        public boolean isEaten() {
            return this.getX() == -1;
        }

        void next() { // генерирует новую точку для еды
            int x,y;
            do {
                x= random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (snake.isInsideSnake(x,y));
            this.setXY(x,y);
        }
    }


    public class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            snake.paint(g);
            food.paint(g);
            if (gameOver) {
                g.setColor(Color.red);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(GAME_OVER_MSG, (FIELD_WIDTH * POINT_RADIUS + FIELD_DX - fm.stringWidth(GAME_OVER_MSG))/2, (FIELD_HEIGHT * POINT_RADIUS + FIELD_DY)/2);
            }
        }
    }


}
