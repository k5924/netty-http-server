import io.netty.handler.logging.LogLevel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    final var server = new Server(4221);
      try {
          server.start();
          Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
      } catch (final Exception e) {
          System.err.println("Failed to start server due to: " + e.getMessage());
         server.stop();
      }
  }
}
