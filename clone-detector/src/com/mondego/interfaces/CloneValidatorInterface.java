package com.mondego.interfaces;

import java.lang.reflect.InvocationTargetException;

import com.mondego.models.CandidatePair;
import com.mondego.models.CandidateSimInfo;
import com.mondego.models.QueryBlock;
import com.mondego.models.TokenInfo;

public interface CloneValidatorInterface {
	
	public void validate(CandidatePair candidatePair)
            throws InterruptedException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException;
	
	public int updateSimilarity(QueryBlock queryBlock, String tokens, int computedThreshold, int candidateSize,
            CandidateSimInfo simInfo);
	
	

}
