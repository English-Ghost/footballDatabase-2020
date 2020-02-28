
import java.sql.*;
import javax.swing.*;
public class baseCode {
  public static void main(String[] args) {

    JFrame f=new JFrame("Sports Database");//creating instance of JFrame

    JButton b=new JButton("Connect to server");//creating instance of JButton
    b.setBounds(130,100,100, 40);//x axis, y axis, width, height

    f.add(b);//adding button in JFrame

    f.setSize(600,300);//400 width and 500 height
    f.setLayout(null);//using no layout managers
    f.setVisible(true);//making the frame visible

  }
}
