import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.UIManager.*;

public class App extends JFrame{
    JFrame frame;


    public Socket connect(String ip, int port, String name) throws Exception {
        Socket socket = new Socket(ip, port);
        return socket;
    }


    App(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Chat");

        JPanel menuCard = new JPanel(new CardLayout());

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setMaximumSize(new Dimension(300, 400));

        menu.add(Box.createRigidArea(new Dimension(260, 10)));
        JLabel menuLabel = new JLabel("<html>Welcome to chat app!<br/> Fill in your credentials!</html>");
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuLabel.setMaximumSize(new Dimension(260, 50));
        menuLabel.setForeground(Color.white);
        menu.add(menuLabel);
        menu.add(Box.createRigidArea(new Dimension(260, 10)));


        JPanel input = new JPanel();


        JTextField ipInput = new JTextField("0.0.0.0");
        ipInput.setPreferredSize(new Dimension(200, 25));

        JTextField portInput = new JTextField("00");
        portInput.setPreferredSize(new Dimension(60, 25));


        JTextField nameInput = new JTextField("Name");
        nameInput.setPreferredSize(new Dimension(265, 25));


        input.add(ipInput);
        input.add(portInput);
        input.add(nameInput);

        input.setMaximumSize(new Dimension(300, 75));
        input.setBackground(Color.decode("#2d0922"));

        menu.add(input);


        JButton connBtn = new JButton("Connect");
        connBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        connBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        connBtn.setMaximumSize(new Dimension(230, 40));
        connBtn.addActionListener(actionEvent -> {
            try {
                Socket socket = connect(ipInput.getText(), Integer.parseInt(portInput.getText()), nameInput.getText());
                socket.close();
            }
            catch (Exception e){
                JOptionPane.showMessageDialog(frame, "Unable to connect!");
            }
        });

        JButton exitBtn = new JButton("Exit");//create button
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        exitBtn.setMaximumSize(new Dimension(230, 40));
        exitBtn.addActionListener(actionEvent -> System.exit(0));

        menu.setBackground(Color.decode("#2d0922"));

        menu.add(connBtn);
        menu.add(Box.createRigidArea(new Dimension(260, 10)));
        menu.add(exitBtn);
        menuCard.add(menu);

        add(menuCard);
        setSize(400,450);
        setMinimumSize(new Dimension(300, 300));
        setVisible(true);

    }
    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    System.out.println("set");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new App();
    }
}

