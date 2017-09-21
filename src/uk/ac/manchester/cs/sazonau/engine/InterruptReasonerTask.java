package uk.ac.manchester.cs.sazonau.engine;

import java.util.TimerTask;

import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class InterruptReasonerTask extends TimerTask {
	
	private OWLReasoner reasoner;
		
	public InterruptReasonerTask(OWLReasoner reasoner) {
		super();
		this.reasoner = reasoner;
	}

	@Override
	public void run() {
		if (reasoner != null) {
			try {
				reasoner.interrupt();
			} catch (Exception e) {
//				System.out.println("Exception while reasoner interruption");
			}
			reasoner.dispose();
//			System.out.println("Aborted: Reasoning task exceeded timeout");
		}
	}

}
