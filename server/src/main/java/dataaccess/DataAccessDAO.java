package dataaccess;

public interface DataAccessDAO {
    // Interface left empty because methods in this class are dependent on the AuthDAO, GameDAO, and UserDAO.
    // Interfaces provide no way require these subclasses to be implemented. If there was a way to do so, I would
    // include them here.

    // This interface exists to allow "one line of code" change in the server file to toggle how the app
    // interacts with data.
    //DataAccessDAO = memoryDAO or DBDAO
}
