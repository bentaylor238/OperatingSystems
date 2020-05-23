import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * @author Benjamin Taylor
 *
 * This class computes fibonacci numbers, factorials, and estimates the number e through the Taylor series
 */
public class Assign1 {
    /**
     * The main method first checks if there are the correct number of command line arguments and if so it runs through
     * every set of arguments and checks whether those are correct values for this program and runs if so.
     *
     * @param args These are the specified factorial, fibonacci, and e arguments to compute
     */
    public static void main(String[] args) {
        if (args.length == 0 || args.length % 2 == 1) {
            System.out.print("--- Assign 1 Help ---\n" +
                    "  -fib [n] : Compute the Fibonacci of [n]; valid range [0, 40]\n" +
                    "  -fac [n] : Compute the factorial of [n]; valid range, [0, 2147483647]\n" +
                    "  -e [n] : Compute the value of 'e' using [n] iterations; valid range [1, 2147483647]\n");
            return;
        }

        for (int i = 0; i < args.length; i+=2)  {
            // Checks if the command line argument is factorial and then catches if the user does not input a number to follow
            if (args[i].equals("-fac")) {
                try {
                    int fac = Integer.parseInt(args[i + 1]);
                    if (fac <= 2147483647 && fac >= 0) {
                        System.out.printf("Factorial of %d is %s\n", fac, factorial(fac).toString());
                    }
                    else {
                        throw new Exception();
                    }
                }
                catch (Exception e) {
                    System.out.println("-fac [n] : Valid factorial range is [0, 2147483647]");
                }
            }
            // Check if it's fibonacci and catch if a user doesn't input a number following
            else if (args[i].equals("-fib")) {
                try {
                    int fib = Integer.parseInt(args[i + 1]);
                    if (fib <= 40 && fib >= 0) {
                        System.out.printf("Fibonacci of %d is %d\n", fib, fibonacci(fib));
                    }
                    else {
                        throw new Exception();
                    }
                }
                catch (Exception e) {
                    System.out.println("-fib [n] : Valid fibonacci range is [0, 40]");
                }
            }
            // Checks if it's e and catches if a number is not following
            else if (args[i].equals("-e")) {
                try {
                    int iterations = Integer.parseInt(args[i + 1]);
                    if (iterations <= 2147483647 && iterations >= 1) {
                        System.out.printf("Value of e using %d iterations is %s\n", iterations, e(iterations).toString());
                    }
                    else {
                        throw new Exception();
                    }
                }
                catch (Exception e) {
                    System.out.println("-e [n] : Valid e iterations range is [1, 2147483647]");
                }
            }
            // If none of the above then it's an unknown command
            else {
                System.out.printf("Unknown command line argument: %s\n", args[i]);
            }
        }
    }

    /**
     * This is a recursive method to calculate the factorial of a number. Calls itself until it gets to 0 and multiplies
     * each time. It uses BigInteger to maintain precision so the number can continue growing without limits of int size
     *
     * @param num the number factorial to be calculated
     * @return It returns the calculated factorial
     */
    private static BigInteger factorial(int num) {
        if (num == 0) {
            return BigInteger.ONE;
        }
        else {
            return new BigInteger(String.valueOf(num)).multiply(factorial(num - 1));
        }
    }

    /**
     * Finds the fibonacci of the given number by adding the numbers together until it hits the designated number
     *
     * @param num what number in the fibonacci sequence is wanted
     * @return Returns the fibonacci number
     */
    private static int fibonacci(int num) {
        int firstNumber = 1;
        int secondNumber = 1;
        int temp;
        for (int i = 2; i <= num; i++) {
            temp = secondNumber;
            secondNumber = firstNumber + secondNumber;
            firstNumber = temp;
        }
        return secondNumber;
    }

    /**
     * Calculates the value of e using the Taylor series. This is done by applying the Taylor series and substituting 1
     * in for x in the equation. BigDecimal is used to maintain precision as the numbers get increasingly small.
     *
     * @param iterations This is how many iterations the Taylor series is to go through
     * @return the bigDecimal found of the value of e
     */
    private static BigDecimal e(int iterations) {
        BigDecimal e = new BigDecimal(0);
        BigDecimal numerator = new BigDecimal(BigInteger.ONE);
        for (int i = 0; i < iterations; i++) {
            BigDecimal denominator = new BigDecimal(factorial(i));
            e = e.add(numerator.divide(denominator, 16, RoundingMode.HALF_UP));
        }
        e = e.setScale(16, RoundingMode.HALF_UP);
        return e;
    }
}
