import java.util.Properties;

/**
 * @author Benjamin Taylor A02021288
 *
 * This program takes command line input and if the commands are one of the specified then it prints specific details
 * about the category of the command
 */
public class Assign2 {
    /**
     * This takes the command line arguments and checks to see which command it is. If it's a recognized command then
     * it calls the specific method to print those details to the screen. It also creates the properties object once,
     * so that it will only need to be instantiated once if multiple commands are given that need properties.
     * @param args the command line arguments to specify what to print
     */
    public static void main(String[] args) {
        Properties properties = System.getProperties();
        for (String arg : args) {
            if (arg.equals("-cpu")) {
                getCPU();
            }
            else if (arg.equals("-mem")) {
                getMemory();
            }
            else if (arg.equals("-dirs")) {
                getDirectory(properties);
            }
            else if (arg.equals("-os")) {
                getOS(properties);
            }
            else if (arg.equals("-java")) {
                getJava(properties);
            }
            else {
                continue;
            }
        }
    }

    /**
     * Prints the number of processors in the computer.
     */
    private static void getCPU() {
        System.out.printf("\n%-13s: %d\n", "Processors", Runtime.getRuntime().availableProcessors());
    }

    /**
     * Prints the free, total, and max memory.
     */
    private static void getMemory() {
        System.out.printf("\n%-13s: %,15d\n", "Free Memory", Runtime.getRuntime().freeMemory());
        System.out.printf("%-13s: %,15d\n", "Total Memory", Runtime.getRuntime().totalMemory());
        System.out.printf("%-13s: %,15d\n", "Max Memory", Runtime.getRuntime().maxMemory());
    }

    /**
     * Prints the working directory and user home directory found in the properties object
     * @param properties contains all the different needed properties
     */
    private static void getDirectory(Properties properties) {
        System.out.printf("\n%-20s: %s\n", "Working Directory", properties.getProperty("user.dir"));
        System.out.printf("%-20s: %s\n", "User Home Directory", properties.getProperty("user.home"));
    }

    /**
     * Prints the Operating System name and version
     * @param properties contains all the different needed properties
     */
    private static void getOS(Properties properties) {
        System.out.printf("\n%-20s: %s\n", "OS Name", properties.getProperty("os.name"));
        System.out.printf("%-20s: %s\n", "OS Version", properties.getProperty("os.version"));
    }

    /**
     * Prints details about the current running java, such as the vendor, runtime name, version, vm version/name
     * @param properties contains all the different needed properties
     */
    private static void getJava(Properties properties) {
        System.out.printf("\n%-20s: %s\n", "Java Vendor", properties.getProperty("java.vendor"));
        System.out.printf("%-20s: %s\n", "Java Runtime Name", properties.getProperty("java.runtime.name"));
        System.out.printf("%-20s: %s\n", "Java Version", properties.getProperty("java.version"));
        System.out.printf("%-20s: %s\n", "Java VM Version", properties.getProperty("java.vm.version"));
        System.out.printf("%-20s: %s\n", "Java VM Name", properties.getProperty("java.vm.name"));
    }
}
