package com.mondego.interfaces;

import java.lang.reflect.InvocationTargetException;

public interface CandidateProcessorInterface{
	
	//private QueryCandidates qc;
    //private static final Logger logger = LogManager.getLogger(CandidateProcessor.class);
	 
	 public void processResultWithFilter()
	            throws InterruptedException, InstantiationException, IllegalAccessException, IllegalArgumentException,
	            InvocationTargetException, NoSuchMethodException, SecurityException;

}
