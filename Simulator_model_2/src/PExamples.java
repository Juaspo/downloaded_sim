import desmoj.core.simulator.Model;

import desmoj.core.simulator.*;
import desmoj.core.dist.*;

import java.util.concurrent.TimeUnit;

public class PExamples extends Model {

	
	//Model parameter: the number of van carriers
	protected static int NUM_VC = 1;
	/**
	* Random number stream used to draw an arrival time for the next truck.
	* See init() method for stream parameters.
	*/
	private desmoj.core.dist.ContDistExponential truckArrivalTime;
	/**
	* Random number stream used to draw a service time for a truck.
	* Describes the time needed by the VC to fetch and load the container
	* onto the truck.
	* See init() method for stream parameters.
	*/
	private desmoj.core.dist.ContDistUniform serviceTime;
	/**
	* A waiting queue object is used to represent the parking area for
	* the trucks.
	* Every time a truck arrives it is inserted into this queue (it parks)
	* and will be removed by the VC for service.
	*
	* This way all necessary basic statistics are monitored by the queue.
	*/
	protected desmoj.core.simulator.ProcessQueue<Truck> truckQueue;
	/**
	* A waiting queue object is used to represent the parking spot for
	* the VC.
	* If there is no truck waiting for service the VC will return here
	* and wait for the next truck to come.
	*
	* This way all idle time statistics of the VC are monitored by the queue.
	*/
	protected desmoj.core.simulator.ProcessQueue<VC> idleVCQueue;
	
	/**
	* Returns a sample of the random stream used to determine the
	* time needed to fetch the container for a truck from the
	* storage area and the time the VC needs to load it onto the truck.
	*
	* @return double a serviceTime sample
	*/
	public double getServiceTime() {
		return serviceTime.sample();
	}
	/**
	* Returns a sample of the random stream used to determine
	* the next truck arrival time.
	*
	* @return double a truckArrivalTime sample
	*/
   public double getTruckArrivalTime() {
      return truckArrivalTime.sample();
   }
   
	   
	public PExamples(Model owner, String modelName, boolean showInReport, boolean showInTrace) {
		super(owner, modelName, showInReport, showInTrace);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "This model describes a queueing system located at a "+
        "container terminal. Trucks will arrive and "+
        "require the loading of a container. A van carrier (VC) is "+
        "on duty and will head off to find the required container "+
        "in the storage. It will then load the container onto the "+
        "truck. Afterwards, the truck leaves the terminal. "+
        "In case the VC is busy, the truck waits "+
        "for its turn on the parking-lot. "+
        "If the VC is idle, it waits on its own parking spot for the "+
        "truck to come.";
	}
	
	/**
	* Activates dynamic model components (simulation processes).
    *
    * This method is used to place all events or processes on the
    * internal event list of the simulator which are necessary to start
    * the simulation.
    *
    * In this case, the truck generator and the van carrier(s) have to be
    * created and activated.
    */
	@Override
	public void doInitialSchedules() {
		// TODO Auto-generated method stub
		// create and activate the van carrier(s)
		for (int i=0; i < NUM_VC; i++)
		{
			VC vanCarrier = new VC(this, "Van Carrier", true);
			vanCarrier.activate();
			// Use TimeSpan to activate a process after a span of time relative to actual simulation time,
			// or use TimeInstant to activate the process at an absolute point in time.
		}
	
	   // create and activate the truck generator process
	   TruckGenerator generator = new TruckGenerator(this,"TruckArrival",false);
	   generator.activate();

	}

	
	/**
	* Initialises static model components like distributions and queues.
	*/
	@Override
	public void init() {
	   // Initialize the serviceTimeStream
	   // Parameters:
	   // this                = belongs to this model
	   // "ServiceTimeStream" = the name of the stream
	   // 3.0                 = minimum time in minutes to deliver a container
	   // 7.0                 = maximum time in minutes to deliver a container
	   // true                = show in report?
	   // false               = show in trace?
	   serviceTime= new ContDistUniform(this, "ServiceTimeStream", 3.0, 7.0, true, false);
	   
	   // necessary because an inter-arrival time can not be negative, but
	   // a sample of an exponential distribution can...
	   truckArrivalTime.setNonNegative(true);
	   
	   // initialise the truckQueue
	   // Parameters:
	   // this          = belongs to this model
	   // "Truck Queue" = the name of the Queue
	   // true          = show in report?
	   // true          = show in trace?
	   truckQueue = new ProcessQueue<Truck>(this, "Truck Queue", true, true);
	   
	   // initialise the idleVCQueue
	   // Parameters:
	   // this            = belongs to this model
	   // "idle VC Queue" = the name of the Queue
	   // true            = show in report?
	   // true            = show in trace?
	   idleVCQueue = new ProcessQueue<VC>(this, "idle VC Queue", true, true);
	   
	}
	
	/**
	 * Runs the model.
	 *
	 * @param args is an array of command-line arguments (will be ignored here)
	 */
	public static void main(java.lang.String[] args) {

	   // create model and experiment
	   ProcessesExample model = new ProcessesExample(null, "Simple Process-Oriented Van Carrier Model", true, true);
	   // null as first parameter because it is the main model and has no mastermodel

	   Experiment exp = new Experiment("ProcessExampleExperiment");
	   // ATTENTION, since the name of the experiment is used in the names of the
	   // output files, you have to specify a string that's compatible with the
	   // filename constraints of your computer's operating system.
	   
	   // connect both
	   model.connectToExperiment(exp);
	   // set experiment parameters
	   exp.setShowProgressBar(true);  // display a progress bar (or not)
	   exp.stop(new TimeInstant(1500, TimeUnit.MINUTES));   // set end of simulation at 1500 minutes
	   exp.tracePeriod(new TimeInstant(0), new TimeInstant(100, TimeUnit.MINUTES));
	                                              // set the period of the trace
	   exp.debugPeriod(new TimeInstant(0), new TimeInstant(50, TimeUnit.MINUTES));   // and debug output
	   // ATTENTION!
	   // Don't use too long periods. Otherwise a huge HTML page will
	   // be created which crashes Netscape :-)
	   
	   // start the experiment at simulation time 0.0
	   exp.start();

	   // --> now the simulation is running until it reaches its end criterion
	   // ...
	   // ...
	   // <-- afterwards, the main thread returns here

	   // generate the report (and other output files)
	   exp.report();

	   // stop all threads still alive and close all output files
	   exp.finish();
	}

}
