// Julien Limoges (2011) LIMJ23049109
// julien.limoges.2 (at) ens.etsmtl.ca
package poker.analyser;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.ArrayList;

import poker.RequestHandAnalysis;
import poker.cards.Card;
import poker.cards.Rank; 
import poker.cards.Suit; 
import poker.hands.PokerRank; 
import poker.HandAnalyser;

public class RoyalStraightFlushAnalyser extends AbstractHandAnalyser {

  public void processRequest(RequestHandAnalysis request) {
    if (analyseHand(request))
      request.setPokerRank(PokerRank.RoyalStraightFlush);
    else if (successor != null)
      successor.processRequest(request);
  }

  protected boolean analyseHand(RequestHandAnalysis request) {
    Suit flush = request.isFlush();;
    if (flush != Suit.None){
      ArrayList<Card> cards = request.getCardsBySuits().get(flush);
      return HandAnalyser.hasStraight(cards).equals(Rank.Ace);
    }
    else
      return false;    
  }

}
