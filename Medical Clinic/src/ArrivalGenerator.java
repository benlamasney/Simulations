//---------------------------------------------------------------------------------------------
//    ARRIVALGENERATOR.JAVA BELOW
//---------------------------------------------------------------------------------------------

import java.util.concurrent.TimeUnit;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;

public class ArrivalGenerator extends SimProcess {
	int maxWaitingRoomLength = 8;
	public ArrivalGenerator(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);	
	}

	@Override
	public void lifeCycle() throws SuspendExecution {
		Clinic model = (Clinic) getModel();
		while(model.presentTime().compareTo(new TimeInstant(12, TimeUnit.HOURS)) < 0) {
			double ta = model.interarrivalTimes.sample();
			this.hold(new TimeSpan(ta));
			Patient nextPatient = new Patient(model, "Patient", true);
			nextPatient.activate(new TimeSpan(model.presentTime().getTimeAsDouble()));
			if(model.treatmentQueue.size() < maxWaitingRoomLength) {
				model.treatmentQueue.insert(nextPatient);
			}else {
				model.balks.update();
			}
		}
		
	}

}
