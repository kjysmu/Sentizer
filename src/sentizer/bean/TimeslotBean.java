package sentizer.bean;

public class TimeslotBean {
	private int stime;
	private int etime;
	private int interval;

	public void setStime(int t){
		stime = t;
	}
	public void setEtime(int t){
		etime = t;
	}
	public void setInterval(int t){
		interval = t;
	}
	public int getStime(){
		return stime;
	}
	public int getEtime(){
		return etime;
	}
	public int getInterval(){
		return interval;
	}

}
