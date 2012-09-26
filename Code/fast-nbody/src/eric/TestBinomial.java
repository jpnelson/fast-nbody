package eric;

public class TestBinomial {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("10 10 : 1 = "  + MP.binomial(10, 10));
		System.out.println("7 3 : 35 = "  + MP.binomial(7, 3));
		System.out.println("7 0 : 1 = "  + MP.binomial(7, 0));
		System.out.println("7 1 : 7 = "  + MP.binomial(7, 1));
		System.out.println("0 7 : 0 = "  + MP.binomial(0, 7));
		System.out.println("100 50 : big = "  + MP.binomial(100, 50));

	}

}
