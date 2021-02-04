package ms3;

import java.io.*;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 
import ms3.DatabaseHandler;
   
/*
 * A class to controll the flow of data between .csv file and database
 */

/**
 *
 * @author Stephen Petersen
 */
public class Controller {
    static DatabaseHandler db;
    private long numReceived;
    private long numSuccessful;
    private long numRejected;
    private String inputPath;
    private String rejectedPath;
    private String table;
    private String dbPath;

    public Controller() {
        this.db = new DatabaseHandler();
        this.numReceived = 0;
        this.numRejected = 0;
        this.numSuccessful = 0;
        String inputPath = "";
        String rejectedPath = "";
        String table = "";
        String dbPath = "";
    }
    
    //run the program.  This should contain all the necessary actions
    public void run(){
        /* For convenience, I put these declarations at the top, so they can
        easily be changed. */
        inputPath = "MS3Interview.csv";
        rejectedPath = "rejected.csv";
        table = "CSV_INPUT";
        dbPath = "jdbc:sqlite:MS3_Challenge.db";
        
        /* Creates a table if one doesn't already exist */
        db.OpenDB("jdbc:sqlite:MS3_Challenge.db", "CSV_INPUT");
        
       //Read in the .csv file
        String line = "";
        try{   
            BufferedReader br = new BufferedReader(new FileReader(inputPath));
            br.readLine();
            while((line = br.readLine()) != null)
            {
                String[] row = line.split(",");
                //System.out.println(row.length);
                ++numReceived;   //for the log file.
                boolean validRow = true;  //to decide whether this goes into
                                          //the table or the rejected csv
                //If this is too short, it's definitely not valid
               if(row.length < 11) 
                    validRow = false;
               /* Even if it's the right size, it's not valid if there is an
                  empty cell. */
               for(String var : row){
                    if(var.isEmpty()){
                        validRow = false;
                    }
                }
                if(validRow == true){  //so if the row contains all elements
                    /* 
                        The row array must be modified to maintain the integrity
                        of the data.  Indexes 5 and 6 are part of the same cell
                        but the original cell contains a comma, which is the
                        delimiter, so we have to recombine them.
                    */
                    String[] temp = new String[]{row[0], row[1], row[2], row[3], 
                    row[4] + "," + row[5], row[6], row[7], row[8], row[9], row[10]};
                    /* 
                        Some of these have an apostrophe, which messes up the 
                        INSERT sql query.  So we have to search for that value
                        and add an escape character.
                    */
                    int ind = temp[9].indexOf('\'');
                    if(ind > -1 && ind < temp[9].length()){
                        temp[9] = temp[9].substring(0, ind) + "\'\'" 
                        + temp[9].substring(ind+1);
                    }
                    ++numSuccessful;   //For the log file
                   
                    /* Insert the modified row into the table */
                     db.insert(temp, dbPath, table);
                }
                else{   //If the row is missing elements
                    //print to the rejected .csv file.
                    /* I had an issue here where the 10th column won't be printed
                        So I fixed it by recognizing that there are three situations
                        Either the final column isn't being read in (so there are
                        9 or fewer elements in the row array), the image URL is missing
                        (so there are only 10 elements in the array), or any other
                        cell is missing (so there are 11 elements in the array). I 
                        arranged these so that the most common cases are at the top, and
                        are reached first.
                    */
                    String[] temp;
                    switch (row.length){
                    case 11://modify the row and print it to th e.csv file.
                        //the delimiter separates the image URL, so we have to 
                        // combine indexes 5 and 6 with a comma, just as when we
                        // add it to the database.
                        temp = new String[]{row[0], row[1], row[2], row[3], 
                        row[4] + "," + row[5], row[6], row[7], row[8], row[9], row[10]};  
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    case 10: // This generally just means that cell 5 was empty
                        this.write(row, rejectedPath); // no need to refactor the array in this case
                        ++numRejected;
                        break;
                    case 9: // The remaining cases handle the situation where the last
                             // columns are missing.
                        temp = new String[]{row[0], row[1], row[2],
                        row[3], row[4], row[5], row[6], row[7], row[8], ""};
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    case 8:
                        temp = new String[]{row[0], row[1], row[2],
                        row[3], row[4], row[5], row[6], row[7], "", ""};
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    case 7:
                        temp = new String[]{row[0], row[1], row[2],
                        row[3], row[4], row[5], row[6], "", "", ""};
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    case 6:
                        temp = new String[]{row[0], row[1], row[2],
                        row[3], row[4], row[5], "", "", "", ""};
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    case 5:
                        temp = new String[]{row[0], row[1], row[2],
                        row[3], row[4], "", "", "", "", ""};
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    case 4:
                        temp = new String[]{row[0], row[1], row[2],
                        row[3], "", "", "", "", "", ""};
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    case 3:
                        temp = new String[]{row[0], row[1], row[2],
                        "", "", "", "", "", "", ""};
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    case 2:
                        temp = new String[]{row[0], row[1], "",
                        "", "", "", "", "", "", ""};
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    case 1:
                        temp = new String[]{row[0], "", "",
                        "", "", "", "", "", "", ""};
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    default:
                        temp = new String[]{"", "", "",
                        "", "", "", "", "", "", ""};  
                        this.write(temp, rejectedPath);
                        ++numRejected;
                        break;
                    }
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        this.printLog(); //print the log
    }
   
   
   
    //Print the updates to a log file.
    private void printLog(){
        /*
            The report looks like this
            Report:  MM/dd/yyyy HH:mm:ss
            Received: numReceived
            Successful: numSuccessful
            Rejected: numRejected
        */
        DateTimeFormatter form = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
         try{
            //The message.  Input isn't the best name.  Message would probably be better
            String input =  "Report:  " + form.format(now) + "\nReceived: "  + numReceived 
        + "\nSuccessful: " + numSuccessful + "\nRejected: " + numRejected + "\n";
            BufferedWriter writer = new BufferedWriter(
                new FileWriter("record.log", true));
            writer.append(input);   //Append, we don't want to replace
            writer.newLine(); //to make it easier to read
            writer.close();
        }catch(IOException e){
            System.out.println("An error ocurred.");
            e.printStackTrace();
        }
    }



   /* This was originally a part of a CSVHandler class,
      which I removed because this would be its only method. 
      It adds a row to the .csv with rejected rows. */
    public void write(String[] row, String file){
        try{
            //FileWriter writer = new FileWriter("bad_values.csv");
            String input =  row[0] + ","
                    + row[1] + ","
                    + row[2] + ","
                    + row[3] + ","
                    + row[4] + ","
                    + row[5] + ","
                    + row[6] + ","
                    + row[7] + ","
                    + row[8] + ","
                    + row[9] + ",,,,,";
            BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, true));
            writer.append(input);
            writer.newLine();
            writer.close();
        }catch(IOException e){
            System.out.println("An error ocurred.");
            e.printStackTrace();
        }
    }
}
