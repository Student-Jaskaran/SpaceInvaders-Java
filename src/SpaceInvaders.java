package Games1;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener
{
    // board
    int tileSize = 32;
    int rows = 16;
    int columns = 16;

    int boardWidth = tileSize * columns;//32*16
    int boardHeight = tileSize * rows;

   private Image shipImg;
    private Image alienImg;
    private Image alienCyanImg;
   private Image alienMagentaImg;
   private Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    class Block
    {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true;//used for aliens
        boolean used = false;//used for bullets

        Block(int x,int y ,int width,int height,Image img)
        {
            this.x =x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    //moving the ship is here
    int shipWidth = tileSize*2;//64px
    int shipHeight = tileSize;//32px
    int shipX = tileSize*columns/2 - tileSize;
    int shipY = boardHeight - tileSize*2;
    int shipVelocityX =tileSize;
    Block ship;

    // aliens
    ArrayList<Block>alienArray;
    int alienWidth = tileSize*2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY =tileSize;

    int alienRows =2;
    int alienColumn = 3;
    int alienCount = 0;// By default
    int alienVelocityX = 1;//alien moving speed

    //bullets
    ArrayList<Block>bulletArray;
    int bulletWidth = tileSize/8;
    int bulletHeight = tileSize/2;
    int bulletVelocityY = -10;//moving bullets

    Timer gameLoop;
    boolean gameOver = false;
    int score = 0;



    SpaceInvaders()//constructor
    {
        setPreferredSize(new Dimension(boardWidth,boardHeight));//why we create second board
        // because first broad not a 512 px for reason.
        // its 511 1px goes from "space invader" that why we create here
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        // load image
        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();//set location in here
        alienImg= new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX,shipY,shipWidth,shipHeight,shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

        //game timer
        gameLoop =new Timer(1000/60, this);//1000/60 = 16.7
        createAlien();
        gameLoop.start();

    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g)
    {
        //ship
        g.drawImage(ship.img, ship.x, ship.y,ship.width,ship.height,null);

        //alien
        for(int i= 0;i<alienArray.size();i++)
        {
            Block alien =alienArray.get(i);
            if(alien.alive)
            {
                g.drawImage(alien.img,alien.x,alien.y,alien.width,alien.height,null);
            }
        }

        //bullets
        g.setColor(Color.white);
        for(int i= 0;i< bulletArray.size();i++)
        {
            Block bullet = bulletArray.get(i);
            if(!bullet.used)
            {
                // g.drawRect(bullet.x,bullet.y,bullet.width,bullet.height);
                g.fillRect(bullet.x,bullet.y,bullet.width,bullet.height);
            }
        }

        //score
        g.setColor(Color.RED);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if(gameOver)
        {
            g.drawString("GAME OVER!: " + String.valueOf((int)score),10,35);
        }
        else
        {
            g.drawString(String.valueOf((int)score),10,35);
        }
    }

    public void move()
    {
        //alien
        for(int i=0;i<alienArray.size();i++) {
            Block alien = alienArray.get(i);
            if (alien.alive)
            {
                alien.x +=alienVelocityX;

                //if alien touches the boarders
                if(alien.x + alienWidth >= boardWidth || alien.x<=0 )
                {
                    alienVelocityX *= -1;
                    alien.x +=alienVelocityX*2;

                    // move all aliens done by one row
                    for(int j = 0;j<alienArray.size();j++)
                    {
                        alienArray.get(j).y += alienHeight;
                    }
                }
                if(alien.y >= ship.y)
                {
                    gameOver = true;
                }
            }
        }

        //bullets
        for(int i= 0;i<bulletArray.size();i++)
        {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            //bullet collision with aliens
            for(int j =0;j < alienArray.size();j++)
            {
                Block alien = alienArray.get(j);
                if(!bullet.used && alien.alive && detectCollision(bullet,alien))
                {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;

                }
            }
        }

        //clear bullets
        while(bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0))
        {
            bulletArray.remove(0);// remove the first elements of the array
        }

        // next level
        if(alienCount == 0)
        {
            //increase the number of alien in columns and rows by 1
            score += alienColumn * 100;//bonus point
            alienColumn = Math.min(alienColumn + 1,columns/2-2);// cap column at 16/2 - 2 = 6
            alienRows = Math.min(alienRows + 1,rows - 6);// cap row at 16-6 =10
            alienArray.clear();
            bulletArray.clear();
            alienVelocityX = 1;
            createAlien();

        }
    }


    public void createAlien()
    {
        Random random = new Random();
        for(int r = 0;r < alienRows;r++)
        {
            for(int c = 0; c < alienColumn; c++)
            {
                int randomImgIndex =random.nextInt(alienImgArray.size());
                Block alien =new Block(
                        alienX + c*alienWidth,
                        alienY + r*alienHeight,
                        alienWidth,
                        alienHeight,
                        alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount =alienArray.size();
    }
    public boolean detectCollision(Block a,Block b)
    {
        return a.x < b.x + b.width && //a's top left corner doesn't reach b's top right corner
                a.x + a.width >b.x && //a's top right corner passes b's top left  corner
                a.y < b.y + b.height && // a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y; // a's bottom left corner passes b's top left corner
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver)
        {
            gameLoop.stop();
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if(gameOver)//any key to restart
        {
            ship.x =shipX;
            alienArray.clear();
            bulletArray.clear();
            score=0;
            alienVelocityX = 1;
            alienColumn =3;
            alienRows =2;
            gameOver = false;
            createAlien();
            gameLoop.start();
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT &&  ship.x-shipVelocityX >=0) {
            ship.x -= shipVelocityX;//move left one tile
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + shipWidth + shipVelocityX <= boardWidth)
        {
            ship.x += shipVelocityX;//move right one tile
        }
        else if(  e.getKeyCode() == KeyEvent.VK_SPACE)
        {//shoot bullet
            Block bullet = new Block(ship.x + shipWidth*15/32,ship.y,bulletWidth,bulletHeight,null);
            bulletArray.add(bullet);
        }
    }
}

