package ms3;


import java.sql.*;

/*
 * A class meant to handle creation, opening, modification, and closing a db.
 */

/**
 *
 * @author Stephen Petersen
 */
public class DatabaseHandler {
    //createDB should only be done if the db doesn't exist
    // createTable if the table doesn't already exist
    // insert data
    
    public DatabaseHandler() {    
    }


        
    void OpenDB(String path, String table){
        Connection c = null;
        Statement stmt = null;
    
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path);
            DatabaseMetaData metaData = c.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, table, null);

            if(!resultSet.next()){

           // System.out.println("Opened database.");
            stmt = c.createStatement();
            String sql = "CREATE TABLE " + table
                   + " (A        TEXT,"
                   + " B        TEXT,"
                   + " C        TEXT,"
                   + " D        TEXT," 
                   + " E        TEXT,"
                   + " F        TEXT,"
                   + " G        TEXT,"
                   + " H        TEXT,"
                   + " I        TEXT,"
                   + " J        TEXT);";
            stmt. executeUpdate(sql);
            stmt.close();
            }
            resultSet.close();
            c.close();
        } catch ( Exception e ){
            System.err.println(e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        //System.out.println("Opened database successfully");
    }



    public void insert(String[] row, String path, String table){

        
        Connection c = null;
        Statement stmt = null;
    
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path);
            c.setAutoCommit(false);

            stmt = c.createStatement();
            String sql = "INSERT INTO " + table + " (A,B,C,D,E,F,G,H,I,J) "
                +   "VALUES ('" + row[0] + "', "
                + "'" + row[1] + "', "
                + "'" + row[2] + "', "
                + "'" + row[3] + "', "
                + "'" + row[4] + "', "
                + "'" + row[5] + "', "
                + "'" + row[6] + "', "
                + "'" + row[7] + "', "
                + "'" + row[8] + "', "
                + "'" + row[9] + "');";
                //"INSERT INTO CVS_INPUT (A,B,C,D,E,F,G,H,I,J) VALUES('row[0]', 'row[1]', 'row[2]' );";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ){
            System.err.println(e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}
