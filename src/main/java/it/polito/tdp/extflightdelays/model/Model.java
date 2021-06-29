package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private Map<Integer, Airport> idMap;
	private ExtFlightDelaysDAO dao;
	
	// variabili per la ricorsione
		private List<Airport> percorsoBest ;
	
	public Model() {
		idMap = new HashMap<Integer,Airport>();
		dao = new ExtFlightDelaysDAO();
		dao.loadAllAirports(idMap);
	}
	
	public String creaGrafo(int distanzaMedia) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		//Aggiungere i vertici
		Graphs.addAllVertices(grafo, idMap.values());
		
		for(Rotta rotta : dao.getRotte(idMap, distanzaMedia)) {
			//controllo se esiste già un arco
			//se esiste, aggiorno il peso
			DefaultWeightedEdge edge = grafo.getEdge(rotta.getA1(), rotta.getA2());
			if(edge == null) {
				Graphs.addEdge(grafo, rotta.getA1(), rotta.getA2(), rotta.getPeso());
			} else {
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = (peso + rotta.getPeso())/2;
				grafo.setEdgeWeight(edge, newPeso);
			}
		}
		return String.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size()) ;
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Rotta> getRotte(){
		//uso la classe Rotta per salvare gli archi del grafo con il relativo peso
		List<Rotta> rotte = new ArrayList<Rotta>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			rotte.add(new Rotta(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
		}
		return rotte;
	}
	
	
	public List<Airport> percorsoMigliore(Airport partenza, double soglia) {
		this.percorsoBest =  new ArrayList<Airport>();
		
		List<Airport> parziale = new ArrayList<Airport>() ;
		parziale.add(partenza) ;
		
		cerca(parziale, 1, soglia) ;
		
		return this.percorsoBest ;
	}
	
private void cerca(List<Airport> parziale, int livello, double soglia) {
		
	Airport ultimo = parziale.get(parziale.size()-1) ;
		
		// caso terminale: ho trovato l'arrivo
		
		if (parziale.size()>1 && getPesoParziale(parziale)<soglia)	{	
			 if(parziale.size() > this.percorsoBest.size() ) {
				this.percorsoBest = new ArrayList<>(parziale) ;
				
			}
		}else if (parziale.size()>1 && getPesoParziale(parziale)>=soglia) {
			return;
		}
		
		// generazione dei percorsi
		// cerca i successori di 'ultimo'
		for(DefaultWeightedEdge e: this.grafo.edgesOf(ultimo)) {
			
				Airport prossimo = Graphs.getOppositeVertex(this.grafo, e, ultimo) ;
				
				if(!parziale.contains(prossimo)) { // evita i cicli
					parziale.add(prossimo);
					cerca(parziale, livello + 1, soglia);
					parziale.remove(parziale.size()-1) ;
				}
		}	
	}

	public double getPesoParziale(List<Airport> parziale) {
		double peso = 0;
		for (int i=0; i<parziale.size()-1; i++) { 
			DefaultWeightedEdge dwe = this.grafo.getEdge(parziale.get(i), parziale.get(i+1));
			peso+=this.grafo.getEdgeWeight(dwe);
		}
		return peso;
	}

	public Map<Integer, Airport> getIdMap() {
		return idMap;
	}

	public SimpleWeightedGraph<Airport, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
	
	
	
}
