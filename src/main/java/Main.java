import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import java.math.BigDecimal;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;

import com.heroku.sdk.jdbc.DatabaseUrl;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;

public class Main {
	/*
	private static BraintreeGateway gateway = new BraintreeGateway(
		Environment.PRODUCTION,
		"69ppkf6h8fqh9cxb",
		"svp64bn3p56344yj",
		"fa458ae542e48d150ed2d456d28f16b7");
*/
	private static BraintreeGateway gateway;


	public static void main(String[] args) {
	
		port(Integer.valueOf(System.getenv("PORT")));
		staticFileLocation("/public");
	get("/hello", (request, response) ->{
	       	return "Hello World!";
	       });
	             		
		post("/postIOSToken" , (req, res) -> {
			String token = req.queryParams("token");
			String UDID = req.queryParams("UDID");
			System.out.println("Token: " + token);
			System.out.println("UDID: " + UDID);
			return token;
		});
	        
        post("/checkout", (req, res) -> {
        	
        	String nonce = req.queryParams("payment_method_nonce");
        	String email = req.queryParams("email");
			String amount = req.queryParams("amount");
			
			Double amountDouble = Double.valueOf(amount);
        	
			System.out.println("-----------------------Purchase-----------------------");
        	System.out.println("Nonce: " + nonce);
        	System.out.println("Email: " + email);
			System.out.println("Amount: " + amountDouble);
        	
        	TransactionRequest request = new TransactionRequest()
            .amount(new BigDecimal(amount))
            .paymentMethodNonce(nonce)
            .customer()
              .email(email)
        	  .done()
            //.merchantAccountId("JobsME_marketplace")
            .options()
              .submitForSettlement(true)
              .done();
       
        	Result<Transaction> result = gateway.transaction().sale(request);
        	
        	String status = "";
        
        	if (result.isSuccess() == true){
        		Transaction transaction = result.getTarget();
        		transaction.getStatus();
        		System.out.println("***Payment Success --> Status: " + transaction.getStatus() + "***");
        	}
       
        	if (result.isSuccess() == false)
        	{
        		System.out.println("***PAYMENT FAILED***");
	            Transaction transaction = result.getTransaction();
	            
	            transaction.getProcessorResponseCode();
	            // e.g. "2001"
	            transaction.getProcessorResponseText();
	            // e.g. "Insufficient Funds"
	            System.out.println("Status: " + transaction.getStatus());
	            System.out.println("Response Code: " + transaction.getProcessorResponseCode());
	            System.out.println("Response Text: " + transaction.getProcessorResponseText());
        	}
        	
        	System.out.println("-----------------------End of Purchase-----------------------");

    		return result.isSuccess() + ": Payment Success!";
   
        });
        
        post("/sendPush", (req, res) -> {
        	
        	System.out.println("///////////////////////Message///////////////////////");
        	
			String to = req.queryParams("to");
			String json = req.queryParams("jsonString");
			String os = req.queryParams("os");
			
			System.out.println("To: " + to);
			System.out.println("JSON: " + json);
			System.out.println("OS: " + os);
			
			if (!(os.equals("android") || os.equals("ios"))){
				try {
		            // Prepare JSON containing the GCM message content. What to send and where to send.
		            JSONObject jGcmData = new JSONObject();
		            JSONArray regIds = new JSONArray();
		            JSONObject jsonMessage = new JSONObject();
		 
					regIds.put(to);
					jsonMessage.put("message", json);
		   
					jGcmData.put("registration_ids", regIds);
					jGcmData.put("data", jsonMessage);
					
		            // Create connection to send GCM Message request.
		            //URL url = new URL("https://android.googleapis.com/gcm/send");
					URL url = new URL("https://pushy.me/push?api_key=144f5ee08d5c0ead05247a144a916e9d035aec539fb4a9779beef8bb2ed79721");
		            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		            //conn.setRequestProperty("Authorization", "key=" + API_KEY);
		            conn.setRequestProperty("Content-Type", "application/json");
		            conn.setRequestMethod("POST");
		            conn.setDoOutput(true);

		            // Send GCM message content.
		            OutputStream outputStream = conn.getOutputStream();
		            outputStream.write(jGcmData.toString().getBytes());

		            // Read GCM response.
		            InputStream inputStream = conn.getInputStream();
		            String resp = IOUtils.toString(inputStream);
		            System.out.println(resp);
		            System.out.println("Check your device/emulator for notification or logcat for " +
		                    "confirmation of the receipt of the GCM message.");
		        } catch (IOException e) {
		           e.getMessage();
		            e.printStackTrace();
		        }
			}
			
			else if (os.equals("android")){
				  try {
			            // Prepare JSON containing the GCM message content. What to send and where to send.
			            JSONObject jGcmData = new JSONObject();
			            JSONArray regIds = new JSONArray();
			            JSONObject jsonMessage = new JSONObject();
			 
						regIds.put(to);
						jsonMessage.put("message", json);
			   
						jGcmData.put("registration_ids", regIds);
						jGcmData.put("data", jsonMessage);
						
			            // Create connection to send GCM Message request.
			            //URL url = new URL("https://android.googleapis.com/gcm/send");
						URL url = new URL("https://pushy.me/push?api_key=144f5ee08d5c0ead05247a144a916e9d035aec539fb4a9779beef8bb2ed79721");
			            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			            //conn.setRequestProperty("Authorization", "key=" + API_KEY);
			            conn.setRequestProperty("Content-Type", "application/json");
			            conn.setRequestMethod("POST");
			            conn.setDoOutput(true);

			            // Send GCM message content.
			            OutputStream outputStream = conn.getOutputStream();
			            outputStream.write(jGcmData.toString().getBytes());

			            // Read GCM response.
			            InputStream inputStream = conn.getInputStream();
			            String resp = IOUtils.toString(inputStream);
			            System.out.println(resp);
			            System.out.println("Check your device/emulator for notification or logcat for " +
			                    "confirmation of the receipt of the GCM message.");
			        } catch (IOException e) {
			           e.getMessage();
			            e.printStackTrace();
			        }
			}
			
			else if (os.equals("ios")){
				try {
					
					JSONObject jGcmData = new JSONObject();
					JSONObject notifications = new JSONObject();
					
					notifications.put("alert", "A broadcast message");
					
					jGcmData.put("audience", "all");
					jGcmData.put("device_types", "all");
					jGcmData.put("notification", notifications);
					
					URL url = new URL("https://go.urbanairship.com/api/push");
		            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		           
		            String userCredentials = "toDJ-fwhRj211DjZUWL80w:hooZT_vcStG4sWYYurKM5A";
		            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
		            
		            //conn.setRequestProperty  ("Authorization", "Basic " + encoding);
		            conn.setRequestProperty  ("Authorization", basicAuth);
		            //conn.setRequestProperty("Authorization", "key=" + "hooZT_vcStG4sWYYurKM5A");
		            conn.setRequestProperty("Accept", "application/vnd.urbanairship+json; version=3");
		            conn.setRequestProperty("Content-Type", "application/json");
		            conn.setRequestMethod("POST");
		            conn.setDoOutput(true);
		
		            // Send GCM message content.
		            OutputStream outputStream = conn.getOutputStream();
		            outputStream.write(jGcmData.toString().getBytes());
		
		            // Read GCM response.
		            InputStream inputStream = conn.getInputStream();
		            String resp = IOUtils.toString(inputStream);
		            System.out.println(resp);
		            System.out.println("Check your device/emulator for notification or logcat for " +
		                    "confirmation of the receipt of the GCM message.");
		        } catch (IOException e) {
		        	System.out.println("Error: " + e.getMessage());
		        }
			}
			
	   
     		System.out.println("///////////////////////End of Message///////////////////////");	       
	       	return json;
		});
			
			
			
			
			
			
			
			
			
			
		get("/", (request, response) -> {
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("message", "Hello World!");

			return new ModelAndView(attributes, "index.ftl");
		}, new FreeMarkerEngine());

		get("/db", (req, res) -> {
		  Connection connection = null;
		  Map<String, Object> attributes = new HashMap<>();
		  try {
			connection = DatabaseUrl.extract().getConnection();

			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
			stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
			ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

			ArrayList<String> output = new ArrayList<String>();
			while (rs.next()) {
			  output.add( "Read from DB: " + rs.getTimestamp("tick"));
			}

			attributes.put("results", output);
			return new ModelAndView(attributes, "db.ftl");
		  } catch (Exception e) {
			attributes.put("message", "There was an error: " + e);
			return new ModelAndView(attributes, "error.ftl");
		  } finally {
			if (connection != null) try{connection.close();} catch(SQLException e){}
		  }
		}, new FreeMarkerEngine());
	}

}
