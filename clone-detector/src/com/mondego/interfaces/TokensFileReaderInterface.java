package com.mondego.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public interface TokensFileReaderInterface {
	
	public void read()
            throws FileNotFoundException, IOException, ParseException ;

}
