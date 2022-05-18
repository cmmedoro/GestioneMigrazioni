package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {
	
	//Coda degli eventi --> comportamento dinamico della simulazione
		//evento principale: ingresso di persone in uno stato ---> calcolo numero di persone stanziali, quello di persone che si devono
		//spostare e in quale stato
	private PriorityQueue<Event> coda;
	
	//Parametri di simulazione --> per poterla avviare
	private int nInizialeMigranti;
	private Country nazioneIniziale;
	
	//Output della simulazione
	private int nPassi; // variabile T del testo
	private Map<Country, Integer> persone; //per ogni nazione, quanti migranti sono stanziali in quella nazione 
	//oppure List<CountryAndNumber> personeStanziali; ---> meno efficiente
	//Stato del mondo simulato
	private Graph<Country, DefaultEdge> grafo;
	//Map persone: Country -> Integer
	
	//costruttore
	public Simulatore(Graph<Country, DefaultEdge> grafo) {
		super();
		this.grafo = grafo;
	}
	
	public Map<Country, Integer> getPersone() {
		return persone;
	}

	public void inizializza(Country partenza, int migranti) {
		//prepara la simulazione
		this.nazioneIniziale = partenza;
		this.nInizialeMigranti = migranti;
		//inizializzo anche la struttura dati che userò per tener traccia del numero degli stanziali della simulazione
		this.persone = new HashMap<>(); //così se vengono fatte numerose simulazioni, non mi porto dietro i residui di quella prec.
		for(Country c : this.grafo.vertexSet()) {
			this.persone.put(c, 0);
		}
		this.coda = new PriorityQueue<>();
		this.coda.add(new Event(1, this.nazioneIniziale, this.nInizialeMigranti));
	}
	
	public void run() {
		//va avanti il simulatore
		while(!this.coda.isEmpty()) {
			Event e = this.coda.poll();
			//elaborazione evento
			processEvent(e);
		}
	}

	private void processEvent(Event e) {
		int stanziali = e.getPersone()/2;
		int migranti = e.getPersone() - stanziali;
		int confinanti = this.grafo.degreeOf(e.getNazione());
		int gruppiMigranti = migranti / confinanti;
		stanziali += migranti % confinanti;
		
		this.persone.put(e.getNazione(), this.persone.get(e.getNazione())+stanziali);
		
		this.nPassi = e.getTime();
		if(gruppiMigranti != 0) {
			//genero altri eventi ---> dal grafo mi faccio dire quali sono gli stati confinanti
			for(Country vicino : Graphs.neighborListOf(this.grafo, e.getNazione())) {
				this.coda.add(new Event(e.getTime()+1, vicino, gruppiMigranti));
			}
		}
	}

	public int getnPassi() {
		return nPassi;
	}
}
