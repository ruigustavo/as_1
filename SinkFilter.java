import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/******************************************************************************************************************
* File:SinkFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Description:
*
* This class serves as a template for creating sink filters. The details of threading, connections writing output
* are contained in the FilterFramework super class. In order to use this template the program should rename the class.
* The template includes the run() method which is executed when the filter is started.
* The run() method is the guts of the filter and is where the programmer should put their filter specific code.
* In the template there is a main read-write loop for reading from the input port of the filter. The programmer is
* responsible for writing the data to a file, or device of some kind. This template assumes that the filter is a sink
* filter that reads data from the input file and writes the output from this filter to a file or device of some kind.
* In this case, only the input port is used by the filter. In cases where the filter is a standard filter or a source
* filter, you should use the FarToCelsFilter.java or the SourceFilter.java as a starting point for creating
* standard or source filters.
*
* Parameters: 		None
*
* Internal Methods:
*
*	public void run() - this method must be overridden by this class.
*
******************************************************************************************************************/

public class SinkFilter extends FilterFramework
{
	public void run()
    {
		String fileName = "NewOutput.dat"; // new output
		DataOutputStream file = null;
		Calendar TimeStamp = Calendar.getInstance();
		SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy:dd:hh:mm:ss");

		int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
		int IdLength = 4;				// This is the length of IDs in the byte stream

		byte databyte = 0;				// This is the data byte read from the stream
		int bytesread = 0;				// This is the number of bytes read from the stream
		int byteswritten = 0;				// Number of bytes written to the stream.
		String time = null;
		String altitude = null;
		String temp = null;
		long measurement;				// This is the word used to store all measurements - conversions are illustrated.
		int id;							// This is the measurement id
		int i;							// This is a loop counter

		boolean checkpoint = false;
		try {
			file = new DataOutputStream(new FileOutputStream(fileName));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		/*************************************************************
		 *	First we announce to the world that we are alive...
		 **************************************************************/

		System.out.println( "\n" + this.getName() + "::Sink Reading ");

/*************************************************************
*	This is the main processing loop for the filter. Since this
*   is a sink filter, we read until there is no more data
* 	available on the input port.
**************************************************************/

		while (true)
		{
			try
			{
				id = 0;

				for (i=0; i<IdLength; i++ )
				{
					databyte = ReadFilterInputPort();	// This is where we read the byte from the stream...

					id = id | (databyte & 0xFF);		// We append the byte on to ID...

					if (i != IdLength-1)				// If this is not the last byte, then slide the
					{									// previously appended byte to the left by one byte
						id = id << 8;					// to make room for the next byte we append to the ID

					} // if

					bytesread++;						// Increment the byte count

				} // for

				measurement = 0;

				for (i=0; i<MeasurementLength; i++ )
				{
					databyte = ReadFilterInputPort();
					measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

					if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
					{												// previously appended byte to the left by one byte
						measurement = measurement << 8;				// to make room for the next byte we append to the
						// measurement
					} // if

					bytesread++;									// Increment the byte count

				} // if

				if ( id == 0 )
				{
					TimeStamp.setTimeInMillis(measurement);
					time = TimeStampFormat.format(TimeStamp.getTime());
					System.out.print("Time:" + TimeStampFormat.format(TimeStamp.getTime() ));
				} // if
				else if ( id == 2 )
				{
					altitude = String.format("%.5f", Double.longBitsToDouble(measurement));
					System.out.print("\tAlti:" + String.format("%.5f", Double.longBitsToDouble(measurement)) );

				} // if
				else if ( id == 4 )
				{
					temp = String.format("%.4f", Double.longBitsToDouble(measurement));
					System.out.print("\tTemp:" + String.format("%.4f", Double.longBitsToDouble(measurement)) );
					System.out.print( "\n" );

					checkpoint = true;

				} // if
				if(checkpoint){
					file.writeChars(time+"\t");
					file.writeChars(temp+"\t");
					file.writeChars(altitude+"\n");
					checkpoint = false;
				}



			} // try

/***************************************************************
*	When we reach the end of the input stream, an exception is
* 	thrown which is shown below. At this point, you should
* 	finish up any processing, close your ports and exit.
***************************************************************/

			catch (EndOfStreamException e)
			{
				ClosePorts();
				System.out.println( "\n" + this.getName() + "::Read file complete, bytes read::" + bytesread + " bytes written: " + byteswritten );

				break;

			} // catch
			catch (IOException e) {
				e.printStackTrace();
			}

		} // while

   } // run

} // FarToCelsFilter