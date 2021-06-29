package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		System.out.println(model.creaGrafo(200)+"\n\n");
		List<Airport> list = model.percorsoMigliore(model.getIdMap().get(223), 20000);
		for (Airport a : list) {
			System.out.println(a);
		}
		System.out.println("\n\npeso percorso: "+model.getPesoParziale(list));
		
	}

}
