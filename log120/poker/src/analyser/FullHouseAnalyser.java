// Julien Limoges (2011) LIMJ23049109
// julien.limoges.2 (at) ens.etsmtl.ca
package poker.analyser;

import java.util.EnumMap;
import java.util.Iterator;

import poker.RequestHandAnalysis;
import poker.cards.Card;
import poker.cards.Rank; 
import poker.cards.Suit; 
import poker.hands.PokerRank; 

public class FullHouseAnalyser extends AbstractHandAnalyser {

  public void processRequest(RequestHandAnalysis request) {
    if (analyseHand(request))
      request.setPokerRank(PokerRank.FullHouse);
    else if (successor != null)
      successor.processRequest(request);
  }

  protected boolean analyseHand(RequestHandAnalysis request) {
    return request.getRanks().containsValue(new Integer(3))
      && request.getRanks().containsValue(new Integer(2));
  }

}
