# AI Matrix Payoff game

- [Description](#description)
- [Target](#target)
- [Author](#author)
- [Lab Account](#lab-account)
- [Compilation](#compilation)
- [Run](#run)
- [AI Algorithm](#ai-algorithm)
- [Additional functionalities](#additional-functionalities)



## Description
This program is a game where there is a payoff matrix (NxN) filled with Vector2()
This matrix is symmetrical
The MainAgent will ask the players of the tournament about their movements, each player do the movement without knowing the current enemy movement.
After the movement the MainAgent will inform to the players about the movement results

### Target
This program is developed targetting to learn to program an intelligent AI with JADE

### Author
  Carlos Daniel Garrido Suárez xuzon69@gmail.com
  
### Lab Account
psi27

### Compilation
Just compile it with like you would compile any java with the classpath jade

Needs java 1.8

### Run

You must use

 > java -cp .:jade.jar jade.Boot "referee:psi27_MainAg;Player1:psi27_Random;Player2:psi27_Fixed;Player3:psi27_Intel1"
 
The only runnable players class are psi27_Random and psi27_Fixed and psi27_Intel1, the psi27_Player is an abstract base class  
The psi27_MainAg class invoke my GUI
My Intelligent player is psi27_Intel1

## AI Algorithm

First the AI will start the match wihout knowing the matrix so it will choose a random value.

After the first movement now it knows some of the matrix and begin the real algorithm:

  * **First** I decide if I have a [dominant](#dominant) move
  
      * If I have one or more decide wich is better (harms the most the enemy) and thats the choosen movement
      
      * If no I start the discover matrix algorithm
  
  * **Discover matrix** algorithm
   
      * I see what percentage of each movement I don't know and store them
      
      * If the percentage surpass my factor is a candidate to know
      
      * I ponderate the know candidates by known payoff difference
      
      * If I have a candidate that will be the movement, if no, it is time to do the strategy
     
  * **Strategy**
  
    * Get the dominants (it have to be null but just in case), Nash Equilibriums, Max Min strategies
    
    * If I'm **winning** I temporary choose a Max Min strategy
    
    * If I'm **losing** I temporary choose the best winner movement, 
    this movement is the movement where I win or draw the most of all the possible movements
    
    * Afer all this strategies it is time to look at the **past**
    
      * If the enemy player was playing the same movement for a factor turns in the recent past I choose the **Better Response** for that,
       if not don't do anything
       
      * This turns factor is initialized to 4 and if I'm wrong in my assumption I increment that factor
  
      
  
### Dominant
My own definition of dominant movement in this game is a movement where you can only win or draw in that movement

## Additional functionalities

* The Main Agent is searching for players, if he founds a new player he will see 
if the player has another service will type that player with the name of that service

* You can turn on / off the log and a detailed log version

* We can change properties directly in the GUI

* We can rename the players as we want

