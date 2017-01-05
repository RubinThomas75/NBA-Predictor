# NBA-Predictor
A Java Predictor for Regular Season NBA Games

Datasets: <br />
>	Data for the NBA in a coder friendly format is not easy to come by on the internet. To resolve this, I used Google's Scraping tool available for chrome on various NBA Sites to get the data I need. This is definitely not the most efficient method, nor the most legal one, but it worked. 
    
    Data:  
           - NBA Full Team Statistics; Scraped from stats.nba.com
           - NBA Full Player Statistics; Scraped from stats.nba.com
           - NBA Full Team Rosters; Found on ESPN.com

Algorithm so far: <br />

The first step was straightforward in its concept and implementation; importing and organizing the data into a way that my algorithm will read it. To do this, I imported the Player Rosters for each team, so when a Team is entered through user input, the machine will be able to find the players on that team and thier corresponding statistics.
<br />
Now the next step is a little more tricky. Originally I had a vague concept of combining three aspects: Team Statistics, Player Statistics and a Head to Head record to formulate a sort of prediction confidence interval. In doing so I've realized 2 things; Finding Head to Head stats online is super difficult, and God I hate Statistics.
<br /> 
But I digress. My vague concept somehow became increasingly more vague during implementation. I was left wondering what kind of tools I can use to help formulate my prediction. So, naturally, I used a simple linear regression (apache) to correlate my data. Now I need to figure a weighing system. I looked at the Data in RStudio using Multiple regression, but it really doesnt help as much as simple linear, mainly because a lot of NBA data is multicolinear. 
<br /> 
Now, Rough Idea of what im going to do.

I regressed each team statistics onto win percentage to see which factors more into winning. Now when a team goes head to head, I will look at the data from each team under the scope of the regression, and determine how far away they are in comparison to the other teams. After all, if the worst team in the NBA has the potential to beat the best team, then odds increase if the second best team was in that place instead. know what i mean? Like say maimi upsets the spurs, they are on the board with say a 1% chance. Now if a better team like Golden state vs the spurs, then they should have a >50% chance. I just need to figure out how to do this in the scope of the regression. Maybe compare 1 to 30... We will see. 
<br />
As for player statistics, I regressed each player statistics onto what I believe to be the most important aspect of a player, thier +/-. Now +/- is tricky because, obviously, if a team does better, thier plus minus is going to be better, also regressed on all the players in the NBA, we find pretty low correlation values to points, ast, etc. So I should probably not take this route, maybe I should try PER.

Well Tried it out and didnt work.
