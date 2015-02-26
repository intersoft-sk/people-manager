package eu.ebbitsproject.peoplemanager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * The VPOS client ready to be used in other programs. It interfaces to VPOS via
 * command line calls. For distribution we can replace this interface with a
 * webservice communication later if required.
 * 
 */
public class VPOSClient {

	/**
	 * Method to ask VPOS whether a given employee has a competence
	 * 
	 * @param directoryPath
	 *            is the directory VPOS is installed at, e.g. "./" or
	 *            "/Users/Martin/ebbits/middleware/managers/OntologyManager/VPOS"
	 * @param whoIsAsking
	 *            is the user role of the person asking, e.g. Manager or
	 *            ShiftResponsible
	 * @param employee
	 *            e.g. http://www.ebbits-project.eu/ontologies/M48_HR.owl#Bob
	 * @param competence
	 *            e.g. http://www.ebbits-project.eu/ontologies/M48_HR.owl#
	 *            AbleToRepairRobots"
	 * @return boolean yes/no whether the employee has the competence.
	 * @throws IllegalArgumentException
	 *             in case of errors
	 */
	public boolean employeeHasCompetence(String directoryPath,
			String whoIsAsking, String employee, String competence)
			throws IllegalArgumentException {
		Runtime rt = Runtime.getRuntime();
		try {
			// detect which OS I am in
			String classPathLibraryPath = null;
			String OS = System.getProperty("os.name", "generic").toLowerCase(
					Locale.ENGLISH);
			if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
				classPathLibraryPath = "-cp bin/:libs/*:libs/owlapi-3.3/* -Djava.library.path=libs/FaCT++-OSX-v1.5.3/64bit";
			} else if (OS.indexOf("win") >= 0) {
				classPathLibraryPath = "-cp bin/;libs/*;libs/owlapi-3.3/* -Djava.library.path=libs/FaCT++-win-v1.5.3/64bit";
			} else if (OS.indexOf("nux") >= 0) {
				classPathLibraryPath = "-cp bin/:libs/*:libs/owlapi-3.3/* -Djava.library.path=libs/FaCT++-linux-v1.5.3/64bit";
			}

			String command = "java "
					+ classPathLibraryPath
					+ " eu.ebbits.middleware.ontologymanager.vpos.services.VPOSDemoEbbitsM48 "
					+ whoIsAsking + " " + employee + " " + competence;
			System.out.println("Calling VPOS: " + command);
			Process pr = rt.exec(command, null, new File(directoryPath));
			BufferedReader input = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));

			String line = input.readLine();
			System.out.println("VPOS answers " + line + " to a " + whoIsAsking
					+ " whether employee " + employee + " has competence "
					+ competence);

			if ("yes".equals(line))
				return true;
			else if ("no".equals(line))
				return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Only in error cases I reach until here
		throw new IllegalArgumentException(
				"Unknown return value received from VPOS");
	}

	public static void main(String[] args) {
		System.out.println("Doing some example calls to VPOS on commandline ...");
		VPOSClient client = new VPOSClient();
                
                String VPOSDir = "C:\\Users\\glukac\\Development\\ebbits\\VPOS";

		client.employeeHasCompetence(VPOSDir, "Manager",
				"http://www.ebbits-project.eu/ontologies/M48_HR.owl#Bob",
				"http://www.ebbits-project.eu/ontologies/M48_HR.owl#AbleToRepairRobots");

		client.employeeHasCompetence(VPOSDir, "ShiftResponsible",
				"http://www.ebbits-project.eu/ontologies/M48_HR.owl#Bob",
				"http://www.ebbits-project.eu/ontologies/M48_HR.owl#AbleToRepairRobots");

		System.out.println("... successfully finished.");
	}
}
