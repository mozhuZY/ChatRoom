package MainSystem;

import com.mozhu.ui.LoginUi;

public class Main {

	public static void main(String[] args) {
		if(args.length == 0)
			new LoginUi("127.0.0.1", 8088);
		else
			new LoginUi(args[0], Integer.valueOf(args[1]));
	}
}
