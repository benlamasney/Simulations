//---------------------------------------------------------------------------------------------
//    SPECIALIST.JAVA BELOW
//---------------------------------------------------------------------------------------------


import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;

public class Specialist extends SimProcess {
	
	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public Specialist(Model arg0, String arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void lifeCycle() throws SuspendExecution {
		Clinic model = (Clinic)getModel();		
	}

}