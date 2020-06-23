/**
 * Model Class Desired Statistical Outputs:
 * 
 * Response Times:
 * 		Min: 13.76525
 * 		Max: 100.1918
 * 		Ave: 42.16339
 * Max Parts in System: 13
 * Machine Queue:
 * 		Ave: 3.97598
 * 		Max: 11
 * Inspector Queue:
 * 		Ave: 0.92949
 * 		Max: 6
 * Utilization:
 * 		Machine:	0.90293
 * 		Inspector:	0.75883
 * 
 */

import java.util.concurrent.TimeUnit;

import desmoj.core.dist.BoolDistBernoulli;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.statistic.Accumulate;
import desmoj.core.statistic.Count;
import desmoj.core.statistic.Tally;

public class SSQModel extends Model {
	
	/* State Variables */
	boolean machineIsIdle;
	boolean inspectorIsIdle;
	
	
	/** Model Structures */
    protected ProcessQueue<Part> machineQueue;
    protected ProcessQueue<Part> inspectionQueue;

    /* Distributions and associated values */
	protected ContDistExponential interarrivalTimes;
	protected ContDistExponential processingTimes;
	protected ContDistNormal      inspectionTimes;
	protected ContDistExponential refiningTimes;
	protected BoolDistBernoulli   needsRefining;
	protected double interarrivalMean	= 7.0;
	protected double processingMean		= 4.5;
	protected double inspectionMean		= 5.0;
	protected double inspectionSD		= 1.0;
	protected double refiningMean		= 3.0;
	protected double refiningProb		= 0.25;
	
	/* Statistical Trackers needed: */
	protected Count numberInSystem;
	protected Tally totalResponseTime;
	protected Accumulate totalMachineUsage, totalInspectorUsage;
	protected double machineUsagePerc, inspectorUsagePerc;

	
    public SSQModel(Model owner, String modelName,
                    boolean showInReport, boolean showInTrace) {
        super(owner, modelName, showInReport, showInTrace);
    }

	@Override
	public String description() {
	      return "This model describes a machine-part-refinery shop's queueing system";
	}

	@Override
	public void doInitialSchedules() {
	    //Create the first part process
        Generator generator = new Generator(this, "Generator Process", true);
        generator.activate();
	}

	@Override
	public void init() {
	    //State variables and structures
        machineIsIdle 	= true;
        inspectorIsIdle 	= true;
        
        machineQueue 		= new ProcessQueue<>(this, "Machine Queue", true, false);
        inspectionQueue 	= new ProcessQueue<>(this, "Inspecting Queue", true, false);

        //Initialize the statistical trackers
        numberInSystem 		= new Count(this, "Number in System", true, false);
        totalResponseTime	= new Tally(this, "Total Response Time", true, true);
        totalMachineUsage	= new Accumulate(this,"Machine Usage",true,true);
        totalInspectorUsage	= new Accumulate(this,"Inspector Usage",true,true);


        // Initialize the random number streams
        interarrivalTimes 	= new ContDistExponential(this, "Interarrival Time", interarrivalMean, true, true);
        interarrivalTimes.setNonNegative(true);
        processingTimes 	= new ContDistExponential(this, "Processing Time", processingMean, true, true);
        inspectionTimes 	= new ContDistNormal(this, "Inspection Time", inspectionMean, inspectionSD, true, true);
        refiningTimes 		= new ContDistExponential(this, "Refining Time", refiningMean, true, true);
        needsRefining		= new BoolDistBernoulli(this, "Needs Refining", refiningProb, true, true);
	}
	
	public static void main(String[] args) {
		 // Set reference units to be in minutes
        Experiment.setReferenceUnit(TimeUnit.MINUTES);
        
        // Create model and experiment
        SSQModel model = new SSQModel(null,
                "Single Server Queue with DESMO-J Processes (v1)",
                true, true);
        Experiment exp = new Experiment("SSQExperiment");

        // connect both
        model.connectToExperiment(exp);
        
        // Set experiment parameters
        exp.setShowProgressBar(false);
        exp.stop(new TimeInstant(8, TimeUnit.HOURS));
        // Set the period of the trace and debug
        exp.tracePeriod(new TimeInstant(0, TimeUnit.MINUTES),
                        new TimeInstant(60, TimeUnit.MINUTES));
        exp.debugPeriod(new TimeInstant(0, TimeUnit.MINUTES),
                        new TimeInstant(60, TimeUnit.MINUTES));
        
        exp.start();

        exp.report();

        exp.finish(); 
	}

}

