package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
public class myPanel extends JPanel implements KeyListener, MouseListener {

    Image backgroundImage;
    Image playerImage;

    Image projectileImage;
    Image groundImage;

    Image enemyImage;


    // Array som håller olika graphics baserat på väder
    ArrayList<BufferedImage> backgroundArray = new ArrayList<>();

    ArrayList<BufferedImage> projectileArray = new ArrayList<>();
    ArrayList<BufferedImage> groundArray = new ArrayList<>();
    ArrayList<BufferedImage> playerArray = new ArrayList<>();


    // Storlek på frame = skärmens storlek, hämtar in igen för att kunna använda dimensioner
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    // Storlek på höjd/bredd
    double screenRow = screenSize.getWidth();
    double screenCol = screenSize.getHeight();


    // spelare storlek
    int playerWidth = (int) (screenRow * 0.1);
    int playerHeight = (int) (screenCol * 0.2);

    // spelare hastighet

    int playerSpeed = 60;
    int playerJump = 60;

    // om denna slås på kan spelaren bygga med material
    Boolean building = false;


    int playerX;
    int playerY;
    ImageIcon treeImage;

    int treeScore = 0;

    int treeX = 300;
    int treeY = 200;
    //JPanel blue = new JPanel();

    int enemyX = 250;
    int enemyY = 0;

    int projectileX = 250;
    int projectileY = 0;
    boolean treeAlive = true;

    // Håller koll på material (gör om till mätare)
    JLabel treeScoreLabel = new JLabel(String.valueOf(treeScore));



    public myPanel() throws IOException {



         //Bakgrunder
        BufferedImage frozenBackground = ImageIO.read(new File("org/example/snowyLevel.png"));
        BufferedImage sunnyBackground = ImageIO.read(new File("org/example/sunnyLevel.png"));
        BufferedImage stormyBackground = ImageIO.read(new File("org/example/stormyLevel.png"));
        // Finns bättre sätt att hämta nivåer?
        backgroundArray.add(frozenBackground);
        backgroundArray.add(sunnyBackground);
        backgroundArray.add(stormyBackground);

        // Börjar skicka ner projektiler mot spelare
        sendProjectileTimer.start();

        // Spelare
         playerImage = new ImageIcon("org/example/playerStatic.jpg").getImage();
         // material att samla
         treeImage = new ImageIcon("org/example/tree.png");
         // fiende array ska vara här
         enemyImage = new ImageIcon("org/example/enemyStatic.png").getImage();
         // projektil array ska vara här
         projectileImage = new ImageIcon(("org/example/lightningBolt.png")).getImage();
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        //TreeScoreLabel  - skapa en metod som uppdaterar
        treeScoreLabel.setForeground(Color.WHITE);
        add(treeScoreLabel);

        /*// Mark
        JPanel groundPanel = new JPanel();
        // 30% längd, 20% höjd av skärmen
        double groundRow = screenRow * 0.5;
        double groundCol = screenCol * 0.2;
        groundPanel.setPreferredSize(new Dimension((int) groundRow, (int) groundCol));
        add(groundPanel, BorderLayout.SOUTH);*/





     }
    // gör sen till en väder baserad background
    public void weatherBackground() {
        Random random = new Random();
        int randomBackground = random.nextInt(backgroundArray.size());
        backgroundImage = new ImageIcon(backgroundArray.get(randomBackground)).getImage();
        repaint();
     }


     // ritar alla objekt i frame
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        // nedre högra hörn - sätts här eftersom frame storlek har beräknats redan nät metod kallad

        // bakgrund flrst - har skärmens höjd/bredd
        graphics2D.drawImage(backgroundImage, 0,0, getWidth(),getHeight(), this); //bakgrund
        g.drawImage(treeImage.getImage(), treeX, treeY,150,150,null); // mark
        g.drawImage(playerImage, playerX, playerY,playerWidth,playerHeight,this); // mark
        g.drawImage(enemyImage, enemyX, enemyY,playerWidth,playerHeight,null); // mark
        g.drawImage(projectileImage, projectileX, projectileY,50,50,null); // mark
        // Rektanglar som används för obstruction handling och intersects
        Rectangle playerRect = new Rectangle(playerX,playerY,playerWidth,playerHeight);
        Rectangle projectileRect = new Rectangle(projectileX,projectileY,playerWidth,playerHeight);
        Rectangle treeRect = new Rectangle(treeX,treeY,playerWidth,playerHeight);
        Timer treeTimer = new Timer(20000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treeAlive = true;
                treeImage = new ImageIcon("org/example/tree.png");
            }
        });
        // interaktion med träd - endast möjligt om träd är aktivt för att förhindra fusk
        if (playerRect.intersects(treeRect) && treeAlive){
            playerImage = new ImageIcon("org/example/playerChopping.jpg").getImage();
            playerX = playerX - playerSpeed;
            // ökar antal trä samlade
            treeScore++;
            //uppdaterar label
            treeScoreLabel.setText(String.valueOf(treeScore));
            // om träscore blir 5 ( max antral samlat ), sänks trä rektangelns bounds till 0
            if (treeScore > 5){
                treeImage = new ImageIcon("org/example/choppedDown.png");
                treeAlive = false;
                treeTimer.start();
            };
        }

        if (building){
            g.drawImage(projectileImage, playerX + 10, playerY,50,50,null); // mark
            Rectangle buildRect = new Rectangle(playerX + 10,playerY + 10,playerWidth,playerHeight);
            if(projectileRect.intersects(buildRect)){
                building = false;
            }

        /*g.drawImage(); // spelare
        g.drawImage(); // fiende
        g.drawImage(); // projektil*/

    }}


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
         int keyCode = e.getKeyCode();
    if(keyCode == KeyEvent.VK_D){
        playerX = playerX + playerSpeed;
        playerImage = new ImageIcon("org/example/playerRunning.jpg").getImage();
        repaint();
    }
        if(keyCode == KeyEvent.VK_A){
            playerX = playerX - playerSpeed;
            playerImage = new ImageIcon("org/example/playerLeft.jpg").getImage();
            repaint();
        }

        if(keyCode == KeyEvent.VK_S){
            playerY = playerY + playerJump;
            playerImage = new ImageIcon("org/example/playerRunning.jpg").getImage();
            repaint();
        }
        if(keyCode == KeyEvent.VK_W){
            playerY = playerY - playerJump;
            playerImage = new ImageIcon("org/example/playerRunning.jpg").getImage();
            repaint();
        }
        if(keyCode == KeyEvent.VK_B){
            while (treeScore > 0){
                // medans treescore är mer än 0 kan man kalla metod
                System.out.println("building");
                treeScore--;

                if (treeScore <= 6){
                       building = true;
                    } else {
                        System.out.println("Not enough materials");
                    }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_D){
            playerX = playerX + playerSpeed;
            playerImage = new ImageIcon("org/example/playerStatic.jpg").getImage();
            repaint();
        }

        if(keyCode == KeyEvent.VK_A){
            playerX = playerX - playerSpeed;
            playerImage = new ImageIcon("org/example/playerStaticLeft.png").getImage();
            repaint();
        }

        if(keyCode == KeyEvent.VK_S){
            playerY = playerY + playerJump;
            playerImage = new ImageIcon("org/example/playerRunning.jpg").getImage();
            repaint();
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
       /* blue.setBackground(Color.CYAN);
        int x = e.getX();
        int y = e.getY();
        blue.setBounds(x,y,100,100);
        setLayout(null);
        add(blue);
        repaint();
        revalidate();*/

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void buildTree(){

    }


    Timer sendingProjectileTimer = new Timer(100, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        sendProjectileTimer.start();
        }
    });
    Timer sendProjectileTimer = new Timer(4, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //metod som skickar badboll

            repaint();
        }
    });


}