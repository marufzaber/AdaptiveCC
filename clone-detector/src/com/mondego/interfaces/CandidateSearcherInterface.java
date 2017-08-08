package com.mondego.interfaces;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.mondego.models.QueryBlock;

public interface CandidateSearcherInterface {
	
	public void searchCandidates(QueryBlock queryBlock)
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

}
