/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nba.predictor;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import java.lang.Math;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//Quick GUI stuff
import javafx.stage.Stage;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;



/**
 *
 * @author rubin
 */
public class NBAPredictor extends Application{

    /**
     * @param args the command line arguments
     */
            static String teams[][] = new String[30][17];  //30 Teams, Name + Y + 15 X
            static String teamRosters[][] = new String[30][11]; //30 Teams, Using top two players of each role
            static String playerStats[][] = new String[447][10]; //447 Total Players, 8 Stats of Importance
            static Integer teamAPlayerIndex[] = new Integer[10];
            static Integer teamBPlayerIndex[] = new Integer[10]; 
            static ArrayList<String> teamList = new ArrayList<String>();
            static ArrayList<String> teamListAbv = new ArrayList<String>();
            

    public static void readData() throws FileNotFoundException{

       //READING IN TEAM STATISTICS *******************************************************************
        File teamStats = new File("data/teamStats.txt");
        Scanner in = new Scanner(teamStats);           //Set Scanner for inputting team variables
        
        for(int i = 0; i < 30; i++){
                String line = in.nextLine();
                String[] temp = line.split("\\t");
                configureTeamStats(temp, i);
        }
        
        
        //READING IN PLAYER ROSTERS **************************************************************************
        File teamDepth = new File("data/teamDepth.txt");
        in = new Scanner(teamDepth);                   //Set Scanner for inputting player Roster
       
        for(int i = 0; i < 30; i++){
            if(teamRosters[i][0] == null)
                teamRosters[i][0] = in.nextLine();

            int counter = 1;
            for(int x = 0; x < 15; x++){
                 String line = in.nextLine();               //Estimating only Vital Players, by taking the top two in Each Role
               
                 if(line.indexOf("-") == -1){
                     teamRosters[i+1][0] = line;
                     break;
                 }
                 
                 String[] temp = line.split("-");
                 if(temp[0].indexOf("1") != -1 || temp[0].indexOf("2") != -1){
                     if(counter <= 10)
                         teamRosters[i][counter] = temp[1];
                         counter++;
                 }
            }
            counter = 1;
        }
        
         //READING IN PLAYER STATISTICS *************************************************************************
         File playerStat = new File("data/playerStats2.txt");
         in = new Scanner(playerStat);
         
         for(int i = 0; i < 446; i++){
             String line = in.nextLine();
             String[] temp = line.split("\\t");
             configurePlayerStats(temp, i);
         }
       
        
    }
    
    public static void configureTeamStats(String[] temp, int index){
     /*
        Team INDEX:          ORIG data INDEX
        0 = Team Name        1
        1 = Win% (Y)         5
        2 = PTS              7
        3 = FG%              10
        4 = 3PM              11
        5 = 3P%              13
        6 = FTM              14
        7 = FT%              16
        8 = OREB            17
        9 = DREB            18
        10 = TREB            19
        11 = AST             20
        12 = TOV             21 
        13 = STL             22
        14 = BLK             23
        15 = PF              25
        16 = PFD             26
        */
        
        Integer[] importantStats = {1,5,7,10,11,13,14,16,17,18,19,20,21,22,23,25,26};
        int i = 0;
        for(int x: importantStats){
            teams[index][i] = temp[x];
            i++;
        }
    }
    
    public static void configurePlayerStats(String temp[], int index){
    /*
        Player INDEX       Orig data INDEX
        0 = Player Name    1
        1 = GamesPlayed    4
        2 = PTS            8
        3 = REB            20
        4 = AST            21
        5 = STL            23
        6 = BLK            24
        7 = TOV            22
        8 = PF             25
        9 = +/-            28
    */
    
      Integer[] importantStats = {1,4,8,20,21,23,24,22,25,28};
      int i = 0;
      for(int x: importantStats){
          playerStats[index][i] = temp[x];
          i++;
      }
    }
    /*
        Method to regress team statistics on win% to discover correlation coef R
        @Return double[] holding coef
    */
    public static Double[] regressTD(){
        Double[] toReturn = new Double[15];
        SimpleRegression sr = new SimpleRegression();
        
        /*
        Attempt at Standardizing the numbers for more accurate prediction values 
        double ysum = 0, ySD = 0, ymean = 0;
             for(int i = 0; i < 30; i++)
                 ysum+= Double.parseDouble(teams[i][1]);
             
        ymean = ysum/30;
        ysum = 0;
        
            for(int i = 0; i < 30; i++)
                ysum += Math.pow(Double.parseDouble(teams[i][1]) - ymean, 2);
        
        ySD = ysum/30;        
       */
            
        for(int x = 2; x < 17; x++){
             for(int i = 0; i < 30; i++){
                 sr.addData(Double.parseDouble(teams[i][x]), Double.parseDouble(teams[i][1]));
             }
        
               toReturn[x-2] = sr.getR(); 
               sr.clear();
        }
        
        return toReturn;
        
    }
    /*
        Method to regress player statistics on individual +/-  score
        Condition that they have participated in > 5 games
        @return double[] holding coef
    */
    public static Double[] regressPD(){
        Double[] toReturn = new Double[7];
        SimpleRegression sr = new SimpleRegression();
        double s = 0;
        
        for(int x = 2; x < 9; x++){
            for(int i = 0; i <446; i++){
                if(Integer.parseInt(playerStats[i][1]) > 5)
                    sr.addData(Double.parseDouble(playerStats[i][x]), Double.parseDouble(playerStats[i][9]));
            }
            toReturn[x-2] = sr.getR();
            sr.clear();
        
        }

           return toReturn;
    }
    /*
        SUPER INEFFICIENT METHOD...
        NEED TO FIX THIS...
    */
    public static Integer[] getPlayerIndex(String team){
        
        Integer[] toReturn = new Integer[10];
        String cityTeam = "";
        String[] temp = team.split(" ");
        if(temp.length == 2)
            cityTeam = temp[0];
        else if(temp.length == 3)
            cityTeam = temp[0] + " " + temp[1];
        
        
        int teamIndex = 0;
        for(int i = 0; i < 30; i++){
            if(cityTeam.equals(teamRosters[i][0]))
                teamIndex = i;
        }
        
        int numLettersInInitial = 0;
        for(int x = 1; x < 11; x++){
            String temp2[] = teamRosters[teamIndex][x].split(" ");          //GET BOTH NAMES from TEAM ROSTER
            numLettersInInitial = temp2[0].length() - 1;                              //The length of the first name from the roster - 1. SO 
                                                                            // FOR EXAMPLE: JR SMITH = one or two would be two.
                                                                                       // Or MARC. MORRIS = one or two would be four.
            String nameToCheck = temp2[0] + " " +  temp2[1];               //string nameToCheck is firstname space lastname
            
            for(int i = 0; i <446; i++){
               String temp3[] = playerStats[i][0].split("\\s|-");
               //NENE CASE
               if(temp3[0].equals("Nene")){
                   if(nameToCheck.equals("N. Hilario")){
                        toReturn[x-1] = i;
                        break;
                    }
                 continue;
               }

               if(temp3[0].length() < numLettersInInitial)
                   continue;
               
               String firstInitial = temp3[0].substring(0, numLettersInInitial);
               String fullName = "" ;
                   fullName = firstInitial + ". " + temp3[1];

               if(fullName.equals(nameToCheck))
                   toReturn[x-1] = i;
               
            }
        }

        
        return toReturn;
    }
    
    public static String[][] sort(String[][] toSort){
        String[][] newTeamScore = new String[30][2];

        for(int x = 0; x < 30; x++){
             double currmax = 0;
             int saveI = -1;
            for(int i = 0; i < 30; i++){
                 if(Double.parseDouble(toSort[i][1]) > currmax){
                     currmax = Double.parseDouble(toSort[i][1]);
                     saveI = i;
                 }
            }
            newTeamScore[x][0] = toSort[saveI][0];
            newTeamScore[x][1] = Double.toString(currmax);
            toSort[saveI][1] = Double.toString(0);
        }
        return newTeamScore;
    }
    
    public static String[][] scorePlayers(){
        String playerScores[][] = new String[30][2];
        for(int i = 0; i < 30; i++){
            Integer x[] = getPlayerIndex(teams[i][0]);
            
            playerScores[i][0] = teams[i][0];
           playerScores[i][1] = Double.toString(getPlayerScore(x));
        }
        playerScores = sort(playerScores);
        return playerScores;
    }
    
    public static String[][] scoreTeams(){
        String teamScore[][] = new String[30][2];
        
        Double[] one = regressTD();
        
        for(int i = 0; i < 30; i++){
            teamScore[i][0] = teams[i][0];
            double score = 0;
            
            for(int x = 2; x < 17; x++){
                score+= one[x - 2]*Double.parseDouble(teams[i][x]);
            }
            
            teamScore[i][1] = Double.toString(score);
        }
        teamScore = sort(teamScore);
        return teamScore;
    }
    
    public static double getPlayerScore(Integer[] teamIndex){
        Double[] two = regressPD();
        double sum = 0;
        for(int x :teamIndex)
            for(int i = 0; i < 7; i++){
             if(i < 5)
                sum += two[i]*(Double.parseDouble(playerStats[x][i+2]));    //add positive stats
             else
                 sum-= two[i]*Double.parseDouble(playerStats[x][i+2]);     //Subtract TOV and PF
        }
        
        return sum;
    }
    
    public static void main(String[] args)  throws FileNotFoundException {
      launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception{
       primaryStage.setTitle("NBA - Predictor");         
       
       Pane mainPane = new Pane();
       
       Button go = new Button("Predict");
       Label awayTeam = new Label("Away Team");
       Label homeTeam = new Label("Home Team");
       Label atLabel = new Label("@");
       
       readInTeamList();  //to Populate choiceboxes and for SCRAPER uses

       scraper s = new scraper("Golden State Warriors", teamList, teamListAbv);
       System.out.println(s.getHeadToHeadPerc("Golden State Warriors", "New York Knicks"));
       System.exit(0);

        ChoiceBox<String> awayTeams = new ChoiceBox<>();
        awayTeams.getItems().addAll(teamList);
        
        ChoiceBox<String> homeTeams = new ChoiceBox<>();
        homeTeams.getItems().addAll(teamList);
        
        awayTeams.setTranslateX(50);
        awayTeams.setTranslateY(50);
        homeTeams.setTranslateX(330);
        homeTeams.setTranslateY(50);
        
        awayTeam.setTranslateX(50);
        homeTeam.setTranslateX(330);
        awayTeam.setTranslateY(20);
        homeTeam.setTranslateY(20);
        
        atLabel.setTranslateX(290);
        atLabel.setTranslateY(55);
        
        go.setTranslateX(580);
        go.setTranslateY(50);
        
        mainPane.getChildren().addAll(awayTeams, homeTeams);
        mainPane.getChildren().addAll(awayTeam, homeTeam, atLabel, go);
        Scene mainScene = new Scene(mainPane, 680, 120);
        
        
        go.setOnAction(e-> 
            {
           try {
               predict(awayTeams.getValue(), homeTeams.getValue());
           } catch (FileNotFoundException ex) {
               Logger.getLogger(NBAPredictor.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
        );
                
        primaryStage.setScene(mainScene);
        primaryStage.show(); 
    }
    
        
        
    
    public void readInTeamList() throws FileNotFoundException{
        File teamListFile = new File("data/teamList.txt");
        Scanner in = new Scanner(teamListFile);           //Set Scanner for inputting team variables
        
        for(int i = 0; i < 30; i++){
            String line = in.nextLine();
            teamList.add(line);
        }
        
        File teamListAbvFile = new File("data/teamListAbv.txt");
        in = new Scanner(teamListAbvFile);           //Set Scanner for inputting team variables
        
        for(int i = 0; i < 30; i++){
            String line = in.nextLine();
            teamListAbv.add(line);
        }
      
    }
    
    public static void predict(String teamA, String teamB)  throws FileNotFoundException {
        
        //SETTING UP GUI STUFF FOR POP UP WINDOW
        Stage infoStage = new Stage();
        infoStage.setTitle("Prediction");
        Pane infoPane = new Pane();
        Scene infoScene = new Scene(infoPane, 700, 200);
        
        /* ERROR HANDLING STUFF HERE.
        if(teamA.equals(teamB)){
            Label warning = new Label("A team cannot verse it self; ABORTING");
            infoPane.getChildren().add(warning);
            Button aborting = new Button("OK");
            infoPane.getChildren().add(aborting);
            aborting.setTranslateX(30);
            aborting.setTranslateY(30);
            aborting.setOnAction(e->
                System.exit(0)
            );            
        }
        
        */
        
        //AlgoStart
        
        readData(); 
        String[][] teamScore = scoreTeams();
        String[][] playerScore = scorePlayers();       
        
        /*Duplicate Check using Hash Map
        HashSet<String> set = new HashSet<String>();
        for(int i = 0; i < 30; i++){
            for(int j = 0; j < 11; j++){
                if(!set.add(teamRosters[i][j]))
                    System.out.println(teamRosters[i][j]);
            }
        }
        */
        
        
        //Heres where I will Prompt input for Team 1 and Team 2
        //Now I need to collect the data for team 1 and team 2\
        
        
        int teamAx = 0, teamBx = 0;
        int teamAy = 0, teamBy = 0;
        
        for(int i = 0; i < 30; i++){
            if(teamScore[i][0].equals(teamA)){
                teamAx = i;
            }
            else if(teamScore[i][0].equals(teamB))
                teamBx = i;
            
            if(playerScore[i][0].equals(teamA))
                teamAy = i;
            else if(playerScore[i][0].equals(teamB))
                teamBy = i;
            
        }
        
        int teamDiff = teamAx - teamBx;
        int playerDiff = teamAy - teamBy;
        
        double teamProb = 50 + (teamDiff * -1.7);
        double playerProb = 50 + (playerDiff * -1.7);
       
        DecimalFormat df = new DecimalFormat("#.####");

        Label one = new Label("According to current team statistics, The " + teamA + " has a " + df.format(teamProb) + "% Chance of winning");
        Label two = new Label("According to current player statistics, The " + teamA + " has a " + df.format(playerProb) + "% Chance of winning");
        
        one.setTranslateX(20);
        one.setTranslateY(20);
        two.setTranslateX(20);
        two.setTranslateY(50);
        
        infoPane.getChildren().addAll(one, two);
        
        double lastProb = (teamProb + playerProb)/2;
        
        boolean win = false;
        if(lastProb < 50)
            win = false;
        else
            win = true;
        
        Label finalLabel;
        if(win == true)
            finalLabel = new Label("I predict The " + teamA + " will win with " + df.format(lastProb) +"% confidence");
        else
            finalLabel = new Label("I predict the " + teamB + " will win with " + df.format((100.0 - lastProb)) +"% confidence");
            
        finalLabel.setTranslateX(20);
        finalLabel.setTranslateY(90);
        infoPane.getChildren().add(finalLabel);
        
        
        infoStage.setScene(infoScene);
        infoStage.show();
        
        
      ////  **try multiplying by 1.7
      
    }
}
