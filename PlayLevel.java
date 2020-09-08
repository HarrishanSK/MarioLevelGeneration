import agents.MarioAgent;
import engine.core.*;
import engine.helper.MarioStats;
import levelGenerators.MarioLevelGenerator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import static engine.helper.RunUtils.*;

    @SuppressWarnings("ConstantConditions")
    public class PlayLevel {

        public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
            // Run settings:
            boolean visuals = true;  // Set to false if no visuals required for this run.
            boolean generateDifferentLevels = false;  // If true, each play will be a different generated level.
            String levelFile = "levels/original/lvl-1.txt";  // null;
            levelFile = null;
            MarioLevelGenerator generator = new levelGenerators.GeneratorT.ChunkGenerator(3,15);
            //MarioLevelGenerator generator =new levelGenerators.GeneratorT.patternExtract(3);

            // Note: either levelFile or generator must be non-null. If neither is null, levelFile takes priority.
            if (levelFile == null && generator == null) {
                return;
            }

            // Create a MarioGame instance and game-playing AI
            MarioGame game = new MarioGame();
            MarioAgent agent = new agents.robinBaumgarten.Agent();

            // Grab a level from file, found in directory "levels/" or pass null to generate a level automatically.
            String level = getLevel(levelFile,generator);

            // Display the entire level.
            //game.buildWorld(level, 0.75f);

            //Blank MarioStats object for storing merged results
            MarioStats statsTotal = new MarioStats();
            int numGamesPlayed = 0;


            //Use A* agent for simulation
            MarioAgent agentTest = new agents.robinBaumgarten.Agent();

            //Counts playable levels for both generators over specified number of simulations and writes to a csv file
            //countPlayableLevels(game,agentTest,50);

            //For testing generated levels and re-generating when level is unplayable.
            int checkPlayability = 1;//Set this to 1 to prevent unplayable levels from being generated
            float winRate;
            System.out.println("Generating Level...");
            if(checkPlayability == 1){
                while (true){
                    int numSimulations = 2; //Number of simulations
                    winRate = checkPlayable(game,agentTest,level,numSimulations);

                    if(winRate < 0.8){
                        System.out.println("Level is unplayable. Win rate - " + winRate + "\nRe-generating");
                        level = generateLevel(generator);
                    }
                    else{
                        System.out.println("Win rate over " + numSimulations + " runs = " + winRate + " Playable level generated .... .....");
                        break;
                    }
                }
                game.buildWorld(level,1); //Show generated playable level
            }



            int playAgain = 0;
            while (playAgain == 0) {  // 0 - play again! 1 - end execution.

                // Play the level, either as a human ...
                //MarioResult result = game.playGame(level, 120, 0,30);

                // ... Or with an
                // AI agent
                MarioResult result = game.runGame(agent, level, 15, 0,visuals,30);

                // Print the results of the game

                System.out.println(result.getGameStatus().toString());
                System.out.println(resultToStats(result).toString());


                //Adds statistics over multiple runs and merge returns a combined MarioStats object of current and new
                statsTotal = statsTotal.merge(resultToStats(result));

                if (generateDifferentLevels) {
                    level = generateLevel(generator);
                }

                numGamesPlayed += 1;

                // Check if we should play again.
                playAgain = (game.playAgain == 0 && visuals) ? 0 : 1;  // If visuals are not on, only play 1 time
            }
            //System.out.println("--- Average of  Statistics over " + numGamesPlayed + " runs ---\n" + statsTotal.toString());


        }

        //Function to check for win rate over n number of simulations/runs
        public static float checkPlayable(MarioGame game, MarioAgent agent, String level,int numSimulations){

            MarioStats statsTotal = new MarioStats();
            MarioResult result;
            for(int i = 0; i < numSimulations; i++){
                result = game.runGame(agent, level, 30, 0,false,4096);//Larger fps for faster simulation

                statsTotal = statsTotal.merge(resultToStats(result));
            }
            return statsTotal.returnWinRate();

        }

        //Function to count the number of playable levels for both generators over specified number of simulations
        public static void countPlayableLevels(MarioGame game, MarioAgent agent,int numSimulations) throws FileNotFoundException, UnsupportedEncodingException {
            MarioLevelGenerator generatorChunk = new GeneratorT.BlockGeneratorFinal(5,15);
            MarioLevelGenerator generatorPattern = new GeneratorT.patternExtract(5);
            String levelChunk,levelPattern;
            int[][] playableLevelCount = new int[2][numSimulations];
            for(int i = 0; i <numSimulations;i++){
                levelChunk = generateLevel(generatorChunk);
                levelPattern = generateLevel(generatorPattern);
                MarioResult resultChunk = game.runGame(agent,levelChunk,30,0,false,1000);
                playableLevelCount[0][i] = (int)resultToStats(resultChunk).returnWinRate();
                MarioResult resultPattern = game.runGame(agent,levelPattern,30,0,false,1000);
                playableLevelCount[1][i] = (int)resultToStats(resultPattern).returnWinRate();
            }

            PrintWriter writer = new PrintWriter("PlayabilityCount.csv", "UTF-8");

            for(int i = 0; i < 2; i++){
                for(int j = 0;j < numSimulations; j++){
                    writer.print(playableLevelCount[i][j] + ", ");
                }
                writer.print("\n");
            }
            writer.close();

        }


    }
