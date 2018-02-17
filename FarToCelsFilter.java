import java.nio.ByteBuffer;

/******************************************************************************************************************
* File:FarToCelsFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Description:
*
* This class serves as a template for creating filters. The details of threading, filter connections, input, and output
* are contained in the FilterFramework super class. In order to use this template the program should rename the class.
* The template includes the run() method which is executed when the filter is started.
* The run() method is the guts of the filter and is where the programmer should put their filter specific code.
* In the template there is a main read-write loop for reading from the input port of the filter and writing to the
* output port of the filter. This template assumes that the filter is a "normal" that it both reads and writes data.
* That is both the input and output ports are used - its input port is connected to a pipe from an up-stream filter and
* its output port is connected to a pipe to a down-stream filter. In cases where the filter is a source or sink, you
* should use the SourceFilterTemplate.java or SinkFilterTemplate.java as a starting point for creating source or sink
* filters.
*
* Parameters: 		None
*
* Internal Methods:
*
*	public void run() - this method must be overridden by this class.
*
******************************************************************************************************************/

public class FarToCelsFilter extends FilterFramework
{
    public static byte[] convertToByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putDouble(value);
        return buffer.array();

    }

	public void run()
    {

        System.out.print( "\n" + this.getName() + "::MIDDLE FILTER Reading ");
        byte[] output = new byte[8];
		long measurement;				// This is the word used to store all measurements - conversions are illustrated.
		int id;							// This is the measurement id
		int i;							// This is a loop counter

		byte databyte = 0;				// This is the data byte read from the stream
		int bytesread = 0;				// This is the number of bytes read from the stream
        int byteswritten = 0;				// Number of bytes written to the stream.

		int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
		int IdLength = 4;				// This is the length of IDs in the byte stream
		while (true)
		{

/***************************************************************
*	The program can insert code for the filter operations
* 	here. Note that data must be received and sent one
* 	byte at a time. This has been done to adhere to the
* 	pipe and filter paradigm and provide a high degree of
* 	portabilty between filters. However, you must reconstruct
* 	data on your own. First we read a byte from the input
* 	stream...
***************************************************************/

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
					// Increment the byte count
                    bytesread++;
                    WriteFilterOutputPort(databyte);
                    byteswritten++;
				} // for

                measurement = 0;
                if ( id == 4 )
                {
                    for (i=0; i<MeasurementLength; i++ ){

                        databyte = ReadFilterInputPort();
                        measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

                        if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
                        {												// previously appended byte to the left by one byte
                            measurement = measurement << 8;				// to make room for the next byte we append to the
                            // measurement
                        } // if

                        bytesread++;

                    } // for
                    double aux = (Double.longBitsToDouble(measurement));
                    System.out.println("temp faren:"+aux);

                    aux = ((aux - 32L)*5L)/9L;
                    //System.out.println("tem cels:"+aux);
                    output = convertToByteArray(aux);

                    for (i=0; i<MeasurementLength; i++ ){
                        databyte = output[i];
                        WriteFilterOutputPort(databyte);
                        byteswritten++;
                    }
                } // if
                else{
                    for (i=0; i<MeasurementLength; i++ ){
                        databyte = ReadFilterInputPort();
                        bytesread++;
                        WriteFilterOutputPort(databyte);
                        byteswritten++;
                    }
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
                System.out.println( "\n" + this.getName() + ":: bytes read::" + bytesread + " bytes written: " + byteswritten );
                break;

            } // catch

		} // while

   } // run

} // FarToCelsFilter