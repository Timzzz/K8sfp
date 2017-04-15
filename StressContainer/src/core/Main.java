package core;

public class Main {
	
	public static void main(String[] args) {
		try {
			System.out.println("StressContainer started...");
			new Main().main();
		} catch (Exception e) {
			System.out.println("StressContainer crashed.");
			e.printStackTrace();
		}
	}
	
	public void main() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			runMethod();
		}
	}
	
	public void runMethod() {
		// empty
	}
	
}
