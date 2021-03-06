package service;


import model.Order;
import dao.DataBase;
import model.Type;
import model.Suborder;
import model.Trailer;
import model.LoadingDock;
import model.Status;
import model.Load;

import java.util.ArrayList;
import java.util.Date;



public class Service {

	private static Service instance;
	
	
	private Service()
	{
		
	}
	
	/**
	 * @author Momo
	 *Gets the instance of a single Service object
	 */
	public static Service getInstance()
	{
		if (instance == null) {
			instance = new Service();

		}
		
		return instance;
	}
	
	

/**
 * Creates new order 
 */
	
	/**
	 * @param orderId
	 * @param totalWeight
	 * @param margin
	 * @param lType
	 * @return
	 */
	public Order createOrder(int orderId, int totalWeight, int margin, model.Type lType)
	{
		Order order=new Order(orderId,totalWeight,margin,lType);
		DataBase.getInstance().addOrder(order);
		return order;
	}
	 

	public void removeOrder(Order order)
	{
		DataBase.getInstance().removeOrder(order);
	}
	
	public  Trailer createTrailer(String truckID, String company,String driver,String driverPhNum,Type lType)
	{
		Trailer trailer=new Trailer(truckID, company, driver, driverPhNum, lType);
		DataBase.getInstance().addTrailer(trailer);
		return trailer;
	}
	
	public void removeTrailer(Trailer trailer)
	{
		DataBase.getInstance().removeTrailer(trailer);
	}
	
	public LoadingDock createLoadingDock(int dockID,Type lType,Status lStatus)
	{
		LoadingDock loadingDock=new LoadingDock(dockID, lType, lStatus);
		DataBase.getInstance().addLoadingDock(loadingDock);
		return loadingDock;
	}
	public void removeLoadingDock(LoadingDock loadingDock)
	{
		DataBase.getInstance().removeLoadingDock(loadingDock);
	}
	
	/**
	 * @author Momo
	 *Creates a load which is connected to a suborder and is attached to a loading dock
	 */
	/**
	 * @param estStartTime
	 * @param estEndTime
	 * @param suborder
	 * @param loadingDock
	 * @return
	 */
	public Load createLoad(Date estStartTime, Date estEndTime,Suborder suborder)
	{
		Load load=new Load(estStartTime, estEndTime);
		suborder.setlLoad(load);
		load.setlSuborder(suborder);
		return load;
	}
	
	public void beginLoad(Load load)
	{
		load.setActualBegTime(DU.createDate());
	}

	
    public void completeLoad(Suborder suborder)
    {
    	
    	
    	if(suborder.getlLoad()!=null)
    	suborder.getlLoad().setCompleted(true);
    	suborder.getlLoad().setActtualEndTime(DU.createDate());
    	
    }
        
	/**
	 * @author The smoking ace's
	 *Creates a suborder belonging to order and that is attached to a Trailer 
	 */
	/**
	 * @param loadingTime
	 * @param weight
	 * @param loadingDate
	 * @param order
	 * @param lTrailer
	 * @return
	 */
	public Suborder createSuborder(int loadingTime,int weight, Date loadingDate,Order order,Trailer lTrailer)
	{
		Suborder suborder=new Suborder(loadingTime, weight, loadingDate);
		suborder.setlTrailer(lTrailer);
		lTrailer.addlSuborders(suborder);
		order.addSuborder(suborder);
		return suborder;
	}
	
	public void removeSuborder(Order order, Suborder suborder)
	{
		order.getlSuborder().remove(suborder);
	}
	
	public void registerIn(String trailerID, int weightIn,int restTime)
	{
	 ArrayList<Trailer> trailers=DataBase.getInstance().getAllTrailers();
	
	 Trailer foundT = null;
	 for(Trailer t : trailers){
		if(t.getTruckID().compareTo(trailerID) == 0){
			foundT = t;
		}
	foundT.setRestTime(restTime);
	foundT.setWeighIn(weightIn);
	foundT.setArrivalTime(DU.createDate());
	ArrayList<LoadingDock> loadingDocks=DataBase.getInstance().getAllLoadingDocks();
	
	
	
	for(Suborder s : foundT.getlSuborders()){
		
		Date plannedDate = DU.createDate();
		LoadingDock appropriateDock = null;
		for (LoadingDock lD : loadingDocks) {
			if(findLastLoad(lD).compareTo(plannedDate)>0)
				{plannedDate= findLastLoad(lD);
				appropriateDock=lD;
				}
			else{
				appropriateDock=loadingDocks.get(0);
			}
		}
		
		Load load=createLoad(DU.createDatePlusMinuts(plannedDate, 5), DU.createDatePlusMinuts(plannedDate, 5+s.getLoadingTime()), s);
		s.setlLoad(load);
		beginLoad(load);
		loadToDock(load, appropriateDock);

		
		System.out.println(s.getlLoad().getEstStartTime());
	}
	}
	 
	}
	
	public void loadToDock(Load load,LoadingDock loadingDock)
	{
	
			if(load.getlSuborder().getlTrailer().getlType()==loadingDock.getlType() && loadingDock.getlStatus()==Status.OPEN)
			{System.out.println(load);
				loadingDock.addLoad(load);
			}
			loadingDock.setlStatus(Status.OCCUPIED);
	}
	
	public Date findLastLoad(LoadingDock loadingDock)
	{Date lastLoad = DU.createDate();
		for( Load l:loadingDock.getlLoad())
		{
			if(l.getEstEndTime().compareTo(lastLoad)>0)
				lastLoad=l.getEstEndTime();
		}
		return lastLoad;
	}
	
	}
