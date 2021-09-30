package cs1.app;

import cs1.android.*;

public class MineSweeperApp extends AndroidApp
{
    void printTable(int [][] table)
    {
        for (int r = 0; r < table.length ; r = r + 1)
        {
            for (int c = 0; c < table[r].length; c = c + 1)
            {
                double curValue = table[r][c];
                System.out.print(curValue + " ");
            }
            
            System.out.println();
        }
    }
    
    //randomly place mines
    
    void plantMines (int[][] field)
    {
        for (int r = 0; r < field.length ; r = r + 1)
        {
            for (int c = 0; c < field[r].length; c = c + 1)
            {
                int prob = canvas.getRandomInt(1,10);
                
                if (prob <= 2)
                {
                    field[r][c] = 9;
                    //System.out.println("Mines in:" + r + ", " + c);
                }
            }
        }
    }
    
    //place flags
    
    void setFlag(int[][] field, int r, int c)
    {        
        if (field[r][c] <= 9)
        {
            field[r][c] = field[r][c] + 20;
        }
        else if (field[r][c] >= 20)
        {
            field[r][c] = field[r][c] - 20;
        }
    }
    
    //check to see if board is clear
    
    boolean isCleared (int[][] field)
    {
        for (int r = 0; r < field.length; r = r + 1)
        {
            for(int c = 0; c < field[r].length; c = c + 1)
            {
                if (field[r][c] <= 9 || (field[r][c] > 19 && field[r][c] < 29)) 
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    //open mines
    
    void openMines (int[][] field)
    {
        for (int r = 0; r < field.length ; r = r + 1)
        {
            for (int c = 0 ; c < field[0].length ; c = c + 1)
            {
                if (field[r][c] == 9)
                {
                    field[r][c] = 19;
                }
            }
        }
    }
    
    //determines whether row and column are valid
    
    boolean isValid (int[][] field, int r, int c)
    {
        if ((r >= 0 && r < field.length ) && (c >= 0 && c < field[0].length))
        {
            return true;
        }
        return false;
    }
    
    //counting mines
    
    int countMines( int[][] field, int r, int c)
    {
        int minesAround = 0;
        
        for (int indexR = r - 1; indexR <= r + 1; indexR = indexR + 1)
        {
            for(int indexC = c - 1; indexC <= c + 1; indexC = indexC + 1)
            {
                if (isValid(field, indexR, indexC) == true)
                {
                    int value = field[indexR][indexC];
                    
                    if( value == 9)
                    {
                        minesAround = minesAround + 1;
                    }
                }
            }
        }
        return minesAround;
    }
    
    //update field around mines
    
    void updateField (int[][] field)
    {
        for (int r = 0; r < field.length ; r = r + 1)
        {
            for (int c = 0; c < field[0].length; c = c + 1)
            {
                int mines = countMines(field, r, c);
                
                if( mines > 0 && field[r][c] != 9)
                {
                    field[r][c] = mines;
                }
            }
        }
    }
    
    //generates the field
    
    int[][] generateField (int r, int c)
    {
        int[][] field = new int[r][c];
        plantMines(field);
        updateField(field);
        return field;
    }
    
    //opens cells, i.e. updates them
    
    void openCells (int[][] field, int r, int c)
    {
        if(field[r][c] == 0)
        {
            for (int indexR = r - 1; indexR <= r + 1; indexR = indexR + 1)
            {
                for(int indexC = c - 1; indexC <= c + 1; indexC = indexC + 1)
                {
                    if (isValid(field, indexR, indexC) == true)
                    {                        
                        if( field[indexR][indexC] < 9) 
                        {
                            field[indexR][indexC] = field[indexR][indexC] + 10;
                        }
                    }
                }
            }
        }
        else if (field[r][c] > 0 && field[r][c] < 9) 
        {
            field[r][c] = field[r][c] + 10;
        }
    }
    
    //draws game field
    
    void drawField(int[][] field, double startX, double startY)
    {
        double curY = startY;
        
        for (int r = 0; r < field.length ; r = r + 1)
        {
            double curX = startX;
            
            for (int c = 0; c < field[0].length ; c = c + 1)
            {
                if (field[r][c] <= 9)
                {
                    //String tryImage = "m" + field[r][c] + ".png";
                    //canvas.drawImage(curX,curY,tryImage);
                    canvas.drawImage(curX, curY, "hidden.png");
                }
                else
                {
                    String image = "m" + field[r][c] + ".png";
                    canvas.drawImage(curX, curY, image);
                }
                curX = curX + 36;
            }
            curY = curY + 36;
        }
    }
    
   //main procedure to play
    
    void playMineSweeper()
    {
        int r = canvas.getRandomInt(7,10);
        int c = canvas.getRandomInt(6,8);
        int imageSize = 36;
        
        int[][] field = generateField( r, c);
                
        double startX = (canvas.getWidth()- (imageSize * c)) / 2 + (imageSize / 2);
        double startY = (canvas.getHeight()/ 2) - (imageSize * (r / 3));
             
        boolean mineTouched = false;
        
        while (isCleared(field) == false && mineTouched == false)
        {   
            canvas.clear();
            drawField(field, startX, startY);
            canvas.drawImage(canvas.getWidth() / 2, canvas.getHeight() / 6, "smiley.png");
            
            Touch touch = canvas.waitForTouch();
            int numTaps = touch.getTaps();
            int cellRow = (int)((touch.getY() - (startY - imageSize/2)) / imageSize);
            int cellCol = (int) ((touch.getX() -(startX - imageSize/2)) / imageSize);
                       
            if (numTaps == 1 && field[cellRow][cellCol] != 9)
            {
                openCells(field, cellRow, cellCol);
                canvas.drawImage(canvas.getWidth() / 2, canvas.getHeight() / 6, "winky.png");
                canvas.sleep(0.4);           
            }
            else if(numTaps !=2 && field[cellRow][cellCol] == 9)
            {
                openMines(field);
                mineTouched = true;
            }
            
            else if (numTaps == 2)
            {
                setFlag(field, cellRow, cellCol);
            }
        } 
        
        canvas.clear();
        drawField(field, startX, startY);
        
        if (mineTouched == true)
        {
            canvas.drawImage(canvas.getWidth() / 2, canvas.getHeight() / 6, "sad.png");
            canvas.drawText(canvas.getWidth() / 6, canvas.getHeight() / 6, "You lose", "black|20");
        }
        else
        {
            canvas.drawImage(canvas.getWidth() / 2, canvas.getHeight() / 6, "smiley.png");
            canvas.drawText(canvas.getWidth() / 6, canvas.getHeight() / 6 , "You win", "black|20");
        }       
    }
    
    public void run()
    {
        //int[][]field = new int[6][7];
        //int[][] field = new int[2][2];
        //int[][] field = new int[1][1];
        //int[][] field = new int[0][0];
        //int[][]field = new int[2][5];
        //plantMines(field);
        //printTable(field);

        //int[][] field = {{ 5,  9,  2,  1,  0 }, { 3,  6,  9,  2,  1 },{ 9, 1,  1,  8,  9 }, { 1,  1,  4,  2,  9 }};
        //setFlag( field, 0, 1 );  
        //setFlag( field, 3, 2 );  
        //setFlag( field, 1, 1 );   
        //setFlag( field, 2, 1 );   
        //printTable(field);
        
        //int[][] field = {{ 9,  29,   2,   1, 20 }, {23,  24,   9,  12,  1 },{ 9,  12,  11,  13,  9 },{ 1,  11,  10,  12,  9 } };
        //System.out.println( "is field cleared: " + isCleared( field ) );
        //int[][] field2 = {{20, 22, 29}, {25, 28, 26}};
        //System.out.println( "is field cleared: " + isCleared( field2 ));
        //int[][] field3 = {{29,  29,  12,  11, 10 },{13,  12,  29,  12, 11 },{29,  14,  11,  13, 29 },{11,  11,  10,  12, 29 } };
        //System.out.println( "is field cleared: " + isCleared( field3 ) );  
        //int[][] field4 ={{},{}};
        //System.out.println( "is field cleared: " + isCleared( field4 ) );
        
        //int[][] openField = {{ 9,  9,  2,  1,  0 }, { 3,  4,  9,  2,  1 }, { 9,  2,  1,  3,  9 }, { 1,  1,  0,  2,  9 } };
        //int[][] openField = {{9, 1, 0, 0} , { 9, 9, 9, 3}};
        //int[][] openField ={{},{}};
        //int[][] openField ={{9},{1}};
        //openMines( openField );
        //printTable(openField );
        
        //int[][] field = {{29,  29,  12,  11, 10 },{13,  14,  29,  12, 11 },{29,  12,  11,  13, 29 },{11,  11,  10,  12, 29 } };
        //System.out.println("Is (3, 4) valid: " + isValid( field, 3, 4 ));    
        //System.out.println("Is (0, 0) valid: " + isValid( field, 0, 0 ));    
        //System.out.println("Is (-1, -2) valid: " + isValid( field, -2, -2 ));    
        //System.out.println("Is (-4, -4) valid: " + isValid( field, -4, -4 )); 
        //System.out.print("Is (5, 5) valid: " + isValid( field, 5,5 ));
               
       // int[][] field = {{ 9,  9,  0,  0,  0 },{ 0,  0,  9,  0,  0 },{ 9,  0,  0,  0,  9 }, { 0,  0,  0,  0,  9 } };
        //int mines = countMines( field, 1, 1 );
       // System.out.println( "mines around cell (1,1): " + mines); 
        //int mines1 = countMines( field, 0, 4 );
        //System.out.println( "mines around cell (0,4): " + mines1);
        //int[][] field2 ={{},{}};
        //int mines2 = countMines(field2, 0, 0);
        //System.out.println( "mines around cell (0,0): " + mines2);
           
        //int[][] field = {{ 9,  9,  0,  0,  0 }, { 0,  0,  9,  0,  0 }, { 9,  0,  0,  0,  9 }, { 0,  0,  0,  0,  9 } };
        //int[][] field = {{},{}};
        //int[][] field = {{9},{0}};
        //updateField( field );
        //printTable( field );
               
        //int[][] field = generateField( 4, 5 );
        //int[][] field = generateField( 1, 1 );
        //int[][] field = generateField( 0, 0 );
        //int[][] field = generateField( 2, 2);
        //printTable( field );
        
        //int[][] field = {{ 9,  9,  2,  1,  0 }, { 3,  4,  9,  2,  1 },{ 9,  2,  1,  3,  9 },{ 1,  1,  0,  2,  9 }};
        //openCells( field, 3, 2 );
        //int[][] field = {{1},{1}};
        //openCells( field, 0, 0 );
        //int[][] field = {{2, 9}, {4, 0}};
        //openCells (field, 1, 1);
        //printTable( field ); 
        
        //int[][] field = {{ 9,  29,   2,   1, 20 },{23,  24,   9,  12,  1 },{ 9,  12,  11,  13,  9 }, { 1,  11,  10,  12,  9 } };
        //int[][] field = {{1},{2}};
        //int[][] field = {{0}};
        //drawField( field, 50, 150 );
        
        playMineSweeper();
    }
}