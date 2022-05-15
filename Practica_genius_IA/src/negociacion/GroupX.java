package negociacion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.actions.Offer;
import genius.core.list.Tuple;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.persistent.PersistentDataType;
import genius.core.persistent.StandardInfo;
import genius.core.persistent.StandardInfoList;

/**
 * Sample party that accepts the Nth offer, where N is the number of sessions
 * this [agent-profile] already did.
 */
public class GroupX extends AbstractNegotiationParty {

	private Bid lastReceivedBid = null;
	private int nrChosenActions = 0; // number of times chosenAction was called.
	private StandardInfoList history;
	private Bid bidRandom;
	private float lastS = 1;
	
	private float getS(float RU, float B) {
		return (float) Math.pow(((( 1 - (1 - RU) ) * (getTimeLine().getTime())) ), 1/B);
	}
	@Override
	public void init(NegotiationInfo info) {

	}
	
	public Action chooseAction(List<Class<? extends Action>> validActions) {
		// generamos una random Bid
		bidRandom = generateRandomBid();
		
		nrChosenActions++;
		// utilidad de reserva minimo de aceptacion
		float RU = (float) 0.75;
		// lineal
		int beta = 1 ;
		
		if (nrChosenActions > history.size() & lastReceivedBid != null) {
			// dependiendo del valor que obtenemos en la ecuacion de concesion temporal Aceptamos
			if(getUtility(lastReceivedBid) > lastS) {
				lastS = getS(RU,beta);
				return new Accept(getPartyId(),lastReceivedBid );
			}
		}
		//mientras no obtengamos una oferta mayor de la que esperamos en s generamos una nueva oferta
		while (getUtility(bidRandom) < getS(RU,beta)  ) {
			bidRandom = generateRandomBid();
		}
		//Devolvemos la offer
		return new Offer(getPartyId(),bidRandom );
	}

	@Override
	public void receiveMessage(AgentID sender, Action action) {
		super.receiveMessage(sender, action);
		if (action instanceof Offer) {
			lastReceivedBid = ((Offer) action).getBid();	
		}
	}

	public String getDescription() {
		return "accept Nth offer";
	}

}
