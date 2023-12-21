package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Random;
public class myPanel extends JPanel implements KeyListener {

    Image backgroundImage;
    Image playerImage;

    Image projectileImage;
    Image groundImage;

    Image enemyImage;
    Image pileImage;
    int pileIndex = 0;

    // Array som håller olika graphics baserat på väder
    ArrayList<BufferedImage> backgroundArray = new ArrayList<>();

    ArrayList<BufferedImage> projectileArray = new ArrayList<>();
    ArrayList<BufferedImage> pileArray = new ArrayList<>();
    ArrayList<BufferedImage> altarArray = new ArrayList<>();


    // Storlek på frame = skärmens storlek, hämtar in igen för att kunna använda dimensioner
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    // Storlek på höjd/bredd
    double screenRow = screenSize.getWidth();
    double screenCol = screenSize.getHeight();

    boolean building = false;

    // spelare storlek
    int playerWidth = (int) (screenRow * 0.1);
    int playerHeight = (int) (screenCol * 0.2);

    // spelare hastighet

    int playerSpeed = 20;


    // om denna slås på kan spelaren bygga med material
    Boolean shielding = false;
    Boolean carryWood = false;

    // 0 på X linje
    int playerX;

    // sätter spelaren längst ned i vänstra hörnet som startpunkt
    int playerY = (int) screenCol - playerHeight;

    // träd
    ImageIcon treeImage;


    BufferedImage altarImage;

    // 40% av skärmhöjd
    int treeHeight = (int) ((int) screenCol * 0.4);
    // 20% av skärmbredd
    int treeWidth = (int) ((int) screenRow * 0.2);

    // håller ordning på antal träd samlat
    int treeScore = 0;

    // träd koordinater
    // längst ned på x linjen  ----  tar totala värdet av hela x linjen minus storleken på bilden så att den placeras i absolut högra hörnet
    int treeX = (int) screenRow - treeWidth;
    // längst ned på y linjen -- samma princip som ovan , tar absoluta värdet av Y linjen - höjden på objekt
    int treeY = (int) screenCol - treeHeight;


    // byggplats

    int pileHeight = (int) (screenCol * 0.2);
    int pileWidth = (int) (screenRow * 0.2);

    // sätter den på mitten av x
    int pileX = (int) (((int) screenRow * 0.5) - ((int) pileWidth * 0.5));
    // längst ned på y linjen
    int pileY = (int) screenCol - pileHeight;



    // om träd är levande eller ej - används för att reglera mängd material som kan samlas från träd
    boolean treeAlive = true;

    //JPanel blue = new JPanel();


    // fiende koordinater
    int enemyX = 250;
    int enemyY = 0;


    // projektil koordinater

    int projectileX = 0;
    int projectileY = -700;

    int projectileSpeed = 10;


    // Altare
    int altarHeight = (int) ((int)screenCol * 0.4);
    int altarY = (int) (screenCol - altarHeight);
    int altarX = 0;
    int altarIndex;

    // Håller koll på material (gör om till mätare)
    JLabel treeScoreLabel = new JLabel(String.valueOf(treeScore));



    public myPanel() throws IOException {

        // väder API
        int maxRequests = 5;
        int triedRequests = 0;
        HttpClient client = HttpClient.newHttpClient();

        //http builder
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?lat=55.6050&lon=13.0038&appid=51b63e86e7c31d25c02aa8899720bc20"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){

            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        //Bakgrunder
        BufferedImage frozenBackground = ImageIO.read(new File("org/example/snowyLevel.png"));
        BufferedImage sunnyBackground = ImageIO.read(new File("org/example/sunnyLevel.png"));
        BufferedImage stormyBackground = ImageIO.read(new File("org/example/stormyLevel.png"));
        // Finns bättre sätt att hämta nivåer?
        backgroundArray.add(frozenBackground);
        backgroundArray.add(sunnyBackground);
        backgroundArray.add(stormyBackground);

        BufferedImage firstHouse = ImageIO.read(new File("org/example/firstHouse.png"));
        BufferedImage secondHouse = ImageIO.read(new File("org/example/secondHouse.png"));
        BufferedImage thirdHouse = ImageIO.read(new File("org/example/thirdHouse.png"));
        BufferedImage fourthHouse = ImageIO.read(new File("org/example/fifthHouse.png"));
        BufferedImage fifthHouse = ImageIO.read(new File("org/example/sixthHouse.png"));
        pileArray.add(firstHouse);
        pileArray.add(secondHouse);
        pileArray.add(thirdHouse);
        pileArray.add(fourthHouse);
        pileArray.add(fifthHouse);

        // altare bilder
        BufferedImage firstAltar = ImageIO.read(new File("org/example/steg1Altare.png"));
        BufferedImage secondAltar = ImageIO.read(new File("org/example/steg2Altare.png"));
        BufferedImage thirdAltar = ImageIO.read(new File("org/example/steg4Altare.png"));
        altarArray.add(firstAltar);
        altarArray.add(secondAltar);
        altarArray.add(thirdAltar);

        // Börjar skicka ner projektiler mot spelare
        altarTimer.start();
        // Spelare
         playerImage = new ImageIcon("org/example/playerStatic.jpg").getImage();
         // Altar
         altarImage = altarArray.getFirst();
         // material att samla
         treeImage = new ImageIcon("org/example/tree.png");
         // fiende array ska vara här
         enemyImage = new ImageIcon("org/example/enemyStatic.png").getImage();
         // projektil array ska vara här
         projectileImage = new ImageIcon(("org/example/lightningBolts.png")).getImage();
         pileImage = pileArray.getFirst();


        // fixa så alla images är samma, inte .getimage() på jhälften, kolla skillnad



        setFocusable(true);
        addKeyListener(this);

        //TreeScoreLabel  - skapa en metod som uppdaterar
        treeScoreLabel.setForeground(Color.WHITE);
        add(treeScoreLabel);


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
        graphics2D.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); //bakgrund

        // tar in rektangel i scope
        Rectangle treeRect = null;

        //for (int i = 0; i < 20; i++) {
        g.drawImage(treeImage.getImage(), treeX, treeY, treeWidth, treeHeight, null); // mark
        treeRect = new Rectangle(treeX, treeY, treeWidth, treeHeight);
        //}
        g.drawImage(pileImage, pileX, pileY, pileWidth, pileHeight, this); // mark
        Rectangle pileRect = new Rectangle(pileX, pileY, pileWidth, pileHeight);

        g.drawImage(playerImage, playerX, playerY, playerWidth, playerHeight, this); // mark
        g.drawImage(enemyImage, enemyX, enemyY, playerWidth, playerHeight, null); // mark
        g.drawImage(projectileImage, projectileX, projectileY, getWidth(), (int) (getHeight() * 0.2), null); // mark
        g.drawImage(altarImage, altarX, altarY, playerWidth, altarHeight, this); // mark

        // Rektanglar som används för obstruction handling och intersects
        Rectangle projectileRect = new Rectangle(projectileX, projectileY, getWidth(), (int) (getHeight() * 0.2));
        Rectangle playerRect = new Rectangle(playerX, playerY, playerWidth, playerHeight);
        Rectangle altarRect = new Rectangle(altarX, altarY, playerWidth, playerHeight);



        Timer treeTimer = new Timer(20000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treeAlive = true;
                treeImage = new ImageIcon("org/example/tree.png");
            }
        });

        // sänker treeScore med 1 vart 4e sekund
        Timer lessTreeTimer = new Timer(4000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (treeScore >= 3) {
                    treeScore--;
                    System.out.println("Lower" + treeScore);
                }
            }
        });


        // sänker index värdet på altar array så den återställer
        if (playerRect.intersects(altarRect)){
            altarIndex--;
        }

        if (playerRect.intersects(pileRect)){
        }

        // om huset är nästan färdigbyggt kan spelaren skyddas intuti
        if (playerRect.intersects(pileRect) && building){
                pileIndex++;
                treeScore--;
                System.out.println("Building house!   " +" tree     "  + treeScore + " pile     "   + pileIndex);
                // ökar index med 1 vart 5 sekund och hämtar varje bild tills det inte finns kvar mer i listan
                if (pileIndex < pileArray.size()){
                    pileImage = pileArray.get(pileIndex);
                    // öka bounds, öka storlek
            } else {
                System.out.println("Not enough wood");
                building = false;
            }
           /* buildingMethod();
            pileIndex++;
            treeScore--;*/
            // om spelaren har mer än 3 trä, kan de bygga på plats
           /* if (building) {  //fixa så building bygger - ju längre denna mer desto mer byggs

                // stannar vid mindre än 3
            } else {
                lessTreeTimer.stop();
            }*/
        }

        // om spelaren blir träffad av attack och är skyddad
        if (playerRect.intersects(projectileRect) && !shielding) {
            playerDeath();
        }

        // interaktion med träd - endast möjligt om träd är aktivt för att förhindra fusk
        if (playerRect.intersects(treeRect) && treeAlive) {
            playerImage = new ImageIcon("org/example/playerChopping.jpg").getImage();
            playerX = playerX - playerSpeed;
            // ökar antal trä samlade
            treeScore++;
            //uppdaterar label
            treeScoreLabel.setText(String.valueOf(treeScore));
            // om träscore blir 5 ( max antral samlat ), sänks trä rektangelns bounds till 0
            if (treeScore > 10) {
                treeImage = new ImageIcon("org/example/choppedDown.png");
                treeAlive = false;
                treeTimer.start();
            }
        }

        if (shielding) {
            g.drawImage(projectileImage, playerX, playerY + 10, playerWidth, playerHeight, null); // mark
            Rectangle buildRect = new Rectangle(playerX + 10, playerY + 10, playerWidth, playerHeight);
            if(buildRect.intersects(pileRect)){
                shielding = false;
                lessTreeTimer.start();
            }
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
         int keyCode = e.getKeyCode();
    if(keyCode == KeyEvent.VK_D && playerSpeed > 0){
        playerX = playerX + playerSpeed;
        playerImage = new ImageIcon("org/example/playerRunning.jpg").getImage();
        repaint();
    }
        if(keyCode == KeyEvent.VK_A&& playerSpeed > 0){
            playerX = playerX - playerSpeed;
            playerImage = new ImageIcon("org/example/playerLeft.jpg").getImage();
            repaint();
        }



        if(keyCode == KeyEvent.VK_B){
            if (treeScore > 0){
                // medans treescore är mer än 0 kan man kalla metod
                building = true;
                //System.out.println("building      " + "treeScore     " + treeScore + "pileindex      "+ pileIndex); //animation
               /* treeScore--;
                treeScoreLabel.setText(String.valueOf(treeScore));*/

                /*if (treeScore <= 6){
                       shielding = true;
                    } else {
                        System.out.println("Not enough materials");
                    }*/
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_D && playerSpeed > 0){
            playerX = playerX + playerSpeed;
            playerImage = new ImageIcon("org/example/playerStatic.jpg").getImage();
            repaint();
        }

        if(keyCode == KeyEvent.VK_A && playerSpeed > 0){
            playerX = playerX - playerSpeed;
            playerImage = new ImageIcon("org/example/playerStaticLeft.png").getImage();
            repaint();
        }
        if(keyCode == KeyEvent.VK_B){
            /*System.out.println("Stopped building");
            building = false;*/

        }
    }

    public void playerDeath(){
        playerImage = new ImageIcon("org/example/gravePile.png").getImage();
        playerSpeed = 0;
    };




    Timer sendingProjectileTimer = new Timer(10000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        sendProjectileTimer.start();
        }
    });
    Timer sendProjectileTimer = new Timer(40, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            projectileY = projectileY + projectileSpeed;
            repaint();
        }
    });

    Timer altarTimer = new Timer(10000, new ActionListener() {
        int altarIndex = 0;

        @Override
        public void actionPerformed(ActionEvent e) {
            altarIndex++;

            // ökar index med 1 vart 5 sekund och hämtar varje bild tills det inte finns kvar mer i listan
            if (altarIndex < altarArray.size()){
                altarImage = altarArray.get(altarIndex);
            }
            if (altarIndex > 3){
                sendingProjectileTimer.start();
            }
        }
    });
    public void buildingMethod(){
        while(building){
            pileIndex++;
            treeScore--;
        }
    }

}