package GeneratorT;

import engine.core.MarioLevelModel;
import engine.helper.MarioTimer;
import levelGenerators.MarioLevelGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class patternExtract implements MarioLevelGenerator {
    private int FLOOR_PADDING = 3;
    private int pattern_length ;
    Random random;
    public patternExtract(int pattern_length)
    {
        this.pattern_length = pattern_length;
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        model.clearMap();
        return setlevel(model);
    }

    @Override
    public String getGeneratorName() {
        return null;
    }

    private String setlevel(MarioLevelModel model) {
        random = new Random();
        //Stores patterns and all the characters that could occur after this pattern in key value pairs
        HashMap<String, char[]> skymap = new HashMap<>(); //Storing patterns from Map height [0,4]
        HashMap<String, char[]> imap = new HashMap<>();   //Storing patterns from Map height [5,11]
        HashMap<String, char[]> gmap = new HashMap<>();   //Storing patterns from Map height [12,15]
        for (int count = 1; count < 16; count++) {
            String filepath = String.format("levels\\original\\lvl-%s.txt", String.valueOf(count));
            File f = new File(filepath);
            try {

                Scanner sc = new Scanner(f); //Scanner object to read level files
                int linecount = 0;   //keeps
                while (sc.hasNextLine()) {
                    String str = sc.nextLine();
                    for (int i = 1; i < str.length() - pattern_length; i++)
                    {
                        String subs = str.substring(i, i + pattern_length);
                        char next = str.charAt(i + pattern_length);
                        if(next=='F'|| next=='M')
                        {
                            next ='-';
                        }
                        if (linecount >= 0 && linecount <= 4) {
                            if (skymap.containsKey(subs) ) {
                                String st1 = String.copyValueOf(skymap.get(subs));
                                st1 = st1 + next;
                                skymap.remove(subs);
                                skymap.put(subs, st1.toCharArray());
                            } else {
                                char c[] = {next};
                                skymap.put(subs, c);
                            }
                        }//END IF [0,4]
                        if (linecount >= 5 && linecount <= 12) {
                            if (imap.containsKey(subs)) {
                                String st1 = String.copyValueOf(imap.get(subs));
                                st1 = st1 + next;
                                imap.remove(subs);
                                imap.put(subs, st1.toCharArray());
                            } else {
                                char c[] = {next};
                                imap.put(subs, c);
                            }
                        }//END IF [5,11]
                        if (linecount >= 13 && linecount <= 15)
                        {
                            if (gmap.containsKey(subs)) {
                                String st1 = String.copyValueOf(gmap.get(subs));
                                st1 = st1 + next;
                                gmap.remove(subs);
                                gmap.put(subs, st1.toCharArray());
                            } else {
                                char c[] = {next};
                                gmap.put(subs, c);
                            }
                        }//END IF [12,15]

                    }//for I loop end
                    linecount++;
                }//WHILE LOOP END


            } catch (Exception e)
            {
                e.printStackTrace();
            }//END TRY-CATCH BLOCK

        }//END FOR LOOP

        for (int x = pattern_length; x < model.getWidth()-3; x++)
        {
            for (int y = 0; y < model.getHeight(); y++)
            {
                String target = "";
                for (int idk = x - pattern_length; idk < x; idk++)
                {
                    target = target + Character.toString(model.getBlock(idk, y));

                }
                if (y >= 0 && y <= 4)
                {
                    if (skymap.containsKey(target))
                    {
                        char c[] = skymap.get(target);
                        int size = c.length;
                        int val = random.nextInt(size);
                        model.setBlock(x, y, c[val]);

                    }
                }//END IF [0 4]
                if (y >= 5 && y <= 12)
                {
                    if (imap.containsKey(target))
                    {
                        char c[] = imap.get(target);
                        int size = c.length;
                        int val = random.nextInt(size);
                        model.setBlock(x, y, c[val]);

                    }
                }//END IF [5 11]
                if (y >= 13 && y <= 15)
                {
                    if (gmap.containsKey(target))
                    {
                        char c[] = gmap.get(target);
                        int size = c.length;
                        int val = random.nextInt(size);
                        model.setBlock(x, y, c[val]);

                    }
                }//END IF [12 15]
            }
        }

        model.setRectangle(0, 14, FLOOR_PADDING, 2, MarioLevelModel.GROUND);
        model.setRectangle(model.getWidth() - 1 - FLOOR_PADDING, 14, FLOOR_PADDING, 2, MarioLevelModel.GROUND);
        model.setBlock(FLOOR_PADDING / 2, 13, MarioLevelModel.MARIO_START);
        model.setBlock(model.getWidth() - 1 - FLOOR_PADDING / 2, 13, MarioLevelModel.MARIO_EXIT);
        return model.getMap();
    }//END OF setlevel METHOD
}
