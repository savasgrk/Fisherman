import java.util.*;
public class Ranger implements Runnable
{
	static int id=0;
	private int myID=0;
	String name;
	private static ArrayList<Fisherman> fm= new ArrayList<Fisherman>(); //holds the fisherman objects
	private ArrayList<Fisherman> fisherman= new ArrayList<Fisherman>(); //fisherman waiting for fishinghole
	private Boolean fishing=false;
	private Manager man;
	private Fisherman next_fisher_man=null;
	private int index=-1;
	private static Boolean end=false;
	
	public void set_fm(Fisherman x) 
	{
		fm.add(x);
	}
	public static void end()
	{
		
		//Thread.currentThread().isAlive()
		end=true;
	}
	
	public Ranger(Manager m)
	{
		myID=id;
		id++;
		man=m;
		name=("Ranger" + id);
	}
	public void addFisherman(Fisherman x)
	{
		fisherman.add(x);
	}
	public void pick_fisherman() //picks a random fisherman
	{
		
		Random rand= new Random();
		index= rand.nextInt(fisherman.size())+0;
		next_fisher_man=fisherman.get(index);
		fisherman.remove(index);
		next_fisher_man.set_waiting_for_hole(false);
		next_fisher_man.set_fishing(true);
		
	}
	public Fisherman get_next_fisher_man()
	{
		return next_fisher_man;
	}
	public void set_next_fisher_man(Fisherman i)
	{
		next_fisher_man=i;
	}
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		msg("Entering");
		man.incVar();
		while(!end)
		{
			for(int i=0; i<fm.size(); i++)
			{
				if(fm.get(i).get_waiting_for_hole() && (!fisherman.contains(fm.get(i)))) //if fisherman is waiting for hole
				{																		// gets added to fisherman ArrayList
						fisherman.add(fm.get(i));										//for the ranger to
																						// select the fisherman for fishing
				}
				
			}
			try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fishing=false;
			for(int i=0; i<fisherman.size(); i++) //checks if no fisherman is fishing
			{
				boolean temp=fisherman.get(i).get_fishing();
				if(temp) fishing=temp;
			}
			if(!fisherman.isEmpty() && !fishing)  //if fisherman is waiting for hole and no one is fishing
			{
				
				pick_fisherman(); //ranger selects a fisherman
				msg("Ranger has selected " + next_fisher_man.get_ID());
				Manager.get_fm_thread(next_fisher_man).interrupt();
				index=-1;
				next_fisher_man=null;
				
			}
			if(false) break;
		}
		if(Manager.get_fm_thread(fm.get(0)).isAlive()) //will not exit unless fisherman 0 has finished exiting
		{
			try {
				Manager.get_fm_thread(fm.get(0)).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		msg("Exiting");
		
		
	}
	public void msg (String m) 
	{
		System.out.println("[" + (System.currentTimeMillis())+ "]" + this.getClass().getName() + ":" +myID + ":" + m );
	}


}
