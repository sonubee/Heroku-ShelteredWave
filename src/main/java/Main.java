import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;
import java.util.Random;
import java.math.BigDecimal;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationErrors;
import com.braintreegateway.ClientTokenRequest;

import com.heroku.sdk.jdbc.DatabaseUrl;

public class Main {
	
	public static int i=0;
	
	public static String token;
    
 	private static BraintreeGateway gateway = new BraintreeGateway(
			  Environment.PRODUCTION,
			  "69ppkf6h8fqh9cxb",
			  "svp64bn3p56344yj",
			  "fa458ae542e48d150ed2d456d28f16b7"
			);
   

  public static void main(String[] args) {
	
    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");

  		get("/hello", (request, response) ->{
        	return "Hello World!";
        });
        
        get("/client_token", (request, response) ->{
        	return "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIzN2Y4Zjg2ZWFhMjQxZWNjZGQ4MTg4ODFkNWQ5ZTUyNzlmNTg4OGEyNmM5MGM1N2QzMDc2ZmQzNDIxY2YwZGNlfGNyZWF0ZWRfYXQ9MjAxNi0wMi0yMFQyMzoxNDowNC4zMDgyMzkxNzUrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIn0sInRocmVlRFNlY3VyZUVuYWJsZWQiOnRydWUsInRocmVlRFNlY3VyZSI6eyJsb29rdXBVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi90aHJlZV9kX3NlY3VyZS9sb29rdXAifSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=";
        
        });
        
        get("/client_token_real", (request, response) ->{
        	return "production_x75kb8hy_69ppkf6h8fqh9cxb";
        });
        
        get("/client_token_test", (request, response) ->{
        	
        	ClientTokenRequest clientTokenRequest = new ClientTokenRequest()
            .customerId("27073348");
        	
        	String clientToken = gateway.clientToken().generate(clientTokenRequest);
        	System.out.println("Client Token: " + clientToken);
        	
        	Random r = new Random();
        	int i1 = r.nextInt(1000 - 100) + 100;
        	i++;
        	
        	//return "Random Number: " + i1 + " & Request Number: " + i;
        	return clientToken;
        });
        
        post("/checkout2", (req, res) -> {
        	String nonce = req.queryParams("payment_method_nonce");
        	System.out.println("Nonce: " + nonce);
        	return nonce;
        });
        
        post("/checkout", (req, res) -> {
        	
        	String nonce = req.queryParams("payment_method_nonce");
        	
        	System.out.println("Nonce: " + nonce);
        	
        	TransactionRequest request = new TransactionRequest()
            .amount(new BigDecimal("1.00"))
            .paymentMethodNonce(nonce)
            //.merchantAccountId("JobsME_marketplace")
            .options()
              .submitForSettlement(true)
              .done();
       
        	System.out.println("After transaction");
			Result<Transaction> result = gateway.transaction().sale(request);
			
			System.out.println("1");
			String status = "";
			System.out.println("2");
		
			
			
			if (result.isSuccess() == true){
				System.out.println("Success ");
				Transaction transaction = result.getTarget();
				transaction.getStatus();
				System.out.println("Status: " + transaction.getStatus());
			}
			System.out.println("3");
			if (result.isSuccess() == false)
			{
				
				System.out.println("Fail");
				Transaction transaction = result.getTransaction();
/*
				transaction.getStatus();
				// Transaction.Status.PROCESSOR_DECLINED

				transaction.getProcessorResponseCode();
				// e.g. "2001"

				transaction.getProcessorResponseText();
				// e.g. "Insufficient Funds"
				System.out.println("Status: " + transaction.getStatus());
				System.out.println("Response Code: " + transaction.getProcessorResponseCode());
				System.out.println("Response Text: " + transaction.getProcessorResponseText());
				*/
			}

			System.out.println("4");
				return result.isSuccess()+ "!222";
   
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
	
	   
        before ((request, response) -> {
        	System.out.println("Request IP: " + request.ip());
        	System.out.println("Request Verb: " + request.requestMethod());
        	System.out.println("Request Agent: " + request.userAgent());
        });

  }

}
