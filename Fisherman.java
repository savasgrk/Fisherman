import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
public class Fisherman implements Runnable
{
	private String name;
	int dead=0;
	private Boolean breton= false; //store
	private Boolean	marrowind=true; //fishing island 
	private Boolean vacation_cost=false;
	private int vc=Manager.get_vc();
	private Boolean big_catch=false;
	private int[] fish_size= {-1, 0, 10, 20, 30, 40, 50, 200}; //-1 = null and 0=bait
	private int fish=-1;
	//Thread t;
	private double money=0;
	private Boolean needs_help=false;
	
	private Boolean busy=false;
	private Boolean interrupt=false;
	
	private Boolean complete=false;
	private Boolean waiting_for_hole=false;
	private Boolean fishing=false;
	private Boolean boat=false;
	private double income=-1;
	
	private static int id=0;
	private int myID=0;
	private Manager man;
	
	public Fisherman(Manager m)
	{
		
		myID=id;
		id++;
		man=m;
		setName("Fisherman" + id);
	}
	public int get_ID()
	{
		return myID;
	}
	public void set_fishing(Boolean x)
	{
		fishing=x;
	}
	public Boolean get_fishing()
	{
		return fishing;
	}
	public Boolean get_waiting_for_hole()
	{
		return waiting_for_hole;
	}
	public void set_waiting_for_hole(Boolean x)
	{
		waiting_for_hole=x;
	}
	public void setName(String x)
	{
		name=x;
	}
	public String getName()
	{
		return name;
	}
	public int get_fish()
	{
		return fish;
	}
	public void set_fish(int x)
	{
		fish=x;
	}
	public Boolean get_big_catch()
	{
		return big_catch;
	}
	public Boolean get_vacation_cost()
	{
		return vacation_cost;
	}
	public void fishing() //fishing
	{
		msg("fishing");
		Random rand= new Random();
		int x= ThreadLocalRandom.current().nextInt(8);
		fish=fish_size[x];
	}
	public void soldFish(double x) //after selling fish, the new income is added to the fishermans overall money, if vacation cost is reached, vacation_cost becomes true
	{
		income=-1;
		money=money+x;
		fish=-1;
		needs_help=false;
		if(money>=vc) vacation_cost=true;
		msg("sold fish for " + x + " money=" + money);
		breton=false;
		busy=false;
		
	}
	public void set_income(double x)
	{
		income=x;
	}
	public double get_income()
	{
		return income;
	}
	public void big_catch() //big catch becomes true
	{
		msg("mounted a big fish");
		needs_help=false;
		big_catch=true;
		busy=false;
		breton=false;
		
	}
	public void boat_trip()  //sleeping on the boat
	{
		boat=true;
		msg("Sleep on boat trip");
		try {
			Thread.currentThread().sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void waiting_for_fishing_hole()  //fisherman is waiting for the fishing hole.
	{
		msg("Sleep waiting for fishing hole");
		waiting_for_hole=true;
		try {
			Thread.currentThread().sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			msg("Interrupted while sleeping for fishing hole");
		}
		waiting_for_hole=false;
	}
	public void waiting_for_ca() //busy wait
	{
		while(busy)
		{
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public Boolean get_needs_help()
	{
		return needs_help;
	}
	public void set_needs_help(Boolean x)
	{
		needs_help=x;
	}
	public void terminate()
	{
		msg("Fisherman " + id + " terminated");
	}
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		msg("Entering");
		man.incVar();
		fishing=false;
		msg("Arrived at Marrowind");
		while(true)
		{
			while(marrowind) //while no fish has been caught
			{
				waiting_for_fishing_hole();
				fishing=true;
				fishing();
				if(get_fish()==-1) //if fish caught is null, priority set to max
				{
					Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
					msg("Caught a null, Priority set to max");
				}
				if(Thread.currentThread().getPriority()==Thread.MAX_PRIORITY)
				{
					fishing();
					Thread.currentThread().yield();
					Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
					msg("Yielding");
				}
				if(fish==-1) msg("Caught " + "null");
				else if(fish==0) msg("Caught " + "bait");
				else msg("Caught " + fish);
				fishing=false;
				if(get_fish()!=0 && get_fish()!=-1)
				{
					marrowind=false;
				}
			}
			msg("Departing from marrowind to breton");
			boat_trip();
			boat=false;
			breton=true;
			msg("Arrived at Breton");
			if(breton)
			{
				busy=true;
				needs_help=true;
				CustomerAssociate.set_fisherman_in_line(this); //Fisherman is in line for customer associate
				waiting_for_ca(); //busy wait
			}
			msg("Big catch=" + get_big_catch()+ " money= " + money);
			msg("outisde Breton");
			if(!get_vacation_cost() || !get_big_catch()) //if vacation cost isn't reached and big catch isn't caught, will keep running
			{
				boat_trip();
				boat=false;
				marrowind=true;
				msg("Arrived at Marrowind");

			}
			else break;
		}
		if(myID==Manager.get_num_fm()-1) //checks to see if their is a previous fisherman before him if not, skips else statement
		{
			
		}
		else if (Manager.get_fm_thread(Manager.get_Fisherman(myID+1)).isAlive())  // if a previous fisherman is alive, joins the previous fisherman
		{
			msg("Joining Fisherman " + (myID+1));
			try {
				Manager.get_fm_thread(Manager.get_Fisherman(myID+1)).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(myID==0)
		{
			Ranger.end(); //after last fisherman terminates, terminates ranger class
		}
		msg("Exiting");
	}
	public void msg (String m) 
	{
		System.out.println("[" + (System.currentTimeMillis())+ "]" + this.getClass().getName() + ":" +myID + ":" + m );
	}
}
