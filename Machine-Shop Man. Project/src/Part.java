import java.util.concurrent.TimeUnit;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;

public class Part extends SimProcess {

	/* Part variables of interest */
	//need to know when part enters system
	TimeInstant systemEntry,systemExit;
	public Part(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);

	}

	@Override
	public void lifeCycle() throws SuspendExecution {
		SSQModel model = (SSQModel) getModel();
		systemEntry = new TimeInstant(model.presentTime().getTimeAsDouble());
		model.numberInSystem.update();
		model.machineQueue.insert(this);

		if(model.machineIsIdle) {
			model.machineQueue.remove(this);
		}
		else {
        	this.passivate();
    }
		//once through queue
		model.machineIsIdle = false;
		model.totalMachineUsage.update(1);

		//sample a new process time
		double processTime = model.processingTimes.sample();
		//hold part for process time
		this.hold(new TimeSpan(processTime, TimeUnit.MINUTES));
		//After part is processed, if the part queue is empty
        if(model.machineQueue.isEmpty()) {
        	//part processor is now idle
        	model.machineIsIdle = true;
        	model.totalMachineUsage.update(0);
        }
        else {
        	// Schedule the part's activation
        	model.machineQueue.removeFirst().activate();
        }

        //part goes to inspection
    	model.inspectionQueue.insert(this);
    	if(model.inspectorIsIdle) {
    		model.inspectionQueue.remove(this);
    	}
    	else {
    		this.passivate();
    	}

    	//once through inspection queue
    	model.inspectorIsIdle = false;
    	model.totalInspectorUsage.update(1);
    	//sample new inspection time
    	double inspectionTime = model.inspectionTimes.sample();
    	//hold part for inspection time
    	this.hold(new TimeSpan(inspectionTime, TimeUnit.MINUTES));
    	//after the part is inspected, if the inspector queue is empty
    	if(model.inspectionQueue.isEmpty()) {
    		//part inspector is now idle
    		model.inspectorIsIdle = true;
        	model.totalInspectorUsage.update(0);

    	}
    	else {
    		// schedule the next part's inspection
    		model.inspectionQueue.removeFirst().activate();
    	}
    	//if part needs refining, re-enter the processingQueue
    	if( model.needsRefining.sample()) {
    		model.machineQueue.insert(this);
    		if(model.machineIsIdle) {
    			model.machineQueue.remove(this);
    		}
    		else {
            	this.passivate();
            }

    		//once through processor queue
    		model.machineIsIdle = false;
        	model.totalMachineUsage.update(1);

    		//sample refiningTime
    		double refineTime = model.refiningTimes.sample();
    		this.hold(new TimeSpan(refineTime, TimeUnit.MINUTES));
    		//If processing queue is empty
            if(model.machineQueue.isEmpty()) {
            	//part processor is now idle
            	model.machineIsIdle = true;
            	model.totalInspectorUsage.update(0);

            }
            else {
            	// Schedule the part's activation
            	model.machineQueue.removeFirst().activate();
            }
    	}
        //Part leaves the system
        model.numberInSystem.update(-1);
        this.systemExit = model.presentTime();
        model.totalResponseTime.update(this.systemExit.getTimeAsDouble() - this.systemEntry.getTimeAsDouble());
	}
}
