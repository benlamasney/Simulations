//---------------------------------------------------------------------------------------------
//    TREATMENT.JAVA BELOW
//---------------------------------------------------------------------------------------------


import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;

public class Treatment extends SimProcess {
	public Treatment(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);	
	}

	@Override
	public void lifeCycle() throws SuspendExecution {
		Clinic model = (Clinic)getModel();		
	}

}