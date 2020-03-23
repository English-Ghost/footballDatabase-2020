import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class secondGui extends JFrame {

  // all JFframe variables and other vraibles
  static JFrame choiceFrame;
  static String[] tableNames = {
                                "conferences",
                                "drive",
                                "foreigner",
                                "game",
                                "gamestatistics",
                                "kickoff",
                                "kickoffreturn",
                                "pass",
                                "play",
                                "player",
                                "playergamestatistics",
                                "playerseason",
                                "punt",
                                "puntreturn",
                                "receptions",
                                "rush",
                                "stadium",
                                "team",
                                "teamgamestatistics"
                              };

  static String[] tableConstraints = {
                                      "DISTINCT",
                                      "ORDER BY",
                                      "WHERE",
                                      "AND",
                                      "OR",
                                      "COUNT"
                                    };

  static String[] questionChoices = {
                                      "Question 1",
                                      "Question 2",
                                      "Question 3",
                                      "Question 4"
                                    };

  static String serverResponse = "NO CHANGES";
  static JPanel userEntryPan = new JPanel();
  static JLabel dropBoxLabel = new JLabel();
  static JComboBox<String> baseTable = new JComboBox<String>(tableNames);
  static String baseTableOption = tableNames[0];
  static JPanel joinPan = new JPanel();
  static JLabel checkBoxLabel = new JLabel();
  static JCheckBox confTableCheck = new JCheckBox(tableNames[0]);
  static boolean confTableFlag = false;
  static JCheckBox driveTableCheck = new JCheckBox(tableNames[1]);
  static boolean driveTableFlag = false;
  static JCheckBox foreTableCheck = new JCheckBox(tableNames[2]);
  static boolean foreTableFlag = false;
  static JCheckBox gameTableCheck = new JCheckBox(tableNames[3]);
  static boolean gameTableFlag = false;
  static JCheckBox gamestatTableCheck = new JCheckBox(tableNames[4]);
  static boolean gamestatTableFlag = false;
  static JCheckBox koTableCheck = new JCheckBox(tableNames[5]);
  static boolean koTableFlag = false;
  static JCheckBox korTableCheck = new JCheckBox(tableNames[6]);
  static boolean korTableFlag = false;
  static JCheckBox passTableCheck = new JCheckBox(tableNames[7]);
  static boolean passTableFlag = false;
  static JCheckBox playTableCheck = new JCheckBox(tableNames[8]);
  static boolean playTableFlag = false;
  static JCheckBox playerTableCheck = new JCheckBox(tableNames[9]);
  static boolean playerTableFlag = false;
  static JCheckBox pgsTableCheck = new JCheckBox(tableNames[10]);
  static boolean pgsTableFlag = false;
  static JCheckBox psTableCheck = new JCheckBox(tableNames[11]);
  static boolean psTableFlag = false;
  static JCheckBox puntTableCheck = new JCheckBox(tableNames[12]);
  static boolean puntTableFlag = false;
  static JCheckBox prTableCheck = new JCheckBox(tableNames[13]);
  static boolean prTableFlag = false;
  static JCheckBox recepTableCheck = new JCheckBox(tableNames[14]);
  static boolean recepTableFlag = false;
  static JCheckBox rushTableCheck = new JCheckBox(tableNames[15]);
  static boolean rushTableFlag = false;
  static JCheckBox stadTableCheck = new JCheckBox(tableNames[16]);
  static boolean stadTableFlag = false;
  static JCheckBox teamTableCheck = new JCheckBox(tableNames[17]);
  static boolean teamTableFlag = false;
  static JCheckBox tgsTableCheck = new JCheckBox(tableNames[18]);
  static boolean tgsTableFlag = false;
  static JCheckBox allTableCheck = new JCheckBox("All possible tables");
  static boolean allTableFlag = false;
  static JPanel tablePan = new JPanel();
  static JButton createTableBut = new JButton();
  static JPanel searchPan = new JPanel();
  static JLabel searchLabel = new JLabel();
  static JTextField searchText = new JTextField(30);
  static JLabel searchLabel2 = new JLabel();
  static JComboBox<String> constraintBox = new JComboBox<String>(tableConstraints);
  static String constraintBoxOption = tableConstraints[0];
  static JPanel requestPan = new JPanel();
  static JButton requestButton = new JButton();
  static JButton resetButton = new JButton();
  static JPanel dataRespPan = new JPanel();
  static JTextArea serverRespText = new JTextArea();
  static JButton sendToFileBut = new JButton();

  static JFrame loginFrame;

  static JFrame questFrame;


  protected static void popupLogin()
  {
    loginFrame = new JFrame("LOGIN");
    GridBagLayout gridLayout = new GridBagLayout();
    loginFrame.getContentPane().setBackground(new Color(0,0,0));
    loginFrame.getContentPane().setLayout(gridLayout);

    GridBagConstraints frameVals = new GridBagConstraints();
    frameVals.fill = GridBagConstraints.HORIZONTAL;
    frameVals.anchor = GridBagConstraints.WEST;
    frameVals.gridx = 0;
    frameVals.gridy = 0;
    frameVals.ipady = 10;
    frameVals.weightx = .5;

    GridBagConstraints panelVals = new GridBagConstraints();
    panelVals.anchor = GridBagConstraints.WEST;
    panelVals.gridx = 0;
    panelVals.gridy = 0;

    JPanel loginPanel = new JPanel();
    loginPanel.setLayout(new GridBagLayout());

    JLabel databaseLabel = new JLabel();
    databaseLabel.setText("DATABASE: ");
    panelVals.gridx = 0;
    panelVals.gridy = 0;
    loginPanel.add(databaseLabel, panelVals);

    JTextField databaseBar = new JTextField(30);
    databaseBar.setText("");
    panelVals.gridx = 1;
    panelVals.gridy = 0;
    loginPanel.add(databaseBar, panelVals);

    JLabel usernameLabel = new JLabel();
    usernameLabel.setText("USERNAME: ");
    panelVals.gridx = 0;
    panelVals.gridy = 1;
    loginPanel.add(usernameLabel, panelVals);

    JTextField usernameBar = new JTextField(30);
    usernameBar.setText("");
    panelVals.gridx = 1;
    panelVals.gridy = 1;
    loginPanel.add(usernameBar, panelVals);

    JLabel passwordLabel = new JLabel();
    passwordLabel.setText("PASSWORD: ");
    panelVals.gridx = 0;
    panelVals.gridy = 2;
    loginPanel.add(passwordLabel, panelVals);

    JPasswordField passwordBar = new JPasswordField(30);
    panelVals.gridx = 1;
    panelVals.gridy = 2;
    loginPanel.add(passwordBar, panelVals);

    JButton connectButtonDatabase = new JButton();
    connectButtonDatabase.setText("<html>CONNECT<br>TO<br>DATABASE</html>");
    connectButtonDatabase.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loginFrame.dispose();
        popupDatabaseWindow();
      }
    });
    panelVals.gridx = 1;
    panelVals.gridy = 3;

    loginPanel.add(connectButtonDatabase, panelVals);

    JButton connectButtonQuestion = new JButton();
    connectButtonQuestion.setText("<html>CONNECT<br>TO<br>QUESTIONS</html>");
    connectButtonQuestion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loginFrame.dispose();
        popupQuestionWindow();
      }
    });
    panelVals.gridx = 1;
    panelVals.gridy = 3;
    panelVals.anchor = GridBagConstraints.EAST;

    loginPanel.add(connectButtonQuestion, panelVals);

    panelVals.anchor = GridBagConstraints.WEST;

    loginFrame.add(loginPanel, frameVals);

    loginFrame.pack();
    loginFrame.setVisible(true);

  }

  protected static void popupQuestionWindow()
  {

    questFrame = new JFrame("Question Window");
    GridBagLayout gridLayout = new GridBagLayout();
    questFrame.getContentPane().setBackground(new Color(0,0,0));
    questFrame.getContentPane().setLayout(gridLayout);

    // layout design of frame
    GridBagConstraints frameVals = new GridBagConstraints();
    frameVals.fill = GridBagConstraints.HORIZONTAL;
    frameVals.anchor = GridBagConstraints.WEST;
    frameVals.gridx = 0;
    frameVals.gridy = 0;
    frameVals.ipady = 10;
    frameVals.weightx = .5;

    // layout design of panels
    GridBagConstraints panelVals = new GridBagConstraints();
    panelVals.anchor = GridBagConstraints.WEST;
    panelVals.gridx = 0;
    panelVals.gridy = 0;

    JPanel questionPanel = new JPanel();
    questionPanel.setLayout(new GridBagLayout());

    JLabel questionOneTeamOneLabel = new JLabel();
    questionOneTeamOneLabel.setText("Team 1:");
    panelVals.gridx = 1;
    panelVals.gridy = 0;
    questionPanel.add(questionOneTeamOneLabel, panelVals);

    JLabel questionOneTeamTwoLabel = new JLabel();
    questionOneTeamTwoLabel.setText("Team 2:");
    panelVals.gridx = 2;
    panelVals.gridy = 0;
    questionPanel.add(questionOneTeamTwoLabel, panelVals);

    JLabel questionOneVicChainLabel = new JLabel();
    questionOneVicChainLabel.setText("Max victory chain length:");
    panelVals.gridx = 3;
    panelVals.gridy = 0;
    questionPanel.add(questionOneVicChainLabel, panelVals);

    JLabel questionOneLabel = new JLabel();
    questionOneLabel.setText("Given 2 teams, create a victory chain");
    panelVals.gridx = 0;
    panelVals.gridy = 1;
    questionPanel.add(questionOneLabel, panelVals);

    JTextField questionOneTeamOne = new JTextField(20);
    questionOneTeamOne.setText("");
    panelVals.gridx = 1;
    panelVals.gridy = 1;
    questionPanel.add(questionOneTeamOne, panelVals);

    JTextField questionOneTeamTwo = new JTextField(20);
    questionOneTeamTwo.setText("");
    panelVals.gridx = 2;
    panelVals.gridy = 1;
    questionPanel.add(questionOneTeamTwo, panelVals);

    JTextField questionOneVicChain = new JTextField(20);
    questionOneVicChain.setText("");
    panelVals.gridx = 3;
    panelVals.gridy = 1;
    questionPanel.add(questionOneVicChain, panelVals);

    JLabel questionTwoPlayerOneLabel = new JLabel();
    questionTwoPlayerOneLabel.setText("Player 1:");
    panelVals.gridx = 1;
    panelVals.gridy = 2;
    questionPanel.add(questionTwoPlayerOneLabel, panelVals);

    JLabel questionTwoPlayerTwoLabel = new JLabel();
    questionTwoPlayerTwoLabel.setText("Player 2:");
    panelVals.gridx = 2;
    panelVals.gridy = 2;
    questionPanel.add(questionTwoPlayerTwoLabel, panelVals);

    JLabel questionTwoLabel = new JLabel();
    questionTwoLabel.setText("What is the shortest chain between 2 players");
    panelVals.gridx = 0;
    panelVals.gridy = 3;
    questionPanel.add(questionTwoLabel, panelVals);

    JTextField questionTwoPlayerOne = new JTextField(20);
    questionTwoPlayerOne.setText("");
    panelVals.gridx = 1;
    panelVals.gridy = 3;
    questionPanel.add(questionTwoPlayerOne, panelVals);

    JTextField questionTwoPlayerTwo = new JTextField(20);
    questionTwoPlayerTwo.setText("");
    panelVals.gridx = 2;
    panelVals.gridy = 3;
    questionPanel.add(questionTwoPlayerTwo, panelVals);

    JLabel questionThreeTeamLabel = new JLabel();
    questionThreeTeamLabel.setText("Team:");
    panelVals.gridx = 1;
    panelVals.gridy = 4;
    questionPanel.add(questionThreeTeamLabel, panelVals);

    JLabel questionThreeLabel = new JLabel();
    questionThreeLabel.setText("Rushing yards against a given team");
    panelVals.gridx = 0;
    panelVals.gridy = 5;
    questionPanel.add(questionThreeLabel, panelVals);

    JTextField questionThreeTeam = new JTextField(20);
    questionThreeTeam.setText("");
    panelVals.gridx = 1;
    panelVals.gridy = 5;
    questionPanel.add(questionThreeTeam, panelVals);

    JLabel questionFourConfLabel = new JLabel();
    questionFourConfLabel.setText("Conference:");
    panelVals.gridx = 1;
    panelVals.gridy = 6;
    questionPanel.add(questionFourConfLabel, panelVals);

    JLabel questionFourLabel = new JLabel();
    questionFourLabel.setText("Average home field advantage of teams in a conference");
    panelVals.gridx = 0;
    panelVals.gridy = 7;
    questionPanel.add(questionFourLabel, panelVals);

    JTextField questionFourConf = new JTextField(20);
    questionFourConf.setText("");
    panelVals.gridx = 1;
    panelVals.gridy = 7;
    questionPanel.add(questionFourConf, panelVals);

    questFrame.add(questionPanel, frameVals);

    JPanel questionChoicePanel = new JPanel();
    questionChoicePanel.setLayout(new GridBagLayout());

    JComboBox<String> questChoice = new JComboBox<String>(questionChoices);
    questChoice.setSelectedIndex(0);
    panelVals.gridx = 0;
    panelVals.gridy = 0;
    questionChoicePanel.add(questChoice, panelVals);

    JButton questChoiceButton = new JButton();
    questChoiceButton.setText("<html>SELECT<br>QUESTION</html>");
    questChoiceButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      }
    });
    panelVals.gridx = 1;
    panelVals.gridy = 0;

    questionChoicePanel.add(questChoiceButton, panelVals);

    frameVals.gridx = 0;
    frameVals.gridy = 1;
    questFrame.add(questionChoicePanel, frameVals);

    serverRespText.append(serverResponse);
    serverRespText.setRows(4);
    serverRespText.setColumns(30);
    panelVals.gridx = 0;
    panelVals.gridy = 0;
    dataRespPan.add(serverRespText, panelVals);

    
    // button to send output to a file
    sendToFileBut.setText("<html> SEND <br> TO <br> FILE </html>");
    sendToFileBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        serverResponse = "send to file button hit";
        serverRespText.setText(serverResponse);
      }
    });
    panelVals.gridx = 1;
    panelVals.gridy = 0;
    dataRespPan.add(sendToFileBut, panelVals);

    frameVals.gridy = 2;
    questFrame.add(dataRespPan, frameVals);

    questFrame.pack();
    questFrame.setVisible(true);
  }

  // creates the popup for the database window
  protected static void popupDatabaseWindow()
  {

    // Frame creation and layout
    choiceFrame = new JFrame("Database window");
    GridBagLayout gridLayout = new GridBagLayout();
    choiceFrame.getContentPane().setBackground(new Color(0,0,0));
    choiceFrame.getContentPane().setLayout(gridLayout);

    // layout design of frame
    GridBagConstraints frameVals = new GridBagConstraints();
    frameVals.fill = GridBagConstraints.HORIZONTAL;
    frameVals.anchor = GridBagConstraints.WEST;
    frameVals.gridx = 0;
    frameVals.gridy = 0;
    frameVals.ipady = 10;
    frameVals.weightx = .5;

    // layout design of panels
    GridBagConstraints panelVals = new GridBagConstraints();
    panelVals.anchor = GridBagConstraints.WEST;
    panelVals.gridx = 0;
    panelVals.gridy = 0;


    // set layout of first panel
    userEntryPan.setLayout(new GridBagLayout());

    // drop box label for the base table
    dropBoxLabel.setText("Please enter a base table to work with: ");
    panelVals.gridx = 0;
    panelVals.gridy = 0;
    userEntryPan.add(dropBoxLabel, panelVals);

    // drop box selection for base table
    baseTable.setSelectedIndex(0);
    baseTable.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        baseTableOption = (String)baseTable.getSelectedItem();
      }
    });
    panelVals.gridx = 1;
    panelVals.gridy = 0;
    userEntryPan.add(baseTable, panelVals);

    // input the first panel
    choiceFrame.add(userEntryPan, frameVals);

    // set layout of the second panel
    joinPan.setLayout(new GridBagLayout());

    // check box labels
    checkBoxLabel.setText("Please Choose which tables to join");
    panelVals.gridx = 0;
    panelVals.gridy = 0;
    joinPan.add(checkBoxLabel, panelVals);

    // conference table check box items
    confTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == 1)
            {
              confTableFlag = true;
            }
            if(e.getStateChange() != 1)
            {
              confTableFlag = false;
            }
         }
      });
    panelVals.gridx = 0;
    panelVals.gridy = 1;
    joinPan.add(confTableCheck, panelVals);

    // drive table check box items
    driveTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             driveTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             driveTableFlag = false;
           }
         }
      });
    panelVals.gridx = 1;
    panelVals.gridy = 1;
    joinPan.add(driveTableCheck, panelVals);

    // foreigner table check box items
    foreTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             foreTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             foreTableFlag = false;
           }
         }
      });
    panelVals.gridx = 2;
    panelVals.gridy = 1;
    joinPan.add(foreTableCheck, panelVals);

    // game table check box items
    gameTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             gameTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             gameTableFlag = false;
           }
         }
      });
    panelVals.gridx = 3;
    panelVals.gridy = 1;
    joinPan.add(gameTableCheck, panelVals);

    // game statistic check box items
    gamestatTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             gamestatTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             gamestatTableFlag = false;
           }
         }
      });
    panelVals.gridx = 4;
    panelVals.gridy = 1;
    joinPan.add(gamestatTableCheck, panelVals);

    // kickoff table check box items
    koTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             koTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             koTableFlag = false;
           }
         }
      });
    panelVals.gridx = 0;
    panelVals.gridy = 2;
    joinPan.add(koTableCheck, panelVals);

    // kickoff return check box items
    korTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             korTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             korTableFlag = false;
           }
         }
      });
    panelVals.gridx = 1;
    panelVals.gridy = 2;
    joinPan.add(korTableCheck, panelVals);

    // pass table check box items
    passTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             passTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             passTableFlag = false;
           }
         }
      });
    panelVals.gridx = 2;
    panelVals.gridy = 2;
    joinPan.add(passTableCheck, panelVals);

    // play table check box items
    playTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             playTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             playTableFlag = false;
           }
         }
      });
    panelVals.gridx = 3;
    panelVals.gridy = 2;
    joinPan.add(playTableCheck, panelVals);

    // player table check box items
    playerTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             playerTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             playerTableFlag = false;
           }
         }
      });
    panelVals.gridx = 4;
    panelVals.gridy = 2;
    joinPan.add(playerTableCheck, panelVals);

    // player game statistics table check box items
    pgsTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             pgsTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             pgsTableFlag = false;
           }
         }
      });
    panelVals.gridx = 0;
    panelVals.gridy = 3;
    joinPan.add(pgsTableCheck, panelVals);

    // player statistics table check box items
    psTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             psTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             psTableFlag = false;
           }
         }
      });
    panelVals.gridx = 1;
    panelVals.gridy = 3;
    joinPan.add(psTableCheck, panelVals);

    // punt table punt check box items
    puntTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             puntTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             puntTableFlag = false;
           }
         }
      });
    panelVals.gridx = 2;
    panelVals.gridy = 3;
    joinPan.add(puntTableCheck, panelVals);

    // punt return table check box items
    prTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             prTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             prTableFlag = false;
           }
         }
      });
    panelVals.gridx = 3;
    panelVals.gridy = 3;
    joinPan.add(prTableCheck, panelVals);

    // reception table check box items
    recepTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             recepTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             recepTableFlag = false;
           }
         }
      });
    panelVals.gridx = 4;
    panelVals.gridy = 3;
    joinPan.add(recepTableCheck, panelVals);

    // rush table check box items
    rushTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             rushTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             rushTableFlag = false;
           }
         }
      });
    panelVals.gridx = 0;
    panelVals.gridy = 4;
    joinPan.add(rushTableCheck, panelVals);

    // stadium check box items
    stadTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             stadTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             stadTableFlag = false;
           }
         }
      });
    panelVals.gridx = 1;
    panelVals.gridy = 4;
    joinPan.add(stadTableCheck, panelVals);

    // team table check box items
    teamTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             teamTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             teamTableFlag = false;
           }
         }
      });
    panelVals.gridx = 2;
    panelVals.gridy = 4;
    joinPan.add(teamTableCheck, panelVals);

    // team game statistics check box items
    tgsTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             tgsTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             tgsTableFlag = false;
           }
         }
      });
    panelVals.gridx = 3;
    panelVals.gridy = 4;
    joinPan.add(tgsTableCheck, panelVals);

    // all table check box items
    allTableCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
           if(e.getStateChange() == 1)
           {
             allTableFlag = true;
           }
           if(e.getStateChange() != 1)
           {
             allTableFlag = false;
           }
         }
      });
    panelVals.gridx = 4;
    panelVals.gridy = 4;
    joinPan.add(allTableCheck, panelVals);

    // add the second panel to the frame
    frameVals.gridy = 1;
    choiceFrame.add(joinPan, frameVals);

    // table panel set up
    tablePan.setLayout(new GridBagLayout());

    // button for creation of join table
    createTableBut.setText("<html>CREATE<br>TABLE</html>");
    createTableBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        serverResponse = "CHANGE HAS BEEN MADE";
        serverRespText.setText(serverResponse);
      }
    });
    panelVals.gridx = 0;
    panelVals.gridy = 0;
    tablePan.add(createTableBut,panelVals);

    // adds the thrid panel to the frame
    frameVals.gridy = 2;
    choiceFrame.add(tablePan, frameVals);

    // search panel set up
    searchPan.setLayout(new GridBagLayout());

    // label for the search bar
    searchLabel.setText("What are you searching for?");
    panelVals.gridx = 0;
    panelVals.gridy = 0;
    searchPan.add(searchLabel, panelVals);

    // empty text box next to search bar
    searchText.setText("");
    panelVals.gridx = 1;
    panelVals.gridy = 0;
    searchPan.add(searchText, panelVals);

    // label for drop down box of constraints
    searchLabel2.setText("Add constraints for Search: ");
    panelVals.gridx = 0;
    panelVals.gridy = 1;
    searchPan.add(searchLabel2, panelVals);

    // drop down box of constraints
    constraintBox.setSelectedIndex(0);
    constraintBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        constraintBoxOption = (String)constraintBox.getSelectedItem();
      }
    });
    panelVals.gridx = 1;
    panelVals.gridx = 1;
    searchPan.add(constraintBox, panelVals);

    // add the fourth panel to the frame
    frameVals.gridy = 3;
    choiceFrame.add(searchPan, frameVals);

    // request panel set up
    requestPan.setLayout(new GridBagLayout());

    // button used for requesting data from database
    requestButton.setText("Request From Database");
    requestButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        serverResponse = "request button hit";
        serverRespText.setText(serverResponse);
      }
    });
    panelVals.gridx = 0;
    panelVals.gridy = 0;
    requestPan.add(requestButton, panelVals);

    resetButton.setText("Reset");
    resetButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        serverResponse = "request button hit";
        serverRespText.setText(serverResponse);
      }
    });
    panelVals.gridx = 1;
    panelVals.gridy = 0;
    requestPan.add(resetButton, panelVals);

    // add the fifth panel to the frame
    frameVals.gridy = 4;
    choiceFrame.add(requestPan, frameVals);

    // data response panel set up
    dataRespPan.setLayout(new GridBagLayout());

    // data response area, where the output is
    serverRespText.append(serverResponse);
    serverRespText.setRows(4);
    serverRespText.setColumns(30);
    panelVals.gridx = 0;
    panelVals.gridy = 0;
    dataRespPan.add(serverRespText, panelVals);

    // button to send output to a file
    sendToFileBut.setText("<html> SEND <br> TO <br> FILE </html>");
    sendToFileBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        serverResponse = "send to file button hit";
        serverRespText.setText(serverResponse);
      }
    });
    panelVals.gridx = 1;
    panelVals.gridy = 0;
    dataRespPan.add(sendToFileBut, panelVals);

    // add sixth panel to the frame
    frameVals.gridy = 5;
    choiceFrame.add(dataRespPan, frameVals);

    // pack frame and complete
    choiceFrame.setSize(900,600);
    choiceFrame.pack();
    choiceFrame.setVisible(true);

  }

  public static void main(String[] args)
  {
    popupLogin();
  }
}
