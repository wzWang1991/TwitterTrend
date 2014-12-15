

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;

/**
 * Servlet implementation class getdata
 */
public class getdata extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getdata() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PropertiesCredentials propertiesCredentials = new PropertiesCredentials(Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties"));
		AmazonS3 s3Client = new AmazonS3Client(propertiesCredentials);
        S3Object s3Object = s3Client.getObject("edu.columbia.cloud.ww2339", "output/part-r-00000");
        InputStream inputStream = new BufferedInputStream(s3Object.getObjectContent());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Gson gson = new Gson();
        List<TrendData> trendData = new LinkedList<>();
        while (true) {
            String line = reader.readLine();
            if (line == null)
            	break;
            TrendData newData = gson.fromJson(line, TrendData.class);
            trendData.add(newData);
        }
        PrintWriter write = response.getWriter();
        response.setContentType("application/json");
        write.write(gson.toJson(trendData));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
