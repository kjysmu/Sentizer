/*
 * SCHype - Java package for spectral clustering in hypergraphs
 * 
 * Copyright (C) 2012 Tom Michoel (The Roslin Institute, University of Edinburgh)
 * 
 */

package SCHype;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.math.*;


import javax.imageio.ImageIO;

import org.xml.sax.SAXException;
/*
import photominer.ExpConst;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.tags.Tag;
import com.aetrion.flickr.tags.TagsInterface;

import net.semanticmetadata.lire.imageanalysis.CEDD;

*/


import java.net.*;

//import net.semanticmetadata.lire.ImageSearchHits;

import cern.colt.list.DoubleArrayList;


public class PFClustering {

	public HyperGraph hyperGraph;

	public ArrayList<Cluster> clusters; 

	public double p; // cluster scaling parameter

	public double q; // cluster scaling parameter only used for directed hypergraphs

	public double tolerance; // convergence tolerance

	public int maxstep; // max number of iterations in perronvector calculations

	public int minClustSize; // minimal number of nodes in a cluster

	public double minClustScore; // minimal score of a cluster

	public boolean clusterEdges; // cluster edges or nodes?

	public boolean isflickr;
	public boolean isPhotomap;
	public boolean isUrlmap;
	
	//Flickr flickr;

	
	
	public HashMap<String,String> photomap;
	
	public HashMap<String,String> urlmap;
	
	public PFClustering (){
		//flickr = new Flickr(ExpConst.APIKEY);
		this.isflickr = false;
		this.isPhotomap = false;
		this.isUrlmap = false;

	}

	public PFClustering (HyperGraph hg, double p){
		
		//flickr = new Flickr(ExpConst.APIKEY);
		this.hyperGraph = hg;
		this.p = p;
		this.q = p;
		// set default parameters
		this.tolerance = 1E-5;
		this.maxstep = 1000;
		this.minClustSize = 1;
		this.minClustScore = 0.0;
		this.clusterEdges = true;
		this.isflickr = false;
		this.isPhotomap = false;
		this.isUrlmap = false;
	}

	public PFClustering (HyperGraph hg, double p, double q){
		//flickr = new Flickr(ExpConst.APIKEY);
		this.hyperGraph = hg;
		this.p = p;
		this.q = q;
		// set default parameters
		this.tolerance = 1E-5;
		this.maxstep = 1000;
		this.minClustSize = 1;
		this.minClustScore = 0.0;
		this.clusterEdges = true;
		this.isflickr = false;
		this.isPhotomap = false;
		this.isUrlmap = false;
	}

	public PFClustering (HyperGraph hg, double p, boolean clusterEdges){
		//flickr = new Flickr(ExpConst.APIKEY);
		this.hyperGraph = hg;
		this.p = p;
		this.q = p;
		this.clusterEdges = clusterEdges;
		// set default parameters
		this.tolerance = 1E-5;
		this.maxstep = 1000;
		this.minClustSize = 1;
		this.minClustScore = 0.0;
		this.isflickr = false;
		this.isPhotomap = false;
		this.isUrlmap = false;
	}

	public PFClustering (HyperGraph hg, double p, double q, boolean clusterEdges){
		//flickr = new Flickr(ExpConst.APIKEY);
		this.hyperGraph = hg;
		this.p = p;
		this.q = q;
		this.clusterEdges = clusterEdges;
		// set default parameters
		this.tolerance = 1E-5;
		this.maxstep = 1000;
		this.minClustSize = 1;
		this.minClustScore = 0.0;
		this.isflickr = false;
		this.isPhotomap = false;
		this.isUrlmap = false;
	}

	public PFClustering (HyperGraph hg, double p, double tolerance, int maxstep, int minClustSize, double minClustScore, boolean clusterEdges){
		//flickr = new Flickr(ExpConst.APIKEY);
		this.hyperGraph = hg;
		this.p = p;
		this.q = p;
		this.tolerance = tolerance;
		this.maxstep = maxstep;
		this.minClustSize = minClustSize;
		this.minClustScore = minClustScore;
		this.clusterEdges = clusterEdges;
		this.isflickr = false;
		this.isPhotomap = false;
		this.isUrlmap = false;
	}

	public PFClustering (HyperGraph hg, double p, double q, double tolerance, int maxstep, int minClustSize, double minClustScore, boolean clusterEdges){
		//flickr = new Flickr(ExpConst.APIKEY);
		this.hyperGraph = hg;
		this.p = p;
		this.q = q;
		this.tolerance = tolerance;
		this.maxstep = maxstep;
		this.minClustSize = minClustSize;
		this.minClustScore = minClustScore;
		this.clusterEdges = clusterEdges;
		this.isflickr = false;
		this.isPhotomap = false;
		this.isUrlmap = false;
	}

	/**
	 * Perform recursive Perron-Frobenius based hyperedge clustering
	 */
	public void setIsflickr(boolean b){
		
		isflickr = b;
		
	}
	public void setPhotoMap(HashMap<String,String> pmap){
		
		photomap = pmap;
		isPhotomap = true;
		
	}
	public void setUrlMap(HashMap<String,String> umap){
		
		urlmap = umap;
		isUrlmap = true;
		
	}
	
	public void recursivePfClustering(){
		// first round of clustering
		this.pfClustering();
		// cluster each cluster
		if (this.clusters.size()>1){
			PFClustering clust;
			ArrayList<Cluster> newClusters = new ArrayList<Cluster>();
			for (Cluster cluster : this.clusters){
				clust = new PFClustering(cluster.toHyperGraph(), this.p);
				clust.recursivePfClustering();
				// add new clusters
				for (Cluster cl : clust.clusters)
					newClusters.add(cl);                    
			}
			this.clusters = newClusters;
		}
	}


	public void postProcessing(){
		//System.out.println("Postprocessing ... ");
		this.selectClustersSize();
		this.selectClustersScore();
		Comparator scoreComp = new ScoreComparator();
		Collections.sort(this.clusters, scoreComp);
		for (Cluster clust : this.clusters){
			//System.out.println("... Cluster with score " + clust.score + " (max = " + clust.scoreBound + ") and " + clust.vertices.size() + " nodes.");
		}
		//System.out.println("Found " + this.clusters.size() + " clusters with at least " + this.minClustSize + " nodes and score higher than " + this.minClustScore + ".");
		//System.out.println("Done.");
	}

	/**
	 * Perform Perron-Frobenius based hyperedge clustering
	 * @param p
	 * @param node
	 */
	public void pfClustering() {
		this.clusters = new ArrayList<Cluster>();
		if (!this.hyperGraph.directed){
			HashMap<String,Double> v;
			// make copy of hypergraph
			HyperGraph hgCopy = new HyperGraph(this.hyperGraph.edges, this.hyperGraph.directed, this.hyperGraph.weighted);
			// clustering loop
			//System.out.println("Start Undirected Hypergraph Spectral Clustering algorithm ...");
			while (!hgCopy.edges.isEmpty()){
				hgCopy.setPerronVector(this.p,this.tolerance, this.maxstep);
				v = hgCopy.perronVector;
				Cluster cluster = bestCluster(hgCopy, v, this.tolerance);
				cluster.scoreBound = hgCopy.vectorScore(v, this.p);
				cluster.perronVector = v;
				clusters.add(cluster);
				if (this.clusterEdges)
					hgCopy.removeEdges(cluster.vertices);
				else
					hgCopy.removeNodes(cluster.vertices);
				//System.out.println("... remaining hypergraph has " + hgCopy.vertices.size() + " nodes and "+ hgCopy.edges.size() + " edges.");
			}
		} else {
			HashMap<String,Double> v, w;
			// make copy of hypergraph
			HyperGraph hgCopy = new HyperGraph(this.hyperGraph.edges, this.hyperGraph.directed, this.hyperGraph.weighted);
			// clustering loop
			//System.out.println("Start Directed Hypergraph Spectral Clustering algorithm ...");
			while (!hgCopy.edges.isEmpty()){
				hgCopy.setPerronVectorXY(this.p,this.q, this.tolerance, this.maxstep);
				v = hgCopy.perronVectorX;
				w = hgCopy.perronVectorY;
				//System.out.println("computing best score");
				Cluster cluster = bestClusterApprox(hgCopy,v, w, this.tolerance);
				//System.out.println("computing vector score");
				cluster.scoreBound = hgCopy.vectorScore(v, w, this.p, this.q);
				cluster.perronVectorX = v;
				cluster.perronVectorY = w;
				clusters.add(cluster);
				//System.out.println("removing edges");
				hgCopy.removeEdges(cluster.sourceVertices,cluster.targetVertices);
				//System.out.println("... remaining hypergraph has " + hgCopy.vertices.size() + " nodes and "+ hgCopy.edges.size() + " edges.");
			}
		}
		// post-processing
		this.postProcessing();
	}

	/**
	 * Get highest scoring cluster based on Perron vector cutoffs
	 * @param hg
	 * @param v
	 * @param tol
	 * @return
	 */
	public Cluster bestCluster (HyperGraph hg, HashMap<String,Double> v, double tol){
		// dirty way to get list of unique values above tolerance level
		HashSet<Double> val1 = new HashSet<Double>();
		for (String node : v.keySet())
			if (v.get(node)>tolerance)
				val1.add(v.get(node));  
		DoubleArrayList vals = new DoubleArrayList();
		for (double vv : val1)
			vals.add(vv);
		// sort in descending order
		vals.quickSort();
		vals.reverse();
		// compute scores
		HashSet<String> elements = new HashSet<String>();
		HashMap<String,Double> vSet = new HashMap<String,Double>();
		for (String node : v.keySet())
			vSet.put(node, 0.0);
		double maxscore=0.0, maxval=0.0, newscore;
		for (int k=0; k<vals.size(); k++){
			for (String node : v.keySet()){
				if (v.get(node)>=vals.get(k))
					vSet.put(node, 1.0);
			}
			newscore = hg.vectorScore(vSet, this.p);
			if (newscore >= maxscore){
				maxscore = newscore;
				maxval = vals.get(k);
			}
		}
		// set final cluster
		elements = new HashSet<String>();
		for (String node : v.keySet()){
			if (v.get(node)>=maxval)
				elements.add(node);
		}
		Cluster cluster = new Cluster(this.hyperGraph, elements, this.p);
		return cluster;
	}

	//      public Cluster bestCluster (HashMap<String,Double> v, double tol){
	//              // dirty way to get list of unique values above tolerance level
	//              HashSet<Double> val1 = new HashSet<Double>();
	//              for (String node : v.keySet())
	//                      if (v.get(node)>tolerance)
	//                              val1.add(v.get(node));  
	//              DoubleArrayList vals = new DoubleArrayList();
	//              for (double vv : val1)
	//                      vals.add(vv);
	//              // sort in descending order
	//              vals.quickSort();
	//              vals.reverse();
	//              // compute scores
	//              HashSet<String> elements = new HashSet<String>();
	//              Cluster cluster = new Cluster(this.hyperGraph, elements);
	//              double maxscore=0.0, maxval=0.0, newscore;
	//              for (int k=0; k<vals.size(); k++){
	//                      for (String node : v.keySet()){
	//                              if (v.get(node)>=vals.get(k))
	//                                      cluster.vertices.add(node);
	//                      }
	//                      cluster.setEdges();
	//                      //newscore = cluster.clusterScore(this.p);
	//                      newscore = cluster.clusterScore(this.p);
	//                      if (newscore >= maxscore){
	//                              maxscore = newscore;
	//                              maxval = vals.get(k);
	//                      }
	//              }
	//              // set final cluster
	//              elements = new HashSet<String>();
	//              for (String node : v.keySet()){
	//                      if (v.get(node)>=maxval)
	//                              elements.add(node);
	//              }
	//              cluster = new Cluster(this.hyperGraph, elements, this.p);
	//              return cluster;
	//      }

	//      public Cluster bestCluster (HashMap<String,Double> v, HashMap<String,Double> w, double tol){
	//              // dirty way to get list of unique values above tolerance level
	//              HashSet<Double> val1 = new HashSet<Double>();
	//              for (String node : v.keySet())
	//                      if (v.get(node)>tolerance)
	//                              val1.add(v.get(node));  
	//              DoubleArrayList vVals = new DoubleArrayList();
	//              for (double vv : val1){
	//                      vVals.add(vv);
	//              }
	//              // same for w
	//              HashSet<Double> val2 = new HashSet<Double>();
	//              for (String node : w.keySet())
	//                      if (w.get(node)>tolerance)
	//                              val2.add(w.get(node));  
	//              DoubleArrayList wVals = new DoubleArrayList();
	//              for (double vv : val2)
	//                      wVals.add(vv);
	//              // sort in descending order
	//              vVals.quickSort();
	//              vVals.reverse();
	//              wVals.quickSort();
	//              wVals.reverse();
	//              
	//              // compute scores
	//              HashSet<String> sourceElements = new HashSet<String>();
	//              HashSet<String> targetElements = new HashSet<String>();
	//              Cluster cluster = new Cluster(this.hyperGraph, sourceElements, targetElements);
	//              double  maxscore=1.0, maxvalX=0.0, maxvalY=0.0, newscore;
	//                      
	//              for (int k=0; k<vVals.size(); k++){
	//                      // set source nodes
	//                      for (String node1 : v.keySet()){
	//                              if (v.get(node1)>=vVals.get(k))
	//                                      cluster.sourceVertices.add(node1);
	//                      }
	//                      cluster.targetVertices = new HashSet<String>();
	//                      for (int l=0; l<wVals.size(); l++){
	//                              // set target nodes
	//                              for (String node2 : w.keySet()){
	//                                      if (w.get(node2)>=wVals.get(l))
	//                                              cluster.targetVertices.add(node2);
	//                              }
	//                              cluster.setEdges();
	//                              newscore = cluster.clusterScore(this.p,this.q);
	//                              if (newscore >= maxscore){
	//                                      maxscore = newscore;
	//                                      maxvalX = vVals.get(k);
	//                                      maxvalY = wVals.get(l);
	//                              }
	//                      }
	//              }
	//              // set final cluster
	//              sourceElements = new HashSet<String>();
	//              targetElements = new HashSet<String>();
	//              for (String node : v.keySet()){
	//                      if (v.get(node)>=maxvalX)
	//                              sourceElements.add(node);
	//              }
	//              for (String node : w.keySet()){
	//                      if (w.get(node)>=maxvalY)
	//                              targetElements.add(node);
	//              }
	//              cluster = new Cluster(this.hyperGraph, sourceElements, targetElements, this.p);
	//              return cluster;
	//      }

	/**
	 * Get highest scoring cluster based on Perron vector cutoffs, for directed hypergraphs, exhaustive search over all cutoff pairs
	 * @param hg
	 * @param v
	 * @param w
	 * @param tol
	 * @return
	 */
	public Cluster bestCluster (HyperGraph hg, HashMap<String,Double> v, HashMap<String,Double> w, double tol){
		// dirty way to get list of unique values above tolerance level
		HashSet<Double> val1 = new HashSet<Double>();
		for (String node : v.keySet())
			if (v.get(node)>tolerance)
				val1.add(v.get(node));  
		DoubleArrayList vVals = new DoubleArrayList();
		for (double vv : val1){
			vVals.add(vv);
		}
		// same for w
		HashSet<Double> val2 = new HashSet<Double>();
		for (String node : w.keySet())
			if (w.get(node)>tolerance)
				val2.add(w.get(node));  
		DoubleArrayList wVals = new DoubleArrayList();
		for (double vv : val2)
			wVals.add(vv);
		// sort in descending order
		vVals.quickSort();
		vVals.reverse();
		wVals.quickSort();
		wVals.reverse();

		// compute scores
		HashSet<String> sourceElements = new HashSet<String>();
		HashSet<String> targetElements = new HashSet<String>();
		Cluster cluster = new Cluster(this.hyperGraph, sourceElements, targetElements);
		double  maxscore=0.0, maxvalX=0.0, maxvalY=0.0, newscore;

		HashMap<String,Double> vSet = new HashMap<String,Double>();
		for (String node : v.keySet())
			vSet.put(node, 0.0);
		HashMap<String,Double> wSet = new HashMap<String,Double>();
		for (String node : w.keySet())
			wSet.put(node, 0.0);

		for (int k=0; k<vVals.size(); k++){
			// set source nodes
			for (String node1 : v.keySet()){
				if (v.get(node1)>=vVals.get(k))
					vSet.put(node1, 1.0);
			}
			// reset wSet
			for (String node : w.keySet())
				wSet.put(node, 0.0);
			for (int l=0; l<wVals.size(); l++){
				// set target nodes
				for (String node2 : w.keySet()){
					if (w.get(node2)>=wVals.get(l))
						wSet.put(node2, 1.0);
				}
				newscore = hg.vectorScore(vSet, wSet, this.p, this.q);
				if (newscore >= maxscore){
					maxscore = newscore;
					maxvalX = vVals.get(k);
					maxvalY = wVals.get(l);
				}
			}
		}
		// set final cluster
		sourceElements = new HashSet<String>();
		targetElements = new HashSet<String>();
		for (String node : v.keySet()){
			if (v.get(node)>=maxvalX)
				sourceElements.add(node);
		}
		for (String node : w.keySet()){
			if (w.get(node)>=maxvalY)
				targetElements.add(node);
		}
		cluster = new Cluster(this.hyperGraph, sourceElements, targetElements, this.p, this.q);
		return cluster;
	}

	/**
	 * Get highest scoring cluster based on Perron vector cutoffs, for directed hypergraphs, optimizing each dimension separately
	 * @param hg
	 * @param v
	 * @param w
	 * @param tol
	 * @return
	 */
	public Cluster bestClusterApprox (HyperGraph hg, HashMap<String,Double> v, HashMap<String,Double> w, double tol){
		// dirty way to get list of unique values above tolerance level
		HashSet<Double> val1 = new HashSet<Double>();
		for (String node : v.keySet())
			if (v.get(node)>tolerance)
				val1.add(v.get(node));  
		DoubleArrayList vVals = new DoubleArrayList();
		for (double vv : val1){
			vVals.add(vv);
		}
		// same for w
		HashSet<Double> val2 = new HashSet<Double>();
		for (String node : w.keySet())
			if (w.get(node)>tolerance)
				val2.add(w.get(node));  
		DoubleArrayList wVals = new DoubleArrayList();
		for (double vv : val2)
			wVals.add(vv);
		// sort in descending order
		vVals.quickSort();
		vVals.reverse();
		wVals.quickSort();
		wVals.reverse();

		// compute scores
		double  maxscore=0.0, maxvalX=0.0, maxvalXnew=0.0, maxvalY=0.0, maxvalYnew=0.0, newscore, diff=1.0;
		HashMap<String,Double> vSet = new HashMap<String,Double>();

		// initialize first dimension
		for (String node : v.keySet())
			vSet.put(node, 0.0);
		for (int k=0; k<vVals.size(); k++){
			// set source nodes
			for (String node1 : v.keySet()){
				if (v.get(node1)>=vVals.get(k))
					vSet.put(node1, 1.0);
			}
			newscore = hg.vectorScore(vSet, w, this.p, this.q);
			if (newscore >= maxscore){
				maxscore = newscore;
				maxvalX = vVals.get(k);
			}       
		}

		maxscore=0.0;
		HashMap<String,Double> wSet = new HashMap<String,Double>();
		for (String node : w.keySet())
			wSet.put(node, 0.0);
		for (int l=0; l<wVals.size(); l++){
			// set target nodes
			for (String node2 : w.keySet()){
				if (w.get(node2)>=wVals.get(l))
					wSet.put(node2, 1.0);
			}
			newscore = hg.vectorScore(vSet, wSet, this.p, this.q);
			if (newscore >= maxscore){
				maxscore = newscore;
				maxvalY = wVals.get(l);
			}
		}

		// set final cluster
		HashSet<String> sourceElements = new HashSet<String>();
		HashSet<String> targetElements = new HashSet<String>();
		for (String node : v.keySet()){
			if (v.get(node)>=maxvalX)
				sourceElements.add(node);
		}
		for (String node : w.keySet()){
			if (w.get(node)>=maxvalY)
				targetElements.add(node);
		}
		Cluster cluster = new Cluster(this.hyperGraph, sourceElements, targetElements, this.p, this.q);
		return cluster;
	}


	/**
	 * Select clusters with minimal number of nodes
	 */
	public void selectClustersSize(){
		HashSet<Cluster> remove = new HashSet<Cluster>();
		for (Cluster clust : this.clusters)
			if (clust.vertices.size()<this.minClustSize)
				remove.add(clust);
		for (Cluster clust : remove)
			this.clusters.remove(clust);
	}

	/**
	 * Select clusters with minimal score value
	 */
	public void selectClustersScore(){
		HashSet<Cluster> remove = new HashSet<Cluster>();
		for (Cluster clust : this.clusters)
			if (clust.score < this.minClustScore)
				remove.add(clust);
		for (Cluster clust : remove)
			this.clusters.remove(clust);
	}

	/**
	 * Write clusters to file (nodes)
	 * @param dir
	 * @param file
	 */
	public void writeClusters(String file, String outputfolder){
		try {
			File dir;
			dir=new File( outputfolder + "/");
			if(!dir.isDirectory()){
				dir.mkdirs();
			}
			
			File f = new File(file);
			PrintWriter pw = new PrintWriter(f);
			int ctr = 0;
			for (Cluster cluster : this.clusters) {
				if (!this.hyperGraph.directed)
					for (String node : cluster.vertices){
						String s = String.format("%s\t%d", node, ctr); 
						pw.println(s);
					}
				else {
					for (String node : cluster.sourceVertices){
						String s = String.format("%s\t%d", node, ctr); 
						pw.println(s);
					}
					for (String node : cluster.targetVertices){
						String s = String.format("%s\t%d", node, ctr); 
						pw.println(s);
					}
				}
				ctr++;
			}
			pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	
	/*
	public void writeClusterFeatures(String file, String featurefolder){

		String imgpath = "D:\\ImageData\\Flickr_small\\";
		CEDD cedd;
		CEDD cedd2;
		
		ArrayList<String> alist = new ArrayList<String>();

		try {
			File dir;
			dir=new File( featurefolder + "/");
			if(!dir.isDirectory()){
				dir.mkdirs();
			}

			File f = new File(file);
			PrintWriter pw = new PrintWriter(f);
			int ctr = 0;
			int ctr2 = 0;

			int offset = 2;
			
			String str_cedd;
			Double cedd_tk_d[];
			for (Cluster cluster : this.clusters) {

				cedd_tk_d = new Double[144];
				
				for(int i=0;i<144;i++){
					cedd_tk_d[i] = 0.0;
				}
				
				ctr2 = 0;

				if (!this.hyperGraph.directed){
					for (String node : cluster.vertices){
						if(isPhotomap){
							//System.out.println("1:"+node);
							
							str_cedd = photomap.get(node);
							
							//System.out.println("2:"+str_cedd);
							
							
							String cedd_tk[] = str_cedd.split(" ");
							for(int i = 0; i < 144; i++){
								//System.out.println(cedd_tk[i+offset]);
								cedd_tk_d[i] += Double.parseDouble( cedd_tk[i+offset] );
							}
							
						}else{
							
							if(isflickr){
								PhotosInterface pi = flickr.getPhotosInterface();
								
								Photo p;
								try {
									p = pi.getPhoto(node);
									URL url = new URL(p.getMediumUrl());
									BufferedImage bimg = ImageIO.read(url);
									cedd = new CEDD();
									
									cedd.extract(ImageIO.read(url));
									str_cedd =  cedd.getStringRepresentation();
									String cedd_tk[] = str_cedd.split(" ");
									for(int i = 0; i < 144; i++){
										//System.out.println(cedd_tk[i+offset]);
										cedd_tk_d[i] += Double.parseDouble( cedd_tk[i+offset] );
									}	
									
									
								} catch (FlickrException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SAXException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}else{
								cedd = new CEDD();
								cedd.extract(ImageIO.read(new FileInputStream(imgpath + node)));
								str_cedd =  cedd.getStringRepresentation();
								String cedd_tk[] = str_cedd.split(" ");
								for(int i = 0; i < 144; i++){
									//System.out.println(cedd_tk[i+offset]);
									cedd_tk_d[i] += Double.parseDouble( cedd_tk[i+offset] );
								}	
							}
							
							
												
						}
						ctr2++;
					}
				}
				else {

				}

				StringBuilder sb = new StringBuilder(144 * 2 + 25);
				sb.append("cedd");
				sb.append(' ');
				sb.append("144");
				sb.append(' ');
				for(int i = 0; i < 144; i++){
					cedd_tk_d[i] = cedd_tk_d[i]/ctr2;
					sb.append((int) Math.round(cedd_tk_d[i]));
					sb.append(' ');
				}
				
				alist.add( sb.toString().trim() );
				String s = String.format("%s", sb.toString().trim()); 
				pw.println(s);
				
				//cedd2 = new CEDD();
				//cedd2.setStringRepresentation(sb.toString().trim());
				//cedd2.getByteArrayRepresentation();
				
				
				ctr++;
			}

			pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	*/


	/**
	 * Write clusters to file (edges)
	 * @param dir
	 * @param file
	 */
	public void writeClusterEdges(String file, String outputfolder){
		try {

			File dir;
			dir=new File( outputfolder + "/");
			if(!dir.isDirectory()){
				dir.mkdirs();
			}
			
			File f = new File(file);
			PrintWriter pw = new PrintWriter(f);
			int ctr = 0;
			for (Cluster cluster : this.clusters) {
				if (!this.hyperGraph.directed){
					for (Edge edge : cluster.edges){
						String s = String.format("%d\t%s", ctr, edge.toString(this.hyperGraph.weighted)); 
						pw.println(s);
					}
				} else {
					for (Edge edge : cluster.edges){
						String s = String.format("%d\t%s", ctr, edge.toStringDirected(this.hyperGraph.weighted));
						pw.println(s);
					}
				}
				ctr++;
			}
			pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void writeScores(String file){
		try {
			File f = new File(file);
			PrintWriter pw = new PrintWriter(f);
			for (Cluster cl : this.clusters){
				pw.println(cl.score + "\t" + cl.scoreBound);
			}
			pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/*

	public void saveToHtml(String outputfolder, String file) {
		BufferedWriter bw;
		try {
			File dir;
			dir=new File(outputfolder + "/");
			if(!dir.isDirectory()){
				dir.mkdirs();
			}
			bw = new BufferedWriter(new FileWriter( outputfolder + "/"+ file + ".html"));

			String imgpath = "D:\\ImageData\\Flickr_small\\";
			String imgpath2 = "http://210.107.182.67/ImageData/Flickr_small/";

			bw.write("<html>\n" +
					"<head><title>Clustering Results</title></head>\n" +
					"<body bgcolor=\"#FFFFFF\">\n");
			bw.write("<h3>Category : "+ file +" </h3>\n");
			bw.write("<h3>results</h3>\n");

			FileInputStream imageStream;
			BufferedImage bimg;
			String resize = "";
			String apikey = ExpConst.APIKEY;
			int ctr = 1;
			int ctr2 = 0;
			URL url;
			for (Cluster cluster : this.clusters) {

				bw.write("<h3>cluster" + ctr + "</h3>\n");

				bw.write("<table width=\"1200\" border=\"2\" cellspacing=\"5\" bordercolor=\"black\" >\n");

				if (!this.hyperGraph.directed){

					for (String node : cluster.vertices){
						//String s = String.format("%s\t%d", node, ctr); 

						if(isflickr){
							
							PhotosInterface pi = flickr.getPhotosInterface();
							try {
								if(isUrlmap){
						
									url = new URL( urlmap.get(node) );
									bimg = ImageIO.read( url );
									//System.out.println("URL : " + urlmap.get(node) );
									
								}else{
									
									Photo p;
									p = pi.getPhoto(node);
									//System.out.println("URL : " + p.getMediumUrl());
									url = new URL(p.getMediumUrl());
									bimg = ImageIO.read(url);
									
		
								}
								resize = "";
								if( bimg.getHeight() >= 240 || bimg.getWidth() >= 240 ){
									if(bimg.getHeight()  >  bimg.getWidth()  ){
										resize = " height=\"240\"";
									}else{
										resize = " width=\"240\"";
									}
								}
								
							} catch (FlickrException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SAXException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}else{
							imageStream = new FileInputStream( imgpath + node );
							bimg = ImageIO.read(imageStream);
							resize = "";
							if( bimg.getHeight() >= 240 || bimg.getWidth() >= 240 ){
								if(bimg.getHeight()  >  bimg.getWidth()  ){
									resize = " height=\"240\"";
								}else{
									resize = " width=\"240\"";
								}
							}
							
						}
						// Used for Resizing the query image for convenience
						
						if( ctr2 == 0){
							bw.write("<tr>\n");	
						}else if( ctr2 % 4 ==0){
							bw.write("</tr>\n");
							bw.write("<tr>\n");
						}

						bw.write("<td>\n");
						//bw.write("<a href=\"file://" + imgpath + node + "\"><img src=\"file://" + imgpath + node + "\"" + resize + "></a><p>\n");
						
						if(isflickr){
							int tagct=0;
							try {
								PhotosInterface pi = flickr.getPhotosInterface();
								TagsInterface ti = flickr.getTagsInterface();
								
								Photo p;
								p = pi.getPhoto(node);
								bw.write("<font size=3><b>");
								bw.write(p.getTitle());
								bw.write("</b></font><br>");
								
								
								if(p.getDescription() != null){
									bw.write("<font size=3>");
									bw.write(p.getDescription());
									bw.write("</font><br>");
								}
								
								if(isUrlmap){
									bw.write("<a href=\"" + urlmap.get(node) + "\"><img src=\"" + urlmap.get(node) + "\"" + resize + "></a><br>\n");	
										
								}else{
									bw.write("<a href=\"" + p.getMediumUrl() + "\"><img src=\"" + p.getMediumUrl() + "\"" + resize + "></a><br>\n");	
								}
								Photo pt = ti.getListPhoto(node);
							    Collection tags = pt.getTags();
							    if( tags.size() != 0 ){
							    	tagct++;
							        Iterator tagsIter = tags.iterator(); 
							        Tag tag = (Tag) tagsIter.next();
							        //System.out.print(tag.getValue());
							        bw.write("<font size=3>");
							        bw.write(tag.getValue());
							        while (tagsIter.hasNext()) {
							        	tag = (Tag) tagsIter.next();
							            //System.out.print(" , " + tag.getValue());
							        	bw.write(" , " + tag.getValue());
							        }
							        bw.write("</font>");
							        //System.out.println();
								}
								
							} catch (FlickrException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SAXException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}else{
							bw.write("<a href=\"" + imgpath2 + node + "\"><img src=\"" + imgpath2 + node + "\"" + resize + "></a><p>\n");
						}
						bw.write("</td>\n");
						//pw.println(s);

						ctr2++;
					}
					bw.write("</tr>\n");
				}
				else {
					for (String node : cluster.sourceVertices){
						String s = String.format("%s\t%d", node, ctr); 
						//pw.println(s);
					}
					for (String node : cluster.targetVertices){
						String s = String.format("%s\t%d", node, ctr); 
						//pw.println(s);
					}
				}
				ctr++;

				bw.write("</table>\n");

			}


			bw.write("</body>\n" +
					"</html>");
			
			bw.close();
			//pw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		

	}
	
	*/

}