
package nba.predictor;

/**
 *
 * @author rubin
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//JSOUP stuff
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/**
 *
 * @author rubin
 */
public class scraper {

    /**
     * @param args the command line arguments
     */
    String teamAbv;
    double roadPerc;
    double homePerc;
    ArrayList<String> awayOrHome = new ArrayList<String>();
    ArrayList<String> winloss = new ArrayList<String>();

    public scraper(){};
    
    public scraper(String teamName, List teamList, List abv) throws IOException{
        teamAbv = (String)abv.get(teamList.indexOf(teamName));
        Document doc = parseHTML();
        setPerc(doc);
        
    }
    
    public Document parseHTML() throws IOException{
        String URL = "http://www.basketball-reference.com/teams/" + teamAbv + "/2017/gamelog/";

        Document doc;
        doc = Jsoup.connect(URL).get();
        return doc;
    }
    
    public void setPerc(Document doc){
        Elements els = doc.select("td[data-stat = game_location]"); // a with href
        for(Element el: els)
            awayOrHome.add(el.text());
        
        els = doc.select("td[data-stat = game_result]");
        for(Element el: els)
            winloss.add(el.text());
        
        int winCounterHome = 0, winCounterAway = 0;
        int lossCounterHome = 0, lossCounterAway = 0;
        
        for(int i = 0; i < winloss.size(); i++){
            if(awayOrHome.get(i).equals("@"))
                if(winloss.get(i).equals("W"))
                    winCounterAway++;
                else
                    lossCounterAway++;
            else
                if(winloss.get(i).equals("W"))
                    winCounterHome++;
                else
                    lossCounterHome++;     
        }
        
        homePerc = (double)winCounterHome/(winCounterHome + lossCounterHome);
        roadPerc = (double)winCounterAway/(winCounterAway + lossCounterAway);
    }
    
    public double getHomePerc(){
        return homePerc;
    }
    
    public double getRoadPerc(){
        return roadPerc;
    }

    
}
