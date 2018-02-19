import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;

/******************************************************************************************************************
* File:SourceFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Description:
*
* This class serves as a template for creating source filters. The details of threading, connections writing output
* are contained in the FilterFramework super class. In order to use this template the program should rename the class.
* The template includes the run() method which is executed when the filter is started. The run() method is the guts
* of the filter and is where the programmer should put their filter specific code.The run() method is the main
* read-write loop for reading data from some source and writing to the output port of the filter. This template
* assumes that the filter is a source filter that reads data from a file, device (sensor),or generates the data
* interally, and then writes data to its output port. In this case, only the output port is used. In cases where the
* filter is a standard filter or a sink filter, you should use the FarToCelsiusFilter.java or SinkFilter.java as
* a starting point for creating standard or sink filters.
*
* Parameters: 		None
*
* Internal Methods:
*
*	public void run() - this method must be overridden by this class.
*
******************************************************************************************************************/

public class SourceFilter extends FilterFramework
{
	public void run()
	{

		String fileName = "FlightData.dat";	// Input data file.
		int bytesread = 0;					// Number of bytes read from the input file.
		int byteswritten = 0;				// Number of bytes written to the stream.
		DataInputStream in = null;			// File stream reference.
		byte databyte = 0;					// The byte of data read from the file

		try
		{
			/***********************************************************************************
			 *	Here we open the file and write a message to the terminal.
			 ***********************************************************************************/

			in = new DataInputStream(new FileInputStream(fileName));
			System.out.println("\n" + this.getName() + "::Source reading file..." );

			/***********************************************************************************
			 *	Here we read the data from the file and send it out the filter's output port one
			 * 	byte at a time. The loop stops when it encounters an EOFExecption.
			 ***********************************************************************************/

			while(true)
			{
				databyte = in.readByte();
				bytesread++;
				WriteFilterOutputPort(databyte);
				byteswritten++;

			} // while

		} //try

		/***********************************************************************************
		 *	The following exception is raised when we hit the end of input file. Once we
		 * 	reach this point, we close the input file, close the filter ports and exit.
		 ***********************************************************************************/

		catch ( EOFException eoferr )
		{
			System.out.println("\n" + this.getName() + "::End of file reached..." );
			try
			{
				in.close();
				ClosePorts();
				System.out.println( "\n" + this.getName() + "::Read file complete, bytes read::" + bytesread + " bytes written: " + byteswritten );

			}
			/***********************************************************************************
			 *	The following exception is raised should we have a problem closing the file.
			 ***********************************************************************************/
			catch (Exception closeerr)
			{
				System.out.println("\n" + this.getName() + "::Problem closing input data file::" + closeerr);

			} // catch

		} // catch

		/***********************************************************************************
		 *	The following exception is raised should we have a problem openinging the file.
		 ***********************************************************************************/

		catch ( IOException iox )
		{
			System.out.println("\n" + this.getName() + "::Problem reading input data file::" + iox );

		} // catch

	} // run

} // SourceFilter