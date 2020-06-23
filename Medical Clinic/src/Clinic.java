//---------------------------------------------------------------------------------------------
//    CLINIC.JAVA BELOW
//---------------------------------------------------------------------------------------------


import java.util.concurrent.TimeUnit;

import desmoj.core.dist.BoolDistBernoulli;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.*;
import desmoj.core.statistic.*;

public class Clinic extends Model{
	
	protected Queue<Patient> treatmentQueue;
	protected Queue<Treatment> idleNurseQueue;
	protected Queue<Patient> specialistQueue;
	
	protected double dailyOperatingCost;
	protected double avgInWaiting;
	
	protected ContDistExponential interarrivalTimes;
	protected ContDistExponential treatmentTimes;
	protected ContDistExponential specialistTimes;
	protected BoolDistBernoulli   needsDiversion;
	
	protected Count arrivals;
	protected Count balks;
	protected Count diversions;
	protected Count numTreated;
	protected Count numInSystem;
	
	ArrivalGenerator generator;
	Treatment 		 nurse;
	Specialist 		 specialist;
  
	//Clinic Staffing Logistics//
	protected int numNurses 	= 1;
	protected int numSpecialist = 1;
	protected int numExamRooms 	= 1;
		
	//Operating Expenses//
	protected int dayNurseSalary 		= 1200;
	protected int nursePatientcost 		= 100;
	protected int daySpecialistSalary 	= 1500;
	protected int specialistPatientCost = 200;
	protected int dayExamRoomCost		= 300;
	protected int emergencyCareCost		= 500;
	//USE THESE VALUES FOR CODE//
	protected int totalDayNurseCost		 = numNurses     * dayNurseSalary;
	protected int totalDaySpecialistCost = numSpecialist * daySpecialistSalary;
	protected int totalExamRoomCost		 = numExamRooms  * dayExamRoomCost;
		
	

	public Clinic( Model owner, String modelName, boolean showInReport, boolean showInTrace) {
        super(owner, modelName, showInReport, showInTrace);

	}

	@Override
	public String description() {
    String description = "Modeling of a health clinic, with the intent to calculate and optimize operating costs and profits.";
		// TODO Auto-generated method stub
		return description;
	}

	@Override
	public void doInitialSchedules() {
		//activate generator
		generator.activate();
		//activate specialist
		nurse.activate();
		//activate treatment
		specialist.activate();
		
	}

	@Override
	public void init() {
		
		treatmentQueue  = new Queue<Patient>(this, "treatmentQueue", true, false);
		specialistQueue = new Queue<Patient>(this, "specialistQueue", true, false);
		idleNurseQueue		= new Queue<Treatment>(this,"nurseQueue",true,false);
		for(int i = 0; i < this.numNurses; i++){
			idleNurseQueue.insert(new Treatment(this, "Processing", true));
		}
		dailyOperatingCost = 0.0;
		arrivals   = new Count(this, "arrivals", true, false);
		balks      = new Count(this, "Balks", true, false);
		diversions = new Count(this, "Diversions", true, false);
		numTreated = new Count(this, "Number Treated", true, false);
		avgInWaiting = 0.0;
		//will need to reinitialize the interarrival times in the arrival generator when 
		//the time changes to the next bracket: (8-10am)->(10am-4 pm)->(4-8pm)
		//						Simulation Time:(0-2hrs)->(2 -  8hrs)->(8-12hrs)
		interarrivalTimes = new ContDistExponential(this, "interarrival time 8am - 10am", 15, true, false);
		treatmentTimes    = new ContDistExponential(this, "treatment time", 8, true, false);
		specialistTimes   = new ContDistExponential(this, "specialist time", 25, true, false);
		needsDiversion    = new BoolDistBernoulli(this, "needs Diversion", .4, true, false);
		
		generator  = new ArrivalGenerator(this, "generator", true);
		specialist = new Specialist(this, "inspection", true);
	}
	
	public static void main(String[] args) {
		 // Set reference units to be in minutes
        Experiment.setReferenceUnit(TimeUnit.MINUTES);
        
        // Create model and experiment
        Clinic model = new Clinic(null, "Health Clinic Model", true, true);
        Experiment exp = new Experiment("Clinic Experiment");
        
        //////////////exp.setSeedGenerator(/**We need to figure out what value goes here before using**/);
        // connect both
        model.connectToExperiment(exp);
        
        // Set experiment parameters
        exp.setShowProgressBar(false);
        exp.stop(new TimeInstant(12, TimeUnit.HOURS));
        // Set the period of the trace and debug
        exp.tracePeriod(new TimeInstant(0, TimeUnit.MINUTES),
                        new TimeInstant(60, TimeUnit.MINUTES));
        exp.debugPeriod(new TimeInstant(0, TimeUnit.MINUTES),
                        new TimeInstant(60, TimeUnit.MINUTES));
        
        exp.start();

        exp.report();

        exp.finish(); 

	}
	
	public ContDistExponential getInterarrivalTime(Model model) {
		int currentTime = (int)model.presentTime().getTimeAsDouble(TimeUnit.HOURS);
		if (currentTime < 2){//8-10
			return new ContDistExponential(this, "interarrival time 8am - 10am", 15, true, false);
		}
		else if(currentTime < 8){
			return new ContDistExponential(this, "interarrival time 10am - 4pm", 6, true, true);
		}
		else{
			return new ContDistExponential(this, "interarrival time 10am - 4pm", 9, true, true);
		}	
	}
}
