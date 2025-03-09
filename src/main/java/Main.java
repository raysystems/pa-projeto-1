public class Main {
    public static void main(String[] args) {
        MainHTTPServerThread s = new MainHTTPServerThread(8888);
        s.start();
        try {
            s.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
