/*
 Copyright (c) 2007, Distributed Computing Group (DCG)
                    ETH Zurich
                    Switzerland
                    dcg.ethz.ch

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 - Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the
   distribution.

 - Neither the name 'Sinalgo' nor the names of its contributors may be
   used to endorse or promote products derived from this software
   without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package projects.bully_election_JP;


import java.io.FileOutputStream;

import sinalgo.configuration.Configuration;
import sinalgo.configuration.CorruptConfigurationEntryException;
import sinalgo.exception.SinalgoFatalException;
import sinalgo.runtime.AbstractCustomGlobal;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;

/**
 * This class holds customized global state and methods for the framework. 
 * The only mandatory method to overwrite is 
 * <code>hasTerminated</code>
 * <br>
 * Optional methods to override are
 * <ul>
 * <li><code>customPaint</code></li>
 * <li><code>handleEmptyEventQueue</code></li>
 * <li><code>onExit</code></li>
 * <li><code>preRun</code></li>
 * <li><code>preRound</code></li>
 * <li><code>postRound</code></li>
 * <li><code>checkProjectRequirements</code></li>
 * </ul>
 * @see AbstractCustomGlobal for more details.
 * <br>
 * In addition, this class also provides the possibility to extend the framework with
 * custom methods that can be called either through the menu or via a button that is
 * added to the GUI. 
 */
public class CustomGlobal extends AbstractCustomGlobal{

	public long workDone = 0;
	public long messagesSent = 0;
	public int totalNodes = 0;
	public String maliciousStr;

	public String baseFilepath = "./output/";


	public String algoName = "JP";
	
	@Override
	public boolean hasTerminated() {
		return false;
	}

	@Override
	public void onExit() {
		Logging logger = Logging.getLogger();

        try{
			totalNodes = Configuration.getIntegerParameter("NumberOfNodes/total");
			maliciousStr = Configuration.getStringParameter("Malicious/str");
			appendToFile();
		}catch (CorruptConfigurationEntryException e) {
			//logger.logln("Favor criar NumberOfNodes em Config.xml");
            throw new SinalgoFatalException(e.getMessage());
		}catch (Exception e) {
			logger.logln("Falhou ao criar csv");
		}

		logger.logln("Number of nodes: " + totalNodes);
		logger.logln("Total work done: " + workDone);
		logger.logln("Total messages sent: " + messagesSent);
		logger.logln("Simulation steps: " + Tools.getGlobalTime());
	}

	public void appendToFile() throws Exception {

	FileOutputStream fos = new FileOutputStream(baseFilepath + algoName + maliciousStr + "_" + totalNodes + "_nodes.csv", true);
		fos.write((workDone + ", " + messagesSent + ", " + Tools.getGlobalTime() + "\r\n").getBytes());
		fos.close();
	}
}
