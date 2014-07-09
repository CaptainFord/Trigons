package trigons.swing;

public class TrigonUtil {
	public static boolean doesTrianglePointUp(int x, int y){
		return (x + y) % 2 == 0;
	}
}
