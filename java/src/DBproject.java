/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Doctor");
				System.out.println("2. Add Patient");
				System.out.println("3. Add Appointment");
				System.out.println("4. Make an Appointment");
				System.out.println("5. List appointments of a given doctor");
				System.out.println("6. List all available appointments of a given department");
				System.out.println("7. List total number of different types of appointments per doctor in descending order");
				System.out.println("8. Find total number of patients per doctor with a given status");
				System.out.println("9. < EXIT");
				
				switch (readChoice()){
					case 1: AddDoctor(esql); break;
					case 2: AddPatient(esql); break;
					case 3: AddAppointment(esql); break;
					case 4: MakeAppointment(esql); break;
					case 5: ListAppointmentsOfDoctor(esql); break;
					case 6: ListAvailableAppointmentsOfDepartment(esql); break;
					case 7: ListStatusNumberOfAppointmentsPerDoctor(esql); break;
					case 8: FindPatientsCountWithStatus(esql); break;
					case 9: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddDoctor(DBproject esql) {//1
		int doctor_ID;
		String doctor_Name;
		String specialty;
		int dept_ID;

		do {
			System.out.print("What's the doctor's ID?");
			try {
				doctor_ID = Integer.parseInt(in.readLine());
				break;
			}
			catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}

		}
		while (true);
	
		do {
			System.out.print("What's the doctor's name?");
			try {
				doctor_Name = in.readLine();
				if (doctor_Name.length() <= 0 || doctor_Name.length() > 128) {
					throw new RuntimeException("The doctor's name must be between 1 and 128 characters.");
				}
				break;
			}
			catch (Exception e) {
				System.out.println("Your input is invalid! " + e.getMessage());
				continue;
			}

		}
		while (true);

		do {
                        System.out.print("What's the doctor's specialty?");
                        try {
                                specialty = in.readLine();
                                if (specialty.length() <= 0 || specialty.length() > 24) {
                                        throw new RuntimeException("The doctor's specialty must be between 1 and 24 characters.");
                                }
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid! " + e.getMessage());
                                continue;
                        }

                }
                while (true);

		do {
                        System.out.print("What's the doctor's department ID?");
                        try {
                                dept_ID = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
                while (true);
		
		try {
			String query = "INSERT INTO Doctor (doctor_ID, name, specialty, did) VALUES (" + doctor_ID + ", \'" + doctor_Name + "\', \'" + specialty + "\', " + dept_ID + ");";
		esql.executeUpdate(query);
		}
		catch (Exception e) {
			System.err.println("Query invalid!" + e.getMessage());
		}				
	
	}

	public static void AddPatient(DBproject esql) {//2
		int patient_ID;
		String patient_Name;  
		String gender;  
		int patient_Age;
		String address; 
		int number_of_appts;
		
		do {
                        System.out.print("What's the patient's ID?");
                        try {
                                patient_ID = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
                while (true);

		do {
                        System.out.print("What's the patient's name?");
                        try {
                                patient_Name = in.readLine();
                                if (patient_Name.length() <= 0 || patient_Name.length() > 128) {
                                        throw new RuntimeException("The patient's name must be between 1 and 128 characters.");
                                }
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!" + e.getMessage());
                                continue;
                        }

                }
                while (true);

		do {
			System.out.print("What is the patient's gender?");
			try {
				gender = in.readLine();
				if(!gender.equals("F") && !gender.equals("M")) {
					throw new RuntimeException("There are only two genders, F or M");
				}
				break;
			}
			catch (Exception e) {
				System.out.println("Your input is invalid! " + e.getMessage()) ;
				continue;
			}
		}
		while(true);

		do {
                        System.out.print("What's the patient's age?");
                        try {
                                patient_Age = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
		while(true);

		do {
                        System.out.print("What's the patient's address?");
                        try {
                                address = in.readLine();
                                if (address.length() <= 0 || address.length() > 256) {
                                        throw new RuntimeException("The patient's address must be between 1 and 256 characters.");
                                }
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid! " + e.getMessage());
                                continue;
                        }

                }
                while (true);	

		do {
                        System.out.print("How many appointments does the patient have? ");
                        try {
                                number_of_appts = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
                while(true);

		try {
                        String query = "INSERT INTO Patient (patient_ID, name, gtype, age, address, number_of_appts) VALUES (" + patient_ID + ", \'" + patient_Name + "\', \'" + gender + "\', " + patient_Age + ", \'" + address + "\', " + number_of_appts + ");";
                esql.executeUpdate(query);
                }
                catch (Exception e) {
                        System.err.println("Invalid query!" + e.getMessage());
                }

	}

	public static void AddAppointment(DBproject esql) {//3
		int appnt_ID;

		String date;
		LocalDate appointment_Date;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		String time_slot;

		String status;	
		

		do {
                        System.out.print("What's the appointment ID? ");
                        try {
                                appnt_ID = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
                while(true);

		do {
			System.out.print("What's the appointment date? ");
			try {
				date = in.readLine();
				appointment_Date = LocalDate.parse(date, dtf);
				break;
			}
			catch (Exception e) {
				System.out.println("Your input is invalid! Must be in the format: MM-DD-YYYY");
				continue;
			}
		}
		while (true);
		
		do {
                        System.out.print("What's the appointment time slot? ");
                        try {
                                time_slot = in.readLine();
                                if (time_slot.length() != 11) {
                                        throw new RuntimeException("Must be in the format: HH:MM-HH:MM");
                                }
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid! " + e.getMessage());
                                continue;
                        }

                }
                while (true);

		do {
                        System.out.print("What is the appointment's status? ");
                        try {
                                status = in.readLine();
                                if(!status.equals("PA") && !status.equals("AC") && !status.equals("AV") && !status.equals("WL")) {
                                        throw new RuntimeException("Must be PA, AC, AV, or WL.");
                                }
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid! " + e.getMessage()) ;
                                continue;
                        }
                }
                while(true);	

		try {
                        String query = "INSERT INTO Appointment (appnt_ID, adate, time_slot, status) VALUES (" + appnt_ID + ", \'" + date + "\', \'" + time_slot + "\', \'" + status + "\');";
                esql.executeUpdate(query);
                }
                catch (Exception e) {
                        System.err.println("Invalid query!" + e.getMessage());
                }	

	}


	public static void MakeAppointment(DBproject esql) {//4
		// Given a patient, a doctor and an appointment of the doctor that s/he wants to take, add an appointment to the DB
		int patID;
		String patName;
                String patGender;
                int patAge;
                String patAddress;
                int patNumber_of_appts;
		int docID;
		int appID;
		String appStatus;
		
		do {
                        System.out.print("What's the patient's ID? ");
                        try {
                                patID = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
                while (true);

		 do {
                        System.out.print("What's the patient's name? ");
                        try {
                                patName = in.readLine();
                                if (patName.length() <= 0 || patName.length() > 128) {
                                        throw new RuntimeException("The patient's name must be between 1 and 128 characters.");
                                }
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!" + e.getMessage());
                                continue;
                        }

                }
                while (true);

                do {
                        System.out.print("What is the patient's gender? ");
                        try {
                                patGender = in.readLine();
                                if(!patGender.equals("F") && !patGender.equals("M")) {
                                        throw new RuntimeException("There are only two genders, F or M");
                                }
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid! " + e.getMessage()) ;
                                continue;
                        }
                }
                while(true);

                do {
                        System.out.print("What's the patient's age? ");
                        try {
                                patAge = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
                while(true);

                do {
                        System.out.print("What's the patient's address?");
                        try {
                                patAddress = in.readLine();
                                if (patAddress.length() <= 0 || patAddress.length() > 256) {
                                        throw new RuntimeException("The patient's address must be between 1 and 256 characters.");
                                }
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid! " + e.getMessage());
                                continue;
                        }

                }
                while (true);

                do {
                        System.out.print("How many appointments does the patient have? ");
                        try {
                                patNumber_of_appts = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
                while(true);

		do {
                        System.out.print("What's the doctor's ID?");
                        try {
                                docID = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
                while (true);
	
		do {
                        System.out.print("What's the appointment ID? ");
                        try {
                                appID = Integer.parseInt(in.readLine());
                                break;
                        }
                        catch (Exception e) {
                                System.out.println("Your input is invalid!");
                                continue;
                        }

                }
                while(true);
		do {
		try {
			String query = "SELECT status FROM Appointment WHERE appnt_ID = " + appID + ";";
				
			if (esql.executeQueryAndPrintResult(query) == 0) {
				System.out.println("No such appointment with that ID.");	
			}

			else {
				
				List<List<String>> yes = esql.executeQueryAndReturnResult(query);
				String result = yes.get(0).get(0);
				//System.out.println(result);
				
				if (result.equals("AV")) {
					try {
						query = "UPDATE Patient Set number_of_appts = " + (patNumber_of_appts+1) + ";"; 
						esql.executeUpdate(query);	
						System.out.println("Updating patient table");
					}
					catch (Exception e) {
						System.err.println("Query invalid.");
					}

					try {			
						query = "INSERT INTO has_appointment (appt_ID, doctor_ID) VALUES (" + appID + ", " + docID + ");";
						esql.executeUpdate(query);
						System.out.println("Inserting into appointment table"); 
                			}
                			catch (Exception e) {
                        			System.err.println("Query invalid!");
					}

					try {
                                                query = "UPDATE Appointment Set status = 'AC';";
                                                esql.executeUpdate(query);
						System.out.println("Status updated to AC");
                                        }
                                        catch (Exception e) {
                                                System.err.println("Query invalid.");
                                        }
                		
				}
			
				else if (result.equals("AC")) {
                                        try {
                                                query = "UPDATE Patient Set number_of_appts = " + (patNumber_of_appts+1) + ";";
                                                esql.executeUpdate(query);
						System.out.println("Updating patient table");
                                        }
                                        catch (Exception e) {
                                                System.err.println("Query invalid.");
                                        }

                                        try {
                                                query = "INSERT INTO has_appointment (appt_ID, doctor_ID) VALUES (" + appID + ", " + docID + ");";
                                                esql.executeUpdate(query);
						System.out.println("Inserting into appointment table");
                                        }
                                        catch (Exception e) {
                                                System.err.println("Query invalid!");
                                        }

                                        try {
                                                query = "UPDATE Appointment Set status = 'WL';";
                                                esql.executeUpdate(query);
                                                System.out.println("Status updated to WL");
                                        }
                                        catch (Exception e) {
                                                System.err.println("Query invalid.");
                                        }
                                }

				else if (result.equals("WL")) {
                                        try {
                                                query = "UPDATE Patient Set number_of_appts = " + (patNumber_of_appts+1) + ";";
                                                esql.executeUpdate(query);
                                                System.out.println("Updating patient table");
                                        }
                                        catch (Exception e) {
                                                System.err.println("Query invalid.");
                                        }

                                        try {
                                                query = "INSERT INTO has_appointment (appt_ID, doctor_ID) VALUES (" + appID + ", " + docID + ");";
                                                esql.executeUpdate(query);
                                                System.out.println("Inserting into appointment table");
                                        }
                                        catch (Exception e) {
                                                System.err.println("Query invalid!");
                                        }

                                          
                                        
                                }

				
					
			}
				
				
			break;
			}
		
		catch (Exception e) {
                        System.err.println(e);
                        continue;
                }
		}while(true);
				
			

		
		

	}

	public static boolean validateDate(String str){
        if(str.length() != 10 || str.charAt(4) != '-' && str.charAt(7) != '-')
            return false;
        
        String[] date = str.split("-", -1);
        
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);
        
        
        if(year < 1900 || month < 1 || month > 12 || day < 1 || day > 31)
            return false;
        
        int daysInMonth = 0;
        switch(month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                daysInMonth = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                daysInMonth = 30;
                break;
            case 2:
                daysInMonth = 28;
                break;
            default:
                return false;
        }
        
        return (day <= daysInMonth);
    }


	   public static void ListAppointmentsOfDoctor(DBproject esql) {
    
        
        Scanner input = new Scanner(System.in);
        
       
        String validatedDocID = "";
        do{
            System.out.println("Enter Doctor ID:  ");
            String doctor_id = input.next();
            
            String query = "SELECT doctor_ID FROM Doctor";
            List<List<String>> listOfDoctors = new ArrayList<List<String>>();
            
            
            
            for(List<String> did : listOfDoctors){
                for(int i = 0; i < did.size(); i++){
                    if(doctor_id == did.get(i))
                        validatedDocID = doctor_id;
                }
            }
        }while("".equals(validatedDocID));
        
        
       
        String date1 = "";
        do{
            System.out.println("Enter the earliest date in YYYY-MM-DD format:  ");
            date1 = input.next();
        }while(!validateDate(date1));
        
        
        
        String date2 = "";
        do{
            System.out.println("Enter the earliest date in YYYY-MM-DD format:  ");
            date2 = input.next();
        }while(!validateDate(date2));
        
        
        
        System.out.println("Listing all active and available appointments of the doctor.");
        String query = "";
      
    }
 
    public static void ListAvailableAppointmentsOfDepartment(DBproject esql) {
      
        Scanner input = new Scanner(System.in);
        
        String validatedDepartmentName = "";
        do{
            System.out.print("Enter Department Name:  ");
            String dep_name = input.next();
            
            String query = "SELECT name FROM Department";
            List<List<String>> listOfDepartments = new ArrayList<List<String>>();
            
            
            for(List<String> name : listOfDepartments){
                for(int i = 0; i < name.size(); i++){
                    if(dep_name == name.get(i))
                        validatedDepartmentName = dep_name;
                }
            }
        }while("".equals(validatedDepartmentName));
        
        
       
        String date1 = "";
        do{
            System.out.println("Enter date in YYYY-MM-DD format:  ");
            date1 = input.next();
        }while(!validateDate(date1));
        
        
        System.out.println("List of available appointments of the department: " + validatedDepartmentName);
        String query = "";
    }
 
    public static void ListStatusNumberOfAppointmentsPerDoctor(DBproject esql) {
        Scanner input = new Scanner(System.in);
    
    }
 
    
    public static void FindPatientsCountWithStatus(DBproject esql) {
       
        Scanner input = new Scanner(System.in);
 
        
        String validatedStatus = "";
        do{
            System.out.println("Enter appointment status to search: PA, AC, AV, WL");
            String status = input.next();
            if(status == "PA" || status == "AC" || status == "AV" || status =="WL"){
                validatedStatus = status;
            }
        }while("".equals(validatedStatus));
 
        String query = "";
 
 
    }
}

/*	public static void ListAppointmentsOfDoctor(DBproject esql) {//5
		// For a doctor ID and a date range, find the list of active and available appointments of the doctor
	}

	public static void ListAvailableAppointmentsOfDepartment(DBproject esql) {//6
		// For a department name and a specific date, find the list of available appointments of the department
	}

	public static void FindPatientsCountWithStatus(DBproject esql) {//8
		// Find how many patients per doctor there are with a given status (i.e. PA, AC, AV, WL) and list that number per doctor.
	}
	
	public static void ListStatusNumberOfAppointmentsPerDoctor(DBproject esql) {//7
                // Count number of different types of appointments per doctors and list them in descending order
                

        }*/
	


