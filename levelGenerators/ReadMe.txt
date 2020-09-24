
-----------Instructions for running generators submitted for group T------------------

Files submitted - ChunkGenerator.java, patternExtract.java, PlayLevel.java, MarioStats.java

The two generators are packaged under a package called GeneratorT

---First generator----
Create a folder called GeneratorT inside the levelGenerators directory in the Mario Framework and 
store the file 'ChunkGenerator.java' inside it.
ChunkGenerator.java contains the generator that implements the chunking based generating algorithm

MarioLevelGenerator generator = new GeneratorT.ChunkGenerator(3,15);

The constructor has two parameters - first parameter is chunk size (also reffered to as block size) and 
second parameter is number of levels to pick chunks from to be used as training corpus.

Run the PlayLevel.java file attached in our code folder to generate levels using the ChunkGenerator.

---Second generator----

patternExtract.java contains the generator that implements the pattern extraction generating algorithm

MarioLevelGenerator generator = new GeneratorT.patternExtract(3);

The constructor takes one parameter - the length of the pattern

-----Changes made to Playlevel.java----

We have added two methods checkPlayability and countPlayableLevel

checkPlayability(game,agent,level,numSimulations);
This method simulates the agent over specified number of runs for the level in the argument.
It returns the average win rate over the number of simulations.

countPlayableLevel(game,agent,numSimulations);
This method creates an array of 0 and 1 values for win-rate of agent over levels created by both generators
and writes to a csv file which is plotted externally for experimental results.

A block of code is added to PlayLevel.java that calls checkPlayability method to test generated levels and regenerate again
when level is unplayable.


-----Changes made to MarioStats.java-----

One method returnWinRate() has been added that returns the floating point value of the calculated win rate.