import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.*;
import java.io.*;
import javax.swing.UIManager.*;

public class App extends JFrame{
    JFrame frame;
    Socket socket;
    DefaultListModel<String> stringModel;
    DefaultListModel<String> fileModel;
    ReadThread readThread;
    FileHandler fileHandler;
    PrintWriter printWriter;
    JList<String> chatList;
    JList<String> filesList;


    public Socket connect(String ip, int port) throws Exception {
        return new Socket(ip, port);
    }



    App(){

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Chat");

        //card layout panel to show either menu or chat
        JPanel menuCard = new JPanel(new CardLayout());

        //menu panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setMaximumSize(new Dimension(300, 400));

        menuPanel.add(Box.createRigidArea(new Dimension(260, 10)));
        JLabel menuLabel = new JLabel("<html>Welcome to chat app!<br/> Fill in your credentials!</html>");
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuLabel.setMaximumSize(new Dimension(260, 50));
        menuLabel.setForeground(Color.white);
        menuPanel.add(menuLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(260, 10)));

        //child panel of menu
        JPanel inputPanel = new JPanel();


        JTextField ipInput = new JTextField("127.0.0.1");
        ipInput.setPreferredSize(new Dimension(200, 25));

        JTextField portInput = new JTextField("3000");
        portInput.setPreferredSize(new Dimension(60, 25));


        JTextField nameInput = new JTextField("Name");
        nameInput.setPreferredSize(new Dimension(265, 25));


        inputPanel.add(ipInput);
        inputPanel.add(portInput);
        inputPanel.add(nameInput);

        inputPanel.setMaximumSize(new Dimension(300, 75));
        inputPanel.setBackground(Color.decode("#2d0922"));
        // end child panel
        menuPanel.add(inputPanel);

        JButton connBtn = new JButton("Connect");
        JButton downloadBtn = new JButton("Download");
        connBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        connBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        connBtn.setMaximumSize(new Dimension(230, 40));
        downloadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        downloadBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        downloadBtn.setMaximumSize(new Dimension(230, 40));

        connBtn.addActionListener(actionEvent -> {
            try {
                socket = connect(ipInput.getText(), Integer.parseInt(portInput.getText()));
                readThread = new ReadThread(socket, stringModel);
                readThread.start();
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println(nameInput.getText());
                CardLayout card = (CardLayout) menuCard.getLayout();
                card.show(menuCard, "chat");
            }
            catch (Exception e){
                JOptionPane.showMessageDialog(frame, "Unable to connect!");
                e.printStackTrace();
            }
        });

        downloadBtn.addActionListener(actionEvent -> {
            try {
                socket = connect(ipInput.getText(), Integer.parseInt(portInput.getText()));
                fileHandler = new FileHandler(socket, fileModel);
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println("download");
                fileHandler.readFiles();
                CardLayout card = (CardLayout) menuCard.getLayout();
                card.show(menuCard, "download");
            }
            catch (Exception e){
                JOptionPane.showMessageDialog(frame, "Unable to connect!");
                e.printStackTrace();
            }
        });

        JButton exitBtn = new JButton("Exit");//create button
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        exitBtn.setMaximumSize(new Dimension(230, 40));
        exitBtn.addActionListener(actionEvent -> System.exit(0));

        menuPanel.setBackground(Color.decode("#2d0922"));

        menuPanel.add(connBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(260, 10)));
        menuPanel.add(downloadBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(260, 10)));
        menuPanel.add(exitBtn);
        menuCard.add(menuPanel, "menu");
        //end of menu
        //chat panel
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new GridBagLayout());
        chatPanel.setBackground(Color.decode("#2d0922"));

        GridBagConstraints c = new GridBagConstraints();

        JButton backButton = new JButton("Back");
        backButton.addActionListener(actionEvent -> {
            try{
                readThread.stop();
                printWriter.println("exit");
                printWriter.close();
                socket.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            CardLayout card = (CardLayout) menuCard.getLayout();
            card.show(menuCard, "menu");
        });
        c.insets = new Insets(10, 8, 0, 5);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        chatPanel.add(backButton, c);

        stringModel = new DefaultListModel<>();
        chatList = new JList<>(stringModel);
        JScrollPane chatScroll = new JScrollPane(
                chatList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        CellRenderer cellRenderer = new CellRenderer(400);
        chatList.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                cellRenderer.setWitdh(chatList.getWidth()-150);
            }
        });
        chatList.setCellRenderer(cellRenderer);
        chatList.setBackground(Color.decode("#450b33"));
        chatList.setForeground(Color.decode("#20c20e"));
        chatList.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        chatScroll.setBorder(BorderFactory.createLineBorder(Color.black, 2));


        c.insets = new Insets(0, 8, 0, 8);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipady = 40;
        c.weightx = 0.5;
        c.gridwidth = 5;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        chatPanel.add(chatScroll, c);

        JTextPane chatInput = new JTextPane();
        c.insets = new Insets(5, 10, 8, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        c.ipady = 0;
        c.weighty = 0;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy =2;
        chatPanel.add(chatInput, c);

        JButton chatSend = new JButton("Send");
        c.insets = new Insets(3, 0, 6, 8);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = 2;
        chatSend.addActionListener(actionEvent -> {
            String msg = chatInput.getText();
            if (!msg.isEmpty()) {
                try{
                    printWriter.println(msg);
                    stringModel.addElement("You:" + msg);
                    chatInput.setText("");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
        chatPanel.add(chatSend, c);


        //end of chat
        menuCard.add(chatPanel, "chat");
        //download panel
        JPanel downloadPanel = new JPanel();
        downloadPanel.setLayout(new GridBagLayout());
        downloadPanel.setBackground(Color.decode("#2d0922"));

        c = new GridBagConstraints();

        JButton downloadBackButton = new JButton("Back");
        downloadBackButton.addActionListener(actionEvent -> {
            try{
                printWriter.println("exit");
                printWriter.close();
                socket.close();

            }
            catch (Exception e){
                e.printStackTrace();
            }finally {
                fileModel = new DefaultListModel<String>();
                filesList.setModel(fileModel);
                CardLayout card = (CardLayout) menuCard.getLayout();
                card.show(menuCard, "menu");
            }
        });
        c.insets = new Insets(10, 8, 0, 5);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        downloadPanel.add(downloadBackButton, c);

        fileModel = new DefaultListModel<>();
        filesList = new JList<>(fileModel);
        JScrollPane filesScroll = new JScrollPane(
                filesList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        CellRenderer downloadCellRenderer = new CellRenderer(400);
        filesList.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                downloadCellRenderer.setWitdh(filesList.getWidth()-150);
            }
        });
        filesList.setCellRenderer(downloadCellRenderer);
        filesList.setBackground(Color.decode("#450b33"));
        filesList.setForeground(Color.decode("#20c20e"));
        filesList.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        filesScroll.setBorder(BorderFactory.createLineBorder(Color.black, 2));


        c.insets = new Insets(0, 8, 0, 8);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipady = 40;
        c.weightx = 0.5;
        c.gridwidth = 5;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        downloadPanel.add(filesScroll, c);

        JTextPane fileInput = new JTextPane();
        c.insets = new Insets(5, 10, 8, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        c.ipady = 0;
        c.weighty = 0;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy =2;
        downloadPanel.add(fileInput, c);

        JButton fileSend = new JButton("Send");
        c.insets = new Insets(3, 0, 6, 8);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = 2;
        fileSend.addActionListener(actionEvent -> {
            String msg = fileInput.getText();
            if (!msg.isEmpty()) {
                try{
                    printWriter.println(msg);
                    fileInput.setText("");
                    fileHandler.readFile(msg);
                    JOptionPane.showMessageDialog(this,"Download complete");
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(frame, "Unable to download!");
                    e.printStackTrace();
                } finally {
                    fileModel = new DefaultListModel<String>();
                    filesList.setModel(fileModel);
                    CardLayout card = (CardLayout) menuCard.getLayout();
                    card.show(menuCard, "menu");
                }
            }

        });
        downloadPanel.add(fileSend, c);


        //end of download
        menuCard.add(downloadPanel, "download");
        add(menuCard);
        setSize(550,500);
        setMinimumSize(new Dimension(550, 500));
        setVisible(true);
    }
    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.getLookAndFeelDefaults().put("Scrollbar.minimumThumbSize", new Dimension(30, 30));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new App();
    }
}

