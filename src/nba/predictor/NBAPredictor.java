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



/**
 *
 * @author rubin
 */
public class NBAPredictor {

    /**
     * @param args the command line arguments
     */
            static String teams[][] = new String[30][17];  //30 Teams, Name + Y + 15 X
            static String teamRosters[][] = new String[30][11]; //30 Teams, Using top two players of each role
            static String playerStats[][] = new String[447][10]; //447 Total Players, 8 Stats of Importance
            static Integer teamAPlayerIndex[] = new Integer[10];
            static Integer teamBPlayerIndex[] = new Integer[10]; 
            

    public static void readData() throws FileNotFoundException{

       //READING IN TEAM STATISTICS *******************************************************************
        File teamStats = new File("teamStats.txt");
        Scanner in = new Scanner(teamStats);           //Set Scanner for inputting team variables
        
        for(int i = 0; i < 30; i++){
                String line = in.nextLine();
                String[] temp = line.split("\\t");
                configureTeamStats(temp, i);
        }
        
        
        //READING IN PLAYER ROSTERS **************************************************************************
        File teamDepth = new File("teamDepth.txt");
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
         File playerStat = new File("playerStats.txt");
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
        
        int oneOrTwo = 0;
        for(int x = 1; x < 11; x++){
            String temp2[] = teamRosters[teamIndex][x].split(" ");
            oneOrTwo = temp2[0].length() - 1;
            
            String nameToCheck = temp2[0] + " " +  temp2[1];
            for(int i = 0; i <446; i++){
               String temp3[] = playerStats[i][0].split(" |-");
               
               if(temp3[0].length() < oneOrTwo)
                   continue;
               
               String firstInitial = temp3[0].substring(0, oneOrTwo);
               String fullName = firstInitial + ". " + temp3[1];
                       
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
                sum += two[i]*Double.parseDouble(playerStats[x][i+2]);    //add positive stats
             else
                 sum-= two[i]*Double.parseDouble(playerStats[x][i+2]);     //Subtract TOV and PF
        }
        
        return sum;
    }
    
    public static void main(String[] args) throws FileNotFoundException {
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
        //Now I need to collect the data for team 1 and team 2
    
        String teamA = "Toronto Raptors";
        String teamB = "LA Clippers";
        int teamAx = 0, teamBx = 0;
        int teamAy = 0, teamBy = 0;
        
        for(int i = 0; i < 30; i++){
            if(teamScore[i][0].equals(teamA))
                teamAx = i;
            else if(teamScore[i][0].equals(teamB))
                teamBx = i;
            
            if(playerScore[i][0].equals(teamA))
                teamAy = i;
            else if(playerScore[i][0].equals(teamB))
                teamBy = i;
            
        }
        
        System.out.println(teamAy + " " + teamBy);
        boolean strongerTeam = false;
        
        if(teamAx > teamBx)
            strongerTeam = true;

        
        
        
        
    }
    
}
