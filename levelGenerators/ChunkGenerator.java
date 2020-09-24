package levelGenerators.GeneratorT;
import engine.core.MarioLevelModel;
import engine.helper.MarioTimer;
import engine.helper.RunUtils;
import levelGenerators.MarioLevelGenerator;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ChunkGenerator implements MarioLevelGenerator {

        private int FLOOR_PADDING = 3;//Floor padding
        private int blockSize; //Number of columns extracted as a chunk
        private int numLevelsTrain; //Number of levels used to train

        public ChunkGenerator(int chunk_size,int train_num)//Pass in parameters
        {
            this.blockSize= chunk_size;
            this.numLevelsTrain=train_num;
        }

        @Override
        public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
            Random random = new Random();
            model.clearMap(); //Clear the map

            int height = 0; //Height of map...will change later based on height of each train level
            int width = 0; //Width of map...will change later based on width of each train level
            int poolWidth = 0;//Width of pool 2d array. (pool 2d array stores all train levels in a concatonated manner)

            //Store levels in levels array entire level as a string
            String levels[] = new String[numLevelsTrain + 1];//Stores levels as strings
            for (int i = 1; i <= numLevelsTrain; i++) {
                //Load human made level from specified folder
                levels[i] = RunUtils.retrieveLevel("levels/original/lvl-" + i + ".txt");
            }


            ArrayList<char[][]> levelTilesGlobal = new ArrayList<char[][]>();//Stores an array list of 2d arrays(levels)

            //Store each level of different size in a 2d array then add it to the arraylist levelTilesGlobal
            for (int lev = 1; lev <= numLevelsTrain; lev++) { //For each level to train
                String[] splitArray;
                String s = levels[lev]; //Store level as string

                //Extract height and width of train level
                String[] splitColArray = s.split("\n");
                //print("Extracted height of level: " + splitColArray.length);
                height = splitColArray.length;
                String[] firstRow = splitColArray[0].split("");
                width = firstRow.length;
                //print("Extracted width of level: " + width);
                poolWidth = poolWidth + width;
                char levelTiles[][] = new char[height][width];


                //Replace empty spaces and \n characters in level
                String stringWithoutSpaces = s.replaceAll("\\s+", "");
                stringWithoutSpaces = stringWithoutSpaces.replaceAll("\n+", "");
                splitArray = stringWithoutSpaces.split(""); //Split string into char per cell by splitting at ""


                //Store level in 2d array
                int loc = 0;
                int counter = 0;
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        String valStr = splitArray[loc];
                        levelTiles[row][col] = valStr.charAt(0);
                        //  print(String.valueOf(levelTiles[row][col]));
                        loc++;
                    }
                    // print("\n");
                }

                //store this level in array list holding all levels
                levelTilesGlobal.add(levelTiles);
            }




            //Store all columns from all levels into 1 array called pool
            char pool[][] = new char[height][poolWidth];
            int rc = 0;
            for (int n = 0; n < numLevelsTrain; n++) {
                char[][] levelN = levelTilesGlobal.get(n);
                int rp = 0;
                int rHeight = levelN.length;
                int cWidth = levelN[1].length;
                for (int col = 0; col < cWidth; col++) {
                    rp = 0;
                    for (int row = 0; row < rHeight; row++) {
                        pool[rp][rc] = levelN[row][col];
                        rp++;
                    }
                    rc++;
                }
            }

            //Print Pool:
            // print("Pool generated");
            //  print("\n Pool width is " + poolWidth + "\n");
            // print2dArray(pool);


            int finalWidth = 150; //Final level must have width 150
            char chopLevel[][] = new char[height][finalWidth];//This 2d array stores
            int randVal = ThreadLocalRandom.current().nextInt(1, poolWidth - 1+1 );//random val between 1 and pool width (cols) - last col

            int bCount = 1; //Block count
            //For each column in level to be outputted
            for (int col = 0; col < finalWidth; col++) {
                if (bCount == blockSize) { //If block size reached end consecutive columns from pool
                    randVal = ThreadLocalRandom.current().nextInt(1, poolWidth - 1+1 );//random val between 1 and pool width (cols)
                    bCount = 1;//Make bCount 1 again
                } else {
                    if (randVal < poolWidth - 1) {
                        randVal++;//increment till block size reached
                    } else { //if pool width reached then randomize from pool again
                        randVal = ThreadLocalRandom.current().nextInt(1, poolWidth - 1+1 );
                    }
                    bCount++;//Increment block count
                }
                for (int row = 0; row < height; row++) {
                    if (pool[row][randVal] != 'M' ) { //Exclude mario
                        chopLevel[row][col] = pool[row][randVal]; //Store this block from pool in generator level
                    }
                }
            }


            //Overwrite first few blocks
            for (int col = 0; col < 5; col++) {
                randVal = ThreadLocalRandom.current().nextInt(1, finalWidth + 1);//random val between 1 and 201 (cols)
                for (int row = 0; row < height; row++) {
                    // print("i is " + i);print("j is" + j);print("\n");
                    chopLevel[row][col] = '-';
                }
            }


            //Overwrite last few blocks
            for (int col = finalWidth-5; col < finalWidth; col++) {
                randVal = ThreadLocalRandom.current().nextInt(1, finalWidth + 1);//random val between 1 and 201 (cols)
                for (int row = 0; row < height-2; row++) {
                    // print("i is " + i);print("j is" + j);print("\n");
                    chopLevel[row][col] = '-'; //pool[row][col];
                }
            }



            //Remove random tall towers
            int countBrick = 0;
            for (int j = 1 ; j < finalWidth ; j++) {
                countBrick = 0;
                for (int i = height-3; i >= 8; i--) {
                    // char val = chopLevel[i][j];
                    if (chopLevel[i][j] == 'X'|| chopLevel[i][j] == 't' || chopLevel[i][j] == 'T'|| chopLevel[i][j] == '#' || chopLevel[i][j] == '%' || chopLevel[i][j] == 'S'|| chopLevel[i][j] == 'C' || chopLevel[i][j] == 'L' || chopLevel[i][j] == 'U' ) {
                        countBrick++;
                        //if left block is empty
                        if (countBrick > 4)

                            if ((chopLevel[i][j - 1] == '-')) {
                                chopLevel[i][j] = '-';
                                // countBrick=0;
                            }
                    }
                }
                countBrick=0;
            }

            //Remove single tubes
            for (int col = 1; col < finalWidth - 1; col++)
            {
                for (int row = 0; row < height; row++)
                {
                    if(chopLevel[row][col] == 'T')
                    {
                        if ((chopLevel[row][col - 1] != 'T') && (chopLevel[row][col + 1] != 'T') && (chopLevel[row][col - 1] != 't') && (chopLevel[row][col + 1] != 't')  )
                        {
                            chopLevel[row][col] = '-';
                        }
                    }
                    if(chopLevel[row][col] == 't')
                    {
                        if ((chopLevel[row][col - 1] != 'T') && (chopLevel[row][col + 1] != 'T') && (chopLevel[row][col - 1] != 't') && (chopLevel[row][col + 1] != 't')  )
                        {
                            chopLevel[row][col] = '-';
                        }
                    }
                } //END for ROW
            }//END for COL


            //Print generated level:
            //print("Generated level : \n");
            // print2dArray(chopLevel);

            //Set model using 2d choplevel array which contains final generated level
            for (int j = 0; j < finalWidth; j++) {
                for (int i = 0; i < height; i++) {
                    char val = chopLevel[i][j];

                    //Set this chunk for final generated level
                    model.setBlock(j, i, val);
                }
            }


            //Initiates first columns with blocks and mario and last columns with flag
            model.setRectangle(0, 14, FLOOR_PADDING, 2, MarioLevelModel.GROUND);
            model.setRectangle(model.getWidth() - 1 - FLOOR_PADDING, 14, FLOOR_PADDING, 2, MarioLevelModel.GROUND);
            model.setBlock(FLOOR_PADDING / 2, 13, MarioLevelModel.MARIO_START);
            model.setBlock(model.getWidth() - 1 - FLOOR_PADDING / 2, 13, MarioLevelModel.MARIO_EXIT);

            return model.getMap();

        }


        public void print(String s)
        {
            System.out.print(s);
        }

        public void print2dArray(char chopLevel[][])
        {
            int rFinal = chopLevel.length;
            int cFinal = chopLevel[1].length;
            print("\n num rows : " + rFinal);
            print("\n num cols : " + cFinal);
            for (int row = 0; row < rFinal; row++) {
                for (int col = 0; col < cFinal; col++) {
                    print(String.valueOf(chopLevel[row][col]));
                }
                print("\n");
            }

        }


        @Override
        public String getGeneratorName (){
            return "MyGenerator";
        }

    }

