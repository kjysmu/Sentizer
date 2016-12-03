package SCHype;

/*
 * SCHype - Java package for spectral clustering in hypergraphs
 * 
 * Copyright (C) 2012 Tom Michoel (The Roslin Institute, University of Edinburgh)
 * 
 */


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class SpectralCluster {


	boolean dir;
	boolean weighted;
	double p;
	double q;
	boolean clusteredges;
	double tolerance;
	int maxstep;
	int minclustsize;
	double minclustscore;

	String hgfile;
	String output;
	String outputfolder;
	String featurefolder;
	String hgfolder;
	
	HashMap<String, String> photomap;
	HashMap<String, String> urlmap;

	boolean isflickr;
	
	boolean isPhotomap;
	boolean isUrlmap;
	
	boolean isFeature;

	
	public SpectralCluster() {
		// TODO Auto-generated method stub

		// mandatory parameters
		dir = false;
		weighted = false;
		p = 1.0;
		q = p;
		clusteredges = true;
		tolerance = 1E-5;
		maxstep = 1000;
		minclustsize = 1;
		minclustscore = 0.0;    

		hgfile = null;
		output = null;
		featurefolder = null;
		outputfolder = null;
		
		hgfolder = null;
		isPhotomap = false;
		isflickr = true;
		isFeature = false;
		isUrlmap = false;

	}
	
	public void setIsflickr(Boolean b){
		isflickr = b;
	}
	public void setIsfeature(Boolean b){
		isFeature = b;
	}
	
	public void setHgfolder(String str){
		hgfolder = str;
	}
	public void setHgfile(String str){
		hgfile = str;
	}
	public void setOutputFolder(String str){
		outputfolder = str;
	}
	public void setOutput(String str){
		output = str;
	}
	public void setFeatureFolder(String str){
		featurefolder = str;
	}
	public void setDir(Boolean b){
		dir = b;
	}
	public void setWeighted(Boolean b){
		weighted = b;
	}
	public void setP(Double d){
		p = d;
	}
	public void setQ(Double d){
		q = d;
	}
	public void setClusteredges(Boolean b){
		clusteredges = b;
	}
	public void setTolerance(Double d){
		tolerance = d;
	}
	public void setMaxstep(int m){
		maxstep = m;
	}
	public void setMinclustsize(int m){
		minclustsize = m;
	}
	public void setMinclustscore(Double d){
		minclustscore = d;
	}

	public void setPhotomap(HashMap<String, String> pmap){
		
		photomap = pmap;
		isPhotomap = true;
	}
	public void setURLmap(HashMap<String, String> umap){
		
		urlmap = umap;
		isUrlmap = true;
	}

	public void printParameters(){
		
		System.out.println("Parameters");
		System.out.println("----------");
		System.out.println("Hypergraph file name:                                   " + hgfile);
		System.out.println("Output file name (clusters - nodes):                    " + output + ".nodes.txt");
		System.out.println("Output file name (clusters - edges):                    " + output + ".edges.txt");
		System.out.println("Directed hypergraph:                                    " + dir);
		System.out.println("Weighted hypergraph:                                    " + weighted);
		if (!dir)
			System.out.println("Edge-to-node scaling parameter:                         " + p);
		else{
			System.out.println("Edge-to-node scaling parameter (source nodes):          " + p);
			System.out.println("Edge-to-node scaling parameter (target nodes):          " + q);
		}
		System.out.println("Cluster edges:                                          " + clusteredges);
		System.out.println("Tolerance (eigenvector calculation):                    " + tolerance);
		System.out.println("Maximum number of iterations (eigenvector calculation): " + maxstep);
		System.out.println("Minimum cluster size:                                   " + minclustsize);
		System.out.println("Minimum cluster score:                                  " + minclustscore);
		System.out.println("----------");
		System.out.println("");
		
	}


	public void doSpectralClustering(){

		HyperGraph hg;
		PFClustering clust;
		String dname;
		
		if(hgfolder != null && hgfile == null){
			String datalist[] = new File(hgfolder).list();
			for(String dlist : datalist){
				if(!dlist.contains("txt")) continue;
				
				System.out.println("Spectral Clustering start : " + dlist);
				System.out.println("dir : " + dir);
				System.out.println("weighted : " + weighted);
				
				dname = dlist.replaceAll(".txt","");
				
				
				hg = new HyperGraph( hgfolder + "/" + dlist, dir, weighted);
				if (dir){
					clust = new PFClustering(hg, p, q);
				}
				else{
					clust = new PFClustering(hg, p);
				}
				
				clust.setIsflickr(isflickr);
				
				
				System.out.println("A");

				clust.recursivePfClustering();
				System.out.println("B");
				
				
				clust.postProcessing();
				System.out.println("C");
				
				if(isPhotomap){
					clust.setPhotoMap(photomap);
				}
				if(isUrlmap){
					clust.setUrlMap(urlmap);
				}
				
				clust.writeClusters( outputfolder + "/" + dname + ".nodes.txt", outputfolder);
				clust.writeClusterEdges( outputfolder + "/" + dname + ".edges.txt", outputfolder);
				/*
				if(isFeature) clust.writeClusterFeatures( featurefolder +"/" + dname + "_feature.txt", featurefolder);
				clust.saveToHtml(outputfolder, dname);
				*/
				
			}
		}else if(hgfolder != null && hgfile != null){
			System.out.println("Spectral Clustering start : " + hgfile);
			dname = hgfile.replaceAll(".txt","");
			hg = new HyperGraph( hgfolder + "/" + hgfile, dir, weighted);
			if (dir){
				clust = new PFClustering(hg, p, q);
			}
			else{
				clust = new PFClustering(hg, p);
			}
			clust.setIsflickr(isflickr);
			clust.recursivePfClustering();
			clust.postProcessing();
			if(isPhotomap){
				clust.setPhotoMap(photomap);
			}
			if(isUrlmap){
				clust.setUrlMap(urlmap);
			}
			clust.writeClusters( outputfolder + "/" + dname + ".nodes.txt", outputfolder);
			clust.writeClusterEdges( outputfolder + "/" + dname + ".edges.txt", outputfolder);
			
			/*
			if(isFeature) clust.writeClusterFeatures( featurefolder +"/" + dname + "_feature.txt", featurefolder);
			clust.saveToHtml(outputfolder, dname);
			*/
		}

		System.out.println("Spectral Clustering complete");

	}

	public static void Die (String msg) {
		System.out.println(msg);
		System.exit(1);
	}



}