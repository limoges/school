import java.util.Scanner;
import java.io.*;
import java.net.*;

class ClientShape {

  private static ClientShape instance = null;
  private Socket socket;
  private boolean sendRunning, receiveRunning;;
  private int sleep;
  private ShapeCanvas canvas;
  private boolean done;

  private PrintWriter out;
  private BufferedReader in;

  protected ClientShape() {
    sendRunning = false;
    receiveRunning = false;
  }

  private void finish() {
    try {
      if (done)
        socket.close();
      else
        done = true;
    }
    catch (Exception e) {
      System.out.println("exception while in finish()");
      e.printStackTrace();
    }
  }

  public static ClientShape getInstance() {
    if (instance == null) {
      instance = new ClientShape();
    }
    return instance;
  }

  public void init(String hostname, int port)
    throws UnknownHostException, ConnectException, IOException {

    this.socket = new Socket(hostname, port);
    //socket.getChannel().configureBlocking(true);
    out = new PrintWriter(socket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  public Shape getShape() throws IOException {
    final String command = "GET";
    String received = null;
    Shape s = null;

    // Send a request
    out.println(command);
    try {
      // Until we have received something useful
      do {
        received = null;
        // Read from the socket
        received = in.readLine();
        // Remove whitespace
        received = received.trim();
      }
      while (received == null || received.equals("commande>"));
      s = ShapeFactory.create(received);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return s;
  }

  public void setCanvas(ShapeCanvas canvas) {
    this.canvas = canvas;
  }

  public void send() {
    String command = "GET";
    out = null;

    try {
      out = new PrintWriter(socket.getOutputStream(), true);
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }

    sendRunning = true;
    done = false;
    while (sendRunning) {
      out.println(command);
      try {
        Thread.sleep(100);
      }
      catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }

    // We end the conversation with the server
    out.close();
    finish();
  }

  public void receive() throws Exception {
    String command = "";
    in = null;
    InputStream is = socket.getInputStream();

    //try {
      in = new BufferedReader(new InputStreamReader(is));
    //}
    /*catch (IOException ioe) {
      ioe.printStackTrace();
    }*/

    receiveRunning = true;
    done = false;
    int failedReads = 0;
    while (receiveRunning) {
      if (failedReads > 50) {
        stop();
        finish();
        throw new Exception("Server does not respond");
      }
      try {
        // hack to handle server disconnection
        command = in.readLine();

        if (command == null) {
          ++failedReads;
          continue;
        }
        command = command.trim();

        if (command.equals("commande>")) {
          command = null;
          continue;
        }
        executeCommand(command);
        // Not elegant but a way to let other thread execute?
        // TODO find a way to listen to the socket and only execute when
        // new data arrive through the socket
        Thread.sleep(1);
      }
      catch (SocketTimeoutException ste) {
        stop();
        finish();
        throw new Exception("Server has disconnected.");
      }
      catch (Exception e) {
        e.printStackTrace();
        receiveRunning = false;
      }
    }

    try {
      in.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    finish();
  }

  private void executeCommand(String command) {
    Shape s = ShapeFactory.create(command);

    if (s == null)
      return;

    canvas.addShape(s);
  }

  public void terminate() {
    out.println("END");
    out.flush();
  }

  public void stop() {
    terminate();
    sendRunning = false;
    receiveRunning = false;
  }

}
