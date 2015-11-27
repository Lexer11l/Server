/**
 * Created by Kirill on 27.11.2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.pi4j.component.servo.ServoDriver;
import com.pi4j.component.servo.ServoProvider;
import com.pi4j.component.servo.impl.RPIServoBlasterProvider;
import com.pi4j.io.gpio.*;


public class ServerPi {
    final GpioController gpio = GpioFactory.getInstance();
    final GpioPinDigitalOutput LED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, PinState.LOW);
    final GpioPinDigitalInput button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
    private List<Connection> connections =
            Collections.synchronizedList(new ArrayList<Connection>());
    private ServerSocket server;

    public static void main(String[] args) {
        new ServerPi();
    }

    public ServerPi() {
        try {
            server = new ServerSocket(8283);
            while (true) {
                Socket socket = server.accept();
                Connection con = new Connection(socket);
                connections.add(con);
                con.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Connection extends Thread {
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;

        private String name = "";

        public Connection(Socket socket) {
            this.socket = socket;

            try {
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run() {
            // final GpioPinDigitalOutput servo = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, PinState.LOW);
            /*ServoProvider servoProvider = null;
            ServoDriver servo7 = null;
            try {
                servoProvider = new RPIServoBlasterProvider();
                servo7 = servoProvider.getServoDriver(servoProvider.getDefinedServoPins().get(7));
            } catch (IOException e) {
                e.printStackTrace();
            }*/


            try {
                String str = "";
                while (true) {
                    str = in.readLine();
                    // Отправляем всем клиентам очередное сообщение


                    // System.out.println(str);
                    switch (str){
                        case "light":{
                            LED.high();
                            System.out.println("light");

                            break;
                        }
                        case "dark":{
                            LED.low();
                            System.out.println("dark");

                            break;
                        }
                        case "open":{
                            // servo7.setServoPulseWidth(5000);
                            System.out.println("open");
                            break;
                        }
                        case "close":{
                            //    servo7.setServoPulseWidth(0);
                            System.out.println("open");
                            break;
                        }
                        case "state":{
                            //      iter.next().out.println(button.isHigh()+"");
                            break;
                        }
                        default: {


                        }}}


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }



        public void close() {
            try {
                in.close();
                out.close();
                socket.close();

                connections.remove(this);
                if (connections.size() == 0) {
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("Close thread");
            }
        }
    }



}
