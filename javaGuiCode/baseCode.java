
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
public class baseCode {
  public static void main(String[] args) {

    JFrame f=new JFrame("Sports Database");//creating instance of JFrame

    final JTextField usernameBar = new JTextField();
    usernameBar.setBounds(50,50,150,20);

    final JTextField passwordBar = new JTextField();
    passwordBar.setBounds(50,100,150,20);

    JButton b=new JButton("Connect to server");//creating instance of JButton
    b.setBounds(50, 150, 200, 40);//x axis, y axis, width, height

    b.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        usernameBar.setText("Connected to the server");
        passwordBar.setText("Connected to the server");
      }

    });

    f.add(b);
    f.add(usernameBar);
    f.add(passwordBar);

    f.setSize(600,300);//400 width and 500 height
    f.setLayout(null);//using no layout managers
    f.setVisible(true);//making the frame visible

  }
}