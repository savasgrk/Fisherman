import java.util.*;
public class Manager implements Runnable
{
	private int var = 0;
	private static long  time = System.currentTimeMillis();
	Thread t;
	private Boolean end=false;
	private static int vacation_cost;
	private static int ca;
	private static int num_fm;
	private static ArrayList<Fisherman> fisherman;
	private static ArrayList<Thread> thread_fm;
	private static ArrayList<CustomerAssociate> costumerA;
	private static ArrayList<Thread> thread_ca;
	private static Ranger ranger;
	private Thread thread_r;

	public Manager()
	{
		t= new Thread(this, ("Manager Thread"));
		t.start();
	}
	

	public synchronized void incVar() 
	{
		var++;	
	}
	public static Ranger get_ranger()
	{
		return ranger;
	}
	public static int get_num_fm()
	{
		return num_fm;
	}
	public static Fisherman get_Fisherman(int x) //return fisherman is index x
	{
		return fisherman.get(x);
	}

	public static int get_vc()
	{
		return vacation_cost;
	}
	public static Thread get_fm_thread(Fisherman x)
	{
		return thread_fm.get(x.get_ID());
	}
	
	public long getTime() 
	{
		return time;
	}
	@Override
	public void run() 
	{	
		
			// TODO Auto-generated method stub	
			msg("Entering");
			time = System.currentTimeMillis();
			fisherman=new ArrayList<Fisherman>();
			thread_fm= new ArrayList<Thread>();
			costumerA=new ArrayList<CustomerAssociate>();
			thread_ca= new ArrayList<Thread>();
			for (int i = 0; i < num_fm; i++) //sets up the fisherman arraylist and starts the fisherman threads
			{
				fisherman.add(new Fisherman(this));
				thread_fm.add(new Thread(fisherman.get(i), "Fisherman " + i));
				msg(thread_fm.get(i).getName());
				thread_fm.get(i).start();
			}
			for(int i=0; i<ca; i++)// sets up customer associate arraylist, starts the threads for customer associate
			{
				//costumerA.add(new CustomerAssociate(this));
				costumerA.add(new CustomerAssociate(this));
				thread_ca.add(new Thread(costumerA.get(i), "Costumer Associate " + i));
				thread_ca.get(i).start();
			}
			
			ranger= new Ranger(this);
			thread_r= new Thread(ranger, "Ranger " + 0);
			thread_r.start();
			for (int i = 0; i < num_fm; i++) //passes the fisherman objects into the ranger class for easy access
			{
				ranger.set_fm(fisherman.get(i));
	
			}
			while(!end)
			{
				Boolean fisher=false;
				for(int i=0; i<num_fm; i++)
				{
					if(thread_fm.get(i).isAlive()) fisher=true;
				}
				if(!fisher)
				{
					Boolean alive=false;
					for(int i=0; i<ca; i++)
					{
						costumerA.get(i).end();
					}
					for(int i=0; i<ca; i++)
					{
						if(thread_ca.get(i).isAlive()) alive=true;
					}
					if(thread_r.isAlive()) alive=true;
					if(alive==false)
					{
						end=true;
					}
				}
			}
			msg("Exiting");
		
	}
	public void msg (String m) 
	{
		System.out.println("[" + (System.currentTimeMillis())+ "]" + this.getClass().getName() +  ":" + m );
	}
	public static void main(String[] args) 
	{

		int vc=250;
		int ca1=2;
		int fm=6;
		System.out.println("Enter the number of fisherman, customer assoicates and vacation cost, respectivily");
		if(args.length>0) fm=Integer.parseInt(args[0]);
		if(args.length>1) ca1=Integer.parseInt(args[1]);
		if(args.length>2) vc=vacation_cost=Integer.parseInt(args[2]);
		Manager.num_fm=fm;
		Manager.ca=ca1;
		Manager.vacation_cost=vc;
		System.out.println("Fisherman: " + fm + " CostumerAssociates " + ca + " Vacation Cost "+ vc);
		Manager manager  = new Manager();
		
	}
}
