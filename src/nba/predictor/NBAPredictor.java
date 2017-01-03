/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nba.predictor;


import java.io.File;
import java.io.FileNotFoundException;
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
            static String teams[][] = new String[30][21];

    public static void readData() throws FileNotFoundException{

        File teamStats = new File("teamStats.txt");
        Scanner in = new Scanner(teamStats);
        
        for(int i = 0; i < 30; i++){
                String line = in.nextLine();
                String[] temp = line.split("\\t");
                configureTeamStats(temp, i);
        }

    }
    
    public static void configureTeamStats(String[] temp, int index){
                                               //   Team INDEX:          ORIG data
        teams[index][0] = temp[1];             //   0 = Team Name        1
        teams[index][1] = temp[5];             //   1 = Win% (Y)         5
        teams[index][2] = temp[7];             //   2 = PTS              7
        teams[index][3] = temp[10];             //   3 = FG%              10
        teams[index][4] = temp[11];             //   4 = 3PM              11
        teams[index][5] = temp[13];             //   5 = 3P%              13
        teams[index][6] = temp[14];             //   6 = FTM              14
        teams[index][7] = temp[16];             //   7 = FT%              16
        teams[index][8] = temp[17];             //   8 = OREB            17
        teams[index][9] = temp[18];             //   9 = DREB            18
        teams[index][10] = temp[19];            //   10 = TREB            19
        teams[index][11] = temp[20];            //   11 = AST             20
        teams[index][12] = temp[21];            //   12 = TOV             21 
        teams[index][13] = temp[22];            //   13 = STL             22
        teams[index][14] = temp[23];            //   14 = BLK             23
        teams[index][15] = temp[25];              //   15 = PF              25
        teams[index][16] = temp[26];             //   16 = PFD             26
        
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
        
    }
    
}
