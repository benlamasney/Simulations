import java.util.concurrent.TimeUnit;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class Generator extends SimProcess{

	public Generator(Model model, String name, boolean showInTrace) {
		super(model, name, showInTrace);	
	}

	@Override
	public void lifeCycle() throws SuspendExecution {
		//get a reference to the model
		SSQModel model = (SSQModel) getModel();
		//Create a generator process
		model.totalInspectorUsage.update(0);
		model.totalMachineUsage.update(0);
		
		while(true) {
			//hold for next interarrival time
			this.hold(new TimeSpan(model.interarrivalTimes.sample(),TimeUnit.MINUTES));
			Part p = new Part(model,"Next Part",true);
			p.activate();
		}
	}
}