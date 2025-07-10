package Games1;

import javax.swing.*;

    public class App {
        public static void main(String[] args) throws Exception
        {
            //Window variables
            int tileSize = 32;
            int rows = 16;
            int columns = 16;
            int boardWidth = tileSize * columns; // 32*16 = 512px
            int boardHeight = tileSize * rows ; // 32*16=512px

            JFrame frame = new JFrame("Space Invaders");

            frame.setVisible(true);// set for visible
            frame.setSize(boardWidth,boardHeight);
            frame.setLocationRelativeTo(null);// set screen in the center
            frame.setResizable(true);// the user not change the size of window
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//for exit and close

            SpaceInvaders spaceInvaders = new SpaceInvaders();//
            frame.add(spaceInvaders);
            frame.pack();
            spaceInvaders.requestFocus();
            frame.setVisible(true);
        }
    }


