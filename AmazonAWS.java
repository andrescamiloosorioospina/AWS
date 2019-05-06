package com.email;

import java.util.HashMap;
import javax.jms.JMSException;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.util.Platform;
import com.fasterxml.jackson.core.JsonProcessingException;

public class AmazonAWS {

//==========================================================================================================================
//								V  A  R  I  A  B  L  E  S
//==========================================================================================================================	
	private static CreatePlatformEndpointResult ARN;
	private static AmazonSQS sqs;
	private static AmazonSNS  sns;
	private static String platformEndpointArn;
	private static String nombre;
	private static String myQueueUrl;
	private static float temperatura;
	private static String mensaje= "Alerta la temperatura supero los 35°C"
			+ " Por favor revisa tu sensor, de lo contrario"
			+ " Contactanos, Muchas Gracias";
		
//==========================================================================================================================
//								CONEXION AWS Y A SQS
//==========================================================================================================================

	//Credenciales para la conexiòn 
	public static  void Conectarse() throws JMSException {
		SQSConnection connection = null;
		final AWSCredentials credentials = new BasicAWSCredentials("AKIAJROR5M2TAS6FA2JQ","gIddFR4+d3cFOu84u2nEts8Q/WP7xdReEdiWM4tL");

		connection = new 		SQSConnectionFactory.Builder(com.amazonaws.regions.Region.getRegion(Regions.SA_EAST_1)).build().createConnection(credentials);
		connection.start();
		System.out.print("Conectadooo");
		 
	        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
	        sqs = AmazonSQSClientBuilder.standard()
	                               .withCredentials(credentialsProvider)
	                               .withRegion(Regions.US_WEST_2)
	                               .build();
	}
	
//==========================================================================================================================
//							C R E A R 		A P P
//==========================================================================================================================	

//Crear aplicaciòn web con el fin de registrar los endpoint
	public static String crearAplicacion(String nombre){
		CreatePlatformApplicationRequest  createPlatformApplicationRequest= new CreatePlatformApplicationRequest();
		createPlatformApplicationRequest.setName(nombre);
		createPlatformApplicationRequest.setPlatform("GCM");
		CreatePlatformApplicationResult createPlatformApplicationResult= sns.createPlatformApplication(createPlatformApplicationRequest);
		platformEndpointArn = createPlatformApplicationResult.getPlatformApplicationArn();
		return platformEndpointArn;
	}
	
//==========================================================================================================================
//							C R E A R 		E N D    P O I N T
//==========================================================================================================================

//Para esto se tiene que crear un equipo terminal, al que se le enviarà la notificaciòn
	public static String crearEndPoint(String regId, String userData, String platformEndpointArnParam){
			CreatePlatformEndpointRequest  createPlatformEndpointRequest = new CreatePlatformEndpointRequest();
			createPlatformEndpointRequest.setCustomUserData(userData);
			createPlatformEndpointRequest.setPlatformApplicationArn(platformEndpointArnParam);
			createPlatformEndpointRequest.setToken(regId);	
			CreatePlatformEndpointResult createPlatformEndpointResult= sns.createPlatformEndpoint(createPlatformEndpointRequest);
		return createPlatformEndpointResult.getEndpointArn();
	}

	/*public boolean eliminarEndPoint(String arn) {
	try{
		DeleteEndpointRequest deleteEndpointRequest= new DeleteEndpointRequest(); 
		deleteEndpointRequest.setEndpointArn(arn);			
		sns.deleteEndpoint(deleteEndpointRequest);
		return true;
	} catch(Exception e){
		return false;
	}
	}*/
	
//==========================================================================================================================
//						Q	U 	E	U	E
//==========================================================================================================================
//Creaciòn de cola para el alamacenamiento de los mensajes SQS
	public static void crearCola(String name){
		// Create a queue
		HashMap<String, String> attributeParams = new HashMap<String, String>();
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(name);
        createQueueRequest.setAttributes(attributeParams);
        myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
	}
	
	public static String enviarMensajeCola(String Mensaje) {	
		sqs.sendMessage(new SendMessageRequest(myQueueUrl,Mensaje));
		return Mensaje;
	}

//==========================================================================================================================
//						E N V I A R   N O T I F I C A C I Ò N
//==========================================================================================================================
//Realizar el push notification al end-point
	public static void sendNotification(final Platform platform,
			  final String principal,
			  final String credential,
			  final String platformToken,
			  final String message) throws JsonProcessingException {

		//Create Platform Application. This corresponds to an app on a platform.
		CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(nombre, platform, principal);
		//Publish a push notification to an Endpoint.
		PublishResult publishResult = publish(ARN.getEndpointArn(), platform, message);
	}

//===============================================================================================================================================================
//							M		A		I		N
//===============================================================================================================================================================
	public static void main(String[] args) throws JMSException, JsonProcessingException {
		Conectarse();
		crearAplicacion("Name Aplication");
		
		if	(temperatura>=35.0) {
			crearEndPoint("sa-east-1","Hola","GCM");
			crearCola("New Queue");
			enviarMensajeCola(mensaje);
			sendNotification(null,"Main","AKIAJROR5M2TAS6FA2JQ","TOKENCEL",mensaje);
		}
	}
//================================================================================================================================================================

//================================================================================================================================================================
	
	

	
	




















	
	



	private static CreatePlatformApplicationResult createPlatformApplication(String endpointArn, Platform platform, String message) {
		// TODO Auto-generated method stub
		return null;
		
	}	private static PublishResult publish(String endpointArn, Platform platform, String message) {
		// TODO Auto-generated method stub
		return null;
	}
}

