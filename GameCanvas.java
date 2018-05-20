import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
public class GameCanvas extends JPanel implements ActionListener{
    private static transient GameCanvas gameCanvas = null;
    private static final int NUMBER_ENEMIES = 28;
    private Player player;
    private Player player2;
    private Player player3;
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private ArrayList<Laser> enemyLaserCleanUpList = new ArrayList<Laser>();
    private ArrayList<Laser> playerLaserCleanUpList = new ArrayList<Laser>();
    private static HashSet<GameObject> gameObjects = new HashSet<GameObject>();
    private int score = 0;
    private static HashMap<String, Image> imageCache = new HashMap<String, Image>();
    private long lastFire;
    private int enemyCount = NUMBER_ENEMIES;
    private int liveCount = 3;
    private int levelCount = 1;
    private Timer leftPressed = new Timer(10, new LeftPressed());
    private Timer rightPressed = new Timer(10, new RightPressed());
    private Timer gameTimer = new Timer(20, this);
    private Timer enemyFireTimer = new Timer(1000, new MyFireListener());
    private Timer respawn = new Timer(4520, new redrawPlayer());
    private GameCanvas() {
            super();
            setBackground(Color.BLACK);
            setDoubleBuffered(true);
            setFocusable(true);
            initialiseShips();
            createPlayer();
            addKeyListener(keyListener);
            gameTimer.start();
            enemyFireTimer.start();
            this.setBackground(Color.BLACK);
    }
    public static GameCanvas getGameCanvas(boolean whichCanvas) {
        if (gameCanvas == null && whichCanvas == false) {
            gameCanvas = new GameCanvas();
        }
        if (gameCanvas == null && whichCanvas == true) {
            gameCanvas = new GameCanvas();
        }
        return gameCanvas;
    }
    private void initialiseShips() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j<4; j++)
            {
                Enemy e = new Enemy("Resources/enemy.jpg", new Point(100 + i*50, (50)+j*30), new Dimension(50, 30));
                enemies.add(e);
                gameObjects.add(e);
            }
        }
    }
    private void createPlayer() {
        player = new Player("Resources/Ship.png", new Point(300, 450), new Dimension(60, 30));
        gameObjects.add(player);
        player3 = new Player("Resources/river.jpg", new Point(0, 500), new Dimension(600, 79));
        gameObjects.add(player3);
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.drawString("Lives:  " + liveCount, 500, 30);
        g.drawString("Level: " + levelCount, 270, 30);
        g.drawString("Pollutants Eliminated:  " + score, 50, 30);
        Iterator<GameObject> itt = gameObjects.iterator();
        while (itt.hasNext()) {
            GameObject current = itt.next();
            if (!current.isActive())
                continue;
            current.draw(g);
        }
    }
    @Override
    public void actionPerformed(ActionEvent event) {
        int moveY = 0;
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).switchDirection()) {
                moveY = 10;
                break;
            }
            else 
                moveY = 0;
        }
        Iterator<GameObject> itt = gameObjects.iterator();
        while (itt.hasNext()) {
            GameObject current = itt.next();
            if (current instanceof Player) 
                continue;
            current.move(0, moveY);
        }
        collision();
        cleanUp();
        repaint();
    }
    public Enemy[] getEnemies() {
        return (Enemy[]) enemies.toArray();
    }
    public static void addGameObject(GameObject obj) {
        gameObjects.add(obj);
    }
    public static void removeGameObject(GameObject obj) {
        if (gameObjects.contains(obj))
            gameObjects.remove(obj);
    }
    private void collision() {  
        for (Enemy e : enemies) {
            Iterator<Laser> laserItt = Player.laserList.iterator();
            while (laserItt.hasNext()) {
                Laser currentLaser = (Laser) laserItt.next();
                if (currentLaser.getRectangle().intersects(e.getRectangle()) && currentLaser.isActive()) {
                    currentLaser.setActive(false);
                    playerLaserCleanUpList.add(currentLaser);
                    score+=25;
                    e.setActive(false);
                    enemyCount--;
                }
            }
            if (enemyCount == 0) {;
                liveCount++;
                levelCount++;
                enemyCount = NUMBER_ENEMIES;
            }
        }
        Iterator<Laser> laserItt = Enemy.laserList.iterator();
         while (laserItt.hasNext()) {
            Laser currentLaser = (Laser) laserItt.next();
            if (player.isActive()) {
                if (currentLaser.getRectangle().intersects(player.getRectangle()) && currentLaser.isActive()) {
                    currentLaser.setActive(false);
                    enemyLaserCleanUpList.add(currentLaser);
                    enemyFireTimer.stop();
                    gameTimer.stop();
                    respawn.start();
                    player.setActive(false);
                    if(liveCount == 3){
                    JOptionPane.showMessageDialog(null,"Fourteen billion pounds of garbage mostly plastic, is dumped into the ocean every year." , "Did you know?", JOptionPane.PLAIN_MESSAGE);
                }
                 if(liveCount == 2){
                    JOptionPane.showMessageDialog(null,"Water pollution is the major cause of various diseases like cholera and typhoid." , "Did you know?", JOptionPane.PLAIN_MESSAGE);
                }
                 if(liveCount == 1){
                    JOptionPane.showMessageDialog(null,"On an average 250 million people worldwide succumb to diseases related to water pollution." , "Did you know?", JOptionPane.PLAIN_MESSAGE);
                }
                if (liveCount >=  1) 
                    liveCount--;
                }
            }
            
            if (player3.isActive()) {
                if (currentLaser.getRectangle().intersects(player3.getRectangle()) && currentLaser.isActive()) {
                    score--;
            }
        }
    }
    }
    private void cleanUp() {
        ArrayList<Enemy> al = new ArrayList<Enemy>();
        for (Enemy e : enemies) {
            if (!e.isActive()) 
                al.add(e);
        }
        enemies.removeAll(al);
        Player.laserList.removeAll(playerLaserCleanUpList);
        Enemy.laserList.removeAll(enemyLaserCleanUpList);
    }
    public static Image getImage(String location, GraphicsConfiguration gc) throws IOException {
        Image img = null;
        if (imageCache.containsKey(location))
            return imageCache.get(location);
        else {
            Image sourceImg;
            sourceImg = ImageIO.read(new File(location));
            img = gc.createCompatibleImage(sourceImg.getWidth(null), sourceImg.getHeight(null), Transparency.BITMASK);
            img.getGraphics().drawImage(sourceImg, 0, 0, null);
            imageCache.put(location, img);
        }
        return img;
    }
    public class MyFireListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (enemies.size() == 0)
                return;
            else {
                int randomNumber = new Random().nextInt(enemies.size());
                enemies.get(randomNumber).fire();
            }
        }
    }
    KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    rightPressed.start();
                    if (player.getPosition().x < 0) {
                        rightPressed.stop();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    leftPressed.start();
                    if (player.getPosition().x > 550) {
                        leftPressed.stop();
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    rightPressed.stop();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    leftPressed.stop();
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    int delay = 800;
                    if (System.currentTimeMillis() - lastFire < delay) {
                        return;
                    }
                    if (player.isActive()) {
                        player.fire();
                        lastFire = System.currentTimeMillis();
                    }
                }
            }
            @Override
            public void keyTyped(KeyEvent e) {}
        };
    public class RightPressed implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (player.isActive())
                player.move(player.getPosition().x-5, 0);
        }
    }
    public class LeftPressed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (player.isActive())
                player.move(player.getPosition().x+5, 0);
        }
    }
    public class redrawPlayer implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            respawn.stop();
            if (liveCount != 0) {
                createPlayer();
                player.setActive(true);
                enemyFireTimer.start();
                gameTimer.start();
            }
        }
    }
}

