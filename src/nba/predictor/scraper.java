
package nba.predictor;

/**
 *
 * @author rubin
 */


import java.io.IOException;
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
    
    public scraper(){};
    public scraper(String teamName, List teamList, List abv){
        String teamAbv = (String)abv.get(teamList.indexOf(teamName));
        
    }
    
    
    
    public static void main(String[] args) throws IOException{
        
        Document doc;
        doc = Jsoup.connect("http://www.basketball-reference.com/teams/GSW/2017/gamelog/").get();
        Elements links = doc.select("td[data-stat = game_location]"); // a with href
        for(Element link: links){
            if(link.text().equals("@"))
                continue;
            System.out.println(link.text());
        }
        

    }
    
}
