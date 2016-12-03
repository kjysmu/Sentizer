package SCHype;

public class SpectralClusterMain {

	public static void main(String args[]){
		
		SpectralCluster sc = new SpectralCluster();
		
		String filepath = "f://w2v//cluster";
		sc.setHgfolder(filepath);
		sc.setIsflickr(false);
		
		sc.setWeighted(true);
		
		sc.setOutputFolder(filepath + "//results");
		sc.doSpectralClustering();
		
	}
	
	
}
