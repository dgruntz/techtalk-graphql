package ch.fhnw.ds.graphql.shop.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Client2 {
	
	public static void main(String[] args) throws Exception {
		var client = HttpClient
				.newHttpClient()
				.newWebSocketBuilder()
				.subprotocols("graphql-transport-ws")
				.buildAsync(URI.create("ws://localhost:8080/graphql"), new WebSocketClient())
				.thenCompose(ws -> ws.sendText("""
						{
						   "type"    : "connection_init",
						   "payload" : {}
						}""", true))
				.thenCompose(ws -> ws.sendText("""
						{
						   "id"        : "1",
						   "type"      : "subscribe",
						   "payload"   : {
						      "query"     : "subscription {\\n productAdvertisement {\\n id \\n }\\n}"
						   }
						}""", true))
				.join();

		synchronized(client) {
			client.wait();
		}
	}
	
	private static class WebSocketClient implements WebSocket.Listener {
		
		@Override
		public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
			System.err.printf("onText(%s, %b)%n", data, last);
			webSocket.request(1);
			
			JsonObject response = JsonParser.parseString(data.toString()).getAsJsonObject();
			
			String type = response.get("type").getAsString();
			if(type.equals("next")) {
				var result = response.getAsJsonObject("payload").getAsJsonObject("data");
				System.out.println(result);
			}
			return null;
		}
		
		@Override
		public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
			System.err.printf("onClose(%d, %s)%n", statusCode, reason);
			return null;
		}
	};

}
