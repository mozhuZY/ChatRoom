package MainSystem;

import com.mozhu.net.Server;

public class Main {
	private static final  int port = 8088;
	
	public static void main(String[] args) {
		Server server;
		if(args.length > 0) {
			server  =new Server(Integer.valueOf(args[0]));
		}else {
			server = new Server(Main.port);
		}
		server.run();
	}
}
