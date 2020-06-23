//---------------------------------------------------------------------------------------------
//    PATIENT.JAVA BELOW
//---------------------------------------------------------------------------------------------


import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeInstant;

public class Patient extends SimProcess {
	
	/**
	 * Patient Attributes
	 */
	protected double 	arrivalTime;
	protected boolean 	needsRefine = false;
	protected int 		id;
	protected double 	startTime;
	protected double 	endTime;
	protected double 	responseTime;
	
	public Patient(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);	
	}

	@Override
	public void lifeCycle() throws SuspendExecution {
		Clinic model = (Clinic)getModel();
		this.arrivalTime = model.presentTime().getTimeAsDouble();
		model.numInSystem.update();
		model.treatmentQueue.insert(this);
		if(model.treatmentQueue.isEmpty()) {
			if(!model.idleNurseQueue.isEmpty()) {//
				model.treatmentQueue.remove(this);
			} else{
				this.passivate();
			}	
		}
		
	}

}