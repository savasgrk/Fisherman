import java.util.*;
public class CustomerAssociate implements Runnable	
{
	private static int id=0;
	private Boolean end=false;
	private int myID=0;
	private String name;
	private Manager man;
	private ArrayList<Fisherman> fisherman2= new ArrayList<Fisherman>(); //holds the fisherman objects
	private static List<Fisherman> fisherman_in_line= Collections.synchronizedList(new ArrayList<Fisherman>()); //Queue of fisherman
	Fisherman fm = null; //temp fisherman object

	public CustomerAssociate(Manager m)
	{
		myID=id;
		id++;
		man=m;
		name=("Customer associate" + id);
	}
	public static void set_fisherman_in_line(Fisherman x)
	{
		fisherman_in_line.add(x);
	}
	public static synchronized Fisherman get_fisherman_in_line()
	{
		if(fisherman_in_line.isEmpty()) return null;
		return fisherman_in_line.remove(0); 
		
		
	}
	public synchronized void next_in_line() //gets the next fisherman
	{
		//msg(id + "Using next in line");
			if(!fisherman_in_line.isEmpty()) fm=get_fisherman_in_line();
			if(fm==null) return;			
			msg("Next in line " + fm.get_ID()+ " ");
	}
	public void help_next() //customer associate helps the next fisherman in line which is now stored in fm
	{	
		
		//msg("help next");
		if(fm.get_fish()!=200)
		{
			fm.set_income(fm.get_fish()*.75);
			msg(" fisherman " + fm.get_ID()+ " has an income of " + fm.get_income());
			fm.soldFish(fm.get_income());
		}
		else
		{
			msg(" fisherman " + fm.get_ID() + " has big fish ");
			fm.big_catch();
		}
		
		
	}
	public void end()
	{
		end=true;
	}
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		msg("Entering");
		man.incVar();
		
		while(!end)
		{
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(!fisherman_in_line.isEmpty())
			{
			//	msg("in the if statement");
				
				next_in_line();//gets the next fisherman in line
				if(fm!=null) help_next(); //fm is holding the next finisherman in line
				fm=null;
				try {
					Thread.currentThread().sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		
		}
		msg("Exiting");
	}
	public void msg (String m)
	{
		System.out.println("[" + (System.currentTimeMillis())+ "]" + this.getClass().getName() + ":" +myID + ":" + m );
	}
}
