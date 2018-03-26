

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;
import com.test.User;

/**
 * Servlet implementation class EmployeeLogin
 */
@WebServlet("/EmployeeLogin")
public class EmployeeLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeeLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	//	String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//		System.out.println("gRecapthcaResponse=" + gRecaptchaResponse);
		JsonObject responseJsonObject = new JsonObject();

//		boolean valid = VerifyUtils.verify(gRecaptchaResponse);
//		
//		if(!valid)
//		{
//			responseJsonObject.addProperty("status", "fail");
//			responseJsonObject.addProperty("message", "Recaptcha is wrong.");
//			response.getWriter().write(responseJsonObject.toString());
//			return;
//		}
		
        String email = request.getParameter("username");
		String password = request.getParameter("password");
        
        try {
        	Context initCtx = new InitialContext();
            if (initCtx == null)
                response.getWriter().println("initCtx is NULL");

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
            	response.getWriter().println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");

            if (ds == null)
            	response.getWriter().println("ds is null.");

            Connection dbcon = ds.getConnection();
            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement("Select email, password,fullname from employees where email = ? and password = ?");
            statement.setString(1, email);
            statement.setString(2, password);

            // Perform the query
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
    			// login success:			
    			//JsonObject responseJsonObject = new JsonObject();
    			request.getSession().setAttribute("user", new User(email, "", rs.getString("fullname")));

    			responseJsonObject.addProperty("status", "success");
    			responseJsonObject.addProperty("message", "success");
    			
    			response.getWriter().write(responseJsonObject.toString());
            }
            
            else
            {
            	// login fail
    		//	request.getSession().setAttribute("user", new User(email, "", rs.getString("fullname")));

    			//JsonObject responseJsonObject = new JsonObject();
    			responseJsonObject.addProperty("status", "fail");
    			
    			PreparedStatement statement1 = dbcon.prepareStatement("Select email from employees where email = ?");
                statement1.setString(1, email);
                ResultSet rs1 = statement1.executeQuery();
                if (rs1.next())
                {
        			responseJsonObject.addProperty("message", "The password " + password + " is incorrect, please try again.");
        			response.getWriter().write(responseJsonObject.toString());
        			return;
                }
                
                PreparedStatement statement2 = dbcon.prepareStatement("Select password from employees where password = ?");
                statement2.setString(1, password);
                ResultSet rs2 = statement2.executeQuery();
                if (rs2.next())
                {
        			responseJsonObject.addProperty("message", "The username " + email + " is incorrect, please try again.");
        			response.getWriter().write(responseJsonObject.toString());
        			return;
                }
                
                responseJsonObject.addProperty("message", "The username " + email + " and password " + password + " are both incorrect, please try again.");
    			response.getWriter().write(responseJsonObject.toString());
            }
            dbcon.close();
            rs.close();
         
        }
        catch(Exception e){
        	response.getWriter().print("error");
        }
       
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}