import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class GetObjectTypesServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		System.out.println("Inside GetObjectTypesServlet::");
		
		String respositoryId = req.getParameter("repositoryId");
		System.out.println("sampleEDService.GetObjectTypesServlet : repositoryId=" + respositoryId);

		InputStream objectTypesStream = this.getClass().getResourceAsStream("ObjectTypes.json");
		JSONArray jsonResponse = JSONArray.parse(objectTypesStream);
		PrintWriter writer = resp.getWriter();
		jsonResponse.serialize(writer);
		
	}

}
