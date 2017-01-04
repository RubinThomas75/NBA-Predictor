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
            static String playerStats[][] = new String[447][8]; //447 Total Players, 7 Stats of Importance
            

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
        1 = PTS            8
        2 = REB            20
        3 = AST            21
        4 = STL            23
        5 = BLK            24
        6 = TOV            22
        7 = PF             25
    */
    
      Integer[] importantStats = {1,8,20,21,23,24,22,25};
      int i = 0;
      for(int x: importantStats){
          playerStats[index][i] = temp[x];
          i++;
      }
    }
    
    public static Double[] regressTD(){
        Double[] toReturn = new Double[15];
        SimpleRegression sr = new SimpleRegression();
        double s = 0;
        for(int x = 2; x < 17; x++){
             for(int i = 0; i < 30; i++){
                   sr.addData(Double.parseDouble(teams[i][1]), Double.parseDouble(teams[i][x]));
             }
        
               toReturn[x-2] = sr.getR(); 
               sr.clear();
        }
        
        return toReturn;
        
        
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        readData(); 
        Double[] rArray = regressTD();
        
        //Duplicate Check using Hash Map
        HashSet<String> set = new HashSet<String>();
        for(int i = 0; i < 30; i++){
            for(int j = 0; j < 11; j++){
                if(!set.add(teamRosters[i][j]))
                //    System.out.println(teamRosters[i][j]);
            }
        }
        
        
        //Heres where I will Prompt input for Team 1 and Team 2
        //Now I need to collect the data for team 1 and team 2
        
        //Team STATS - ROSTER FIRST NAME
        //TO PLAYER -- Find NAME From Rost, double check first initial of team name from team stats
        
        
        
    }
    
}
