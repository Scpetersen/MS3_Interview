# MS3_Interview
Statement of Purpose.
A Java applicationfor the MS3 interview.  The program reads data from a .csv file and insers it into a SQLite database.


Steps for Developers
If you want to simply run the application, go to the "dist" folder and run the .jar file, and it run off of the .csv file provided for this challenge.  It will generate its own databse called "MS3_Challenge.db", a .csv file called "rejected.csv", and a .log file called "record.log".

If you want to change the input file, which is currently MS3Interview.csv, go into the src folder and then ms3.  Open the "Controller" class.  Under the "run()" method you'll see inputPath = "MS3Interview.csv".  You can change this string to the  path of your chosen input file.  Remember to make sure your .csv file is actually in that pathway.

If you want to change the name of the .csv file to which the rejected rows are printed, go to src > ms3 folder.  In the "Controller" class, under the "run()" method, you can change the rejectedPath string to the path of the desired file.

If you want to change the name of the table, again you can go to src > ms3, open the "Controller" class, and under the "run()" method change "table" to your desired name.

If you want to change the name of the database, or run from an existing database, go into the src folder and then ms3.  Open the Controller class.  Under the "run()" method, change the "dbPath" string to the desired database, as in "jdbc:sqlite:database_name.db".


Overview of approach, design choices, and assumptions
Admittedly, this was a sort of design as you go, which obviously isn't the best way to handle any program.  However, there were
several concepts that I had to learn along the way as I had never worked with .csv files, and have only a little experience with
SQL.  I started with an object oriented approach.  The Controller class and DatabaseHandler class come from that approach.  I
Originally planned to use a CSVHandler class too,  but it would only have one method, and so I merged it into the Controller.
From there, the algorithms used are mainly mitigating issues I found in code as I ran the program.  For instance, we're using
a "Comma-separated values" file, which means that, in plaintext, the the file is a bunch of values, separated by a comma.  This
seems straightforward until you realize that some of the values may contain commas themselves.  I was committed to using java
Utility libraries, because I wanted this to be the kind of program you can run with little to no configuration.  This meant that
I didn't download a library dedicated to reading .csv files.  Instead, I read them in, creating tokens with the comma as a delimiter, which meant that some values were separated when they should be, so the array I would read in would contain an extra value.  I compensated for this by creating a new array that would recombine those two elements with a new comma separating them.

Another issue that I found was that values with single quotes will interfere with the SQL queries made in JDBC.  It took me a while to learn that the escape character in SQL was also a single quote, unlike in Java or C++.  Once I learned this, I had to devise a method that would search the strings to find a single quote, and insert another single quote so that SQL reads it as an escape character.

I had an issue with .csv files because they end in a series of commas, that tells excel that the row is complete when read in that program.  This is fine, as it's trivial to append the row with the appropriate number of commas.  However, this would become a problem when the last cell or two in the row is/are missing, because then excel would not read it correctly, so each row had to be checked for this condition and corrected.

Finally, all this variablity in data created the situation where my file reader would read in rows of varying length, which creates a challenge of having to format each array to the appropriate length before printing it to the .csv or database.  I eventually settled on the solution of handling each with a switch statement, just in case there ended up being more variability than I had a solution for.  For efficiency's sake, I put the most common variations at the top of the switch statement.

When designing, I made the following assumptions:
The user will want to make this program work with different input files, so I made it as easy as possible to find the current filename and replace it with the path of the input file.
The same goes for the database, rejected rows file, and log file.  They are placed together at the top of the Controller class.
The files will always have the correct information in corresponding columns, that is the image will always be in the right column.  This would be a problem, especially if the user put that image in a different column.  This goes for the final column as well.  To optimize the program, it only checks the last row for single quotes because that was the only one I found to be a problem.  The program also doesn't assume any misplaced commas or single quotes in other cells.
I assumed that the customer wanted all data stored as TEXT in SQL.
I assumed that the log file is run over time, so each time the program is run, it adds to the existing log file, instead of creating a new one, or overwriting the last one.
I also assumed that the program is meant to be run a few times, so it appends to the .csv file instead of overwriting it.
