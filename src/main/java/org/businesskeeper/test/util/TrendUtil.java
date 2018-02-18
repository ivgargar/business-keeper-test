package org.businesskeeper.test.util;

public class TrendUtil {
	public static boolean isAscendingTrend(double[] data){
	    for(int i = 1; i < data.length; i++){
	        if(data[i-1] >= data[i]){
	            return false;
	        }
	    }
	    return true;
	}
	
	public static boolean isDescendingTrend(double[] data){
	    for(int i = 1; i < data.length; i++){
	        if(data[i-1] <= data[i]){
	            return false;
	        }
	    }
	    return true;
	}
	
	public static boolean isConstantTrend(double[] data){
	    for(int i = 1; i < data.length; i++){
	        if(data[i-1] != data[i]){
	            return false;
	        }
	    }
	    return true;
	}
}
