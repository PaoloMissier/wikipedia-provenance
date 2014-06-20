package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

import core.ReadXML;

import javax.swing.BoxLayout;

import org.codehaus.jettison.json.JSONException;

import javax.swing.JTable;

public class NewGUI {

	private JFrame frmCreatingAProvenance;
	private JTextField textArticleTitle;
	private JTextField textArticleRevlimit;
	private JTextField textArticleUclimit;
	private JTextField textArticleDepth;
	private JTextField textUserName;
	private JTextField textUserRevlimit;
	private JTextField textUserUclimit;
	private JTextField textUserDepth;
	private JTextField textFieldArticleName;
	private JTextField textFindLatestArticleTitle;
	private JTextField textFindNodeNumberByUser;
	private JTextField textFolderPathUser;
	private JTextField textContrisUserName;
	private JTextField textFolderPathTitle;
	private JTextField textArticleInfoByTitle;
	private JTextField textFolerPathVandalism;
	private JTextField textShowVandalismUserName;
	private JTextField textFolerPathVandalismByUser;
	private JTextField textUserVandalismByUser;
	private JTextField textFindRevisionInfoByRevid;
	private JTextField textFindLatestUserName;
	private JTextField textFindRevisionByTitleAndUserTitle;
	private JTextField textFindRevisionByTitleAndUserUser;
	private JTextField textFindUserByRevid;
	private JTextField textShowVandalismByArticle;
	private JTextField textFolerPathVandalismByTitle;
	private JTextField textTitleVandalismByTitle;
	private JTextField textGetContrisByUser;
	private JTextField textFieldGetInfoByArticle;
	private JTable tableGetContrisByUser;
	private JTable tableAllVandalism;
	private JTable tableVandalismByUser;
	private JTable tableVandalismByArticle;
	private JTable tableGetInfoByArticle;
	private JTable tableCountTitle;
	private JTable tableShowAllTitles;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewGUI window = new NewGUI();
					try{
					window.frmCreatingAProvenance.setVisible(true);
					}catch(Exception e){
						e.printStackTrace();
					}					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public NewGUI() {
		try{
			checkConnect.Connect.checkDatabaseIsRunning();
			initialize();
			}catch(Exception e){
				initializeCannotConnectNeo4j();
			}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmCreatingAProvenance = new JFrame();
		frmCreatingAProvenance.setTitle("Creating a provenance benchmark dataset out of Wikipedia history pages");
		frmCreatingAProvenance.setBounds(100, 100, 553, 682);
		frmCreatingAProvenance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		frmCreatingAProvenance.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel create = new JPanel();
		tabbedPane.addTab("Create graph", null, create, null);
		create.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Create graph by article title", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 383, 170);
		create.add(panel);
				
		textArticleTitle = new JTextField();
		textArticleTitle.setColumns(10);
		textArticleTitle.setBounds(111, 25, 124, 20);
		panel.add(textArticleTitle);
		
		textArticleRevlimit = new JTextField();
		textArticleRevlimit.setColumns(10);
		textArticleRevlimit.setBounds(111, 56, 124, 20);
		panel.add(textArticleRevlimit);
		
		textArticleUclimit = new JTextField();
		textArticleUclimit.setColumns(10);
		textArticleUclimit.setBounds(111, 80, 124, 20);
		panel.add(textArticleUclimit);
		
		JLabel label = new JLabel("title");
		label.setBounds(55, 28, 46, 14);
		panel.add(label);
		
		JLabel label_1 = new JLabel("revlimit");
		label_1.setBounds(55, 52, 46, 14);
		panel.add(label_1);
		
		JLabel label_2 = new JLabel("uclimit");
		label_2.setBounds(55, 83, 46, 14);
		panel.add(label_2);
		
		textArticleDepth = new JTextField();
		textArticleDepth.setColumns(10);
		textArticleDepth.setBounds(111, 105, 124, 20);
		panel.add(textArticleDepth);
		
		JLabel label_3 = new JLabel("depth");
		label_3.setBounds(55, 108, 46, 14);
		panel.add(label_3);
		
		
		
		JButton btnSubmitArticle = new JButton("submit");
		btnSubmitArticle.setEnabled(false);
		btnSubmitArticle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = textArticleTitle.getText();
				String revlimit = textArticleRevlimit.getText().trim();
				String uclimit = textArticleUclimit.getText().trim();
				String depthString = textArticleDepth.getText();
							
				if(!revlimit.matches("^[1-9]\\d*$") || !uclimit.matches("^[1-9]\\d*$")|| !depthString.matches("^[1-9]\\d*$"))
				{					
				JOptionPane.showMessageDialog(null, "revlimit, uclimit and depth must be digital number");
				return;
				}
				if(Integer.parseInt(revlimit)>500 || Integer.parseInt(revlimit)<1 || Integer.parseInt(uclimit)>500 || Integer.parseInt(uclimit)<1){
					JOptionPane.showMessageDialog(null, "revlimit and uclimit cannot more than 500 or less than 1");
					return;
				}
				
				int depth = Integer.parseInt(depthString);
				try {
					System.err.println(Calendar.getInstance().getTime());
					ReadXML.queryByArticle(title, revlimit, depth, uclimit);
					System.err.println(Calendar.getInstance().getTime());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnSubmitArticle.setBounds(121, 136, 89, 23);
		panel.add(btnSubmitArticle);
		
		listenFourText(textArticleTitle, textArticleRevlimit, textArticleUclimit, textArticleDepth, btnSubmitArticle);
		listenFourText(textArticleRevlimit, textArticleTitle, textArticleUclimit, textArticleDepth, btnSubmitArticle);
		listenFourText(textArticleUclimit, textArticleTitle, textArticleRevlimit, textArticleDepth, btnSubmitArticle);
		listenFourText(textArticleDepth, textArticleRevlimit, textArticleUclimit, textArticleTitle, btnSubmitArticle);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new TitledBorder(null, "Create graph by user name", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panel_1.setBounds(10, 186, 383, 189);
		create.add(panel_1);
		
		textUserName = new JTextField();
		textUserName.setColumns(10);
		textUserName.setBounds(120, 29, 124, 20);
		panel_1.add(textUserName);
		
		textUserRevlimit = new JTextField();
		textUserRevlimit.setColumns(10);
		textUserRevlimit.setBounds(120, 60, 124, 20);
		panel_1.add(textUserRevlimit);
		
		textUserUclimit = new JTextField();
		textUserUclimit.setColumns(10);
		textUserUclimit.setBounds(120, 91, 124, 20);
		panel_1.add(textUserUclimit);
			
		textUserDepth = new JTextField();
		textUserDepth.setColumns(10);
		textUserDepth.setBounds(120, 122, 124, 20);
		panel_1.add(textUserDepth);
		
		JLabel label_4 = new JLabel("name");
		label_4.setBounds(45, 32, 46, 14);
		panel_1.add(label_4);
		
		JLabel label_5 = new JLabel("revlimit");
		label_5.setBounds(45, 63, 46, 14);
		panel_1.add(label_5);
		
		JLabel label_6 = new JLabel("uclimit");
		label_6.setBounds(45, 94, 46, 14);
		panel_1.add(label_6);
		
		JLabel label_7 = new JLabel("depth");
		label_7.setBounds(45, 125, 46, 14);
		panel_1.add(label_7);
		
		JButton btnSubmitUser = new JButton("submit");
		btnSubmitUser.setEnabled(false);
		btnSubmitUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = textUserName.getText();
				String revlimit = textUserRevlimit.getText().trim();
				String uclimit = textUserUclimit.getText().trim();
				String depthString = textUserDepth.getText();
				if(!revlimit.matches("^[1-9]\\d*$") || !uclimit.matches("^[1-9]\\d*$") || !depthString.matches("^[1-9]\\d*$"))
				{					
				JOptionPane.showMessageDialog(null, "revlimit, uclimit and depth must be digital number");
				return;
				}
				if(Integer.parseInt(revlimit)>500 || Integer.parseInt(revlimit)<1 || Integer.parseInt(uclimit)>500 || Integer.parseInt(uclimit)<1){
					JOptionPane.showMessageDialog(null, "revlimit and uclimit cannot more than 500 or less than 1");
					return;
				}
				
				int depth = Integer.parseInt(depthString);
				try {
					ReadXML.queryByUser(user, uclimit, depth, revlimit);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			
			}
		});
		btnSubmitUser.setBounds(131, 153, 89, 23);
		panel_1.add(btnSubmitUser);
		
		listenFourText(textUserName, textUserRevlimit, textUserUclimit, textUserDepth, btnSubmitUser);
		listenFourText(textUserRevlimit, textUserName, textUserUclimit, textUserDepth, btnSubmitUser);
		listenFourText(textUserUclimit, textUserRevlimit, textUserName, textUserDepth, btnSubmitUser);
		listenFourText(textUserDepth, textUserRevlimit, textUserUclimit, textUserName, btnSubmitUser);
		
		JPanel panel_15 = new JPanel();
		panel_15.setLayout(null);
		panel_15.setBorder(new TitledBorder(null, "Delete index", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_15.setBounds(10, 470, 383, 66);
		create.add(panel_15);
		
		JButton btnDelete = new JButton("delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "You will delete all indexes from the Neo4j, Are you sure?", "Delete",
	                    JOptionPane.YES_NO_OPTION);
				if(result==JOptionPane.YES_OPTION){
					ReadXML.deleteIndex();
				}
			}
		});
		btnDelete.setBounds(134, 32, 89, 23);
		panel_15.add(btnDelete);
		
		JPanel panel_25 = new JPanel();
		panel_25.setLayout(null);
		panel_25.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Delete All", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_25.setBounds(10, 386, 383, 66);
		create.add(panel_25);
		
		JButton buttonDeleteNodeAndRelationship = new JButton("delete");
		buttonDeleteNodeAndRelationship.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "You will delete all nodes, relationships and indexes from the Neo4j, are you sure?", "Delete",
	                    JOptionPane.YES_NO_OPTION);
				if(result==JOptionPane.YES_OPTION){
				ReadXML.deleteRelationshipAndNode();
				ReadXML.deleteIndex();
				}
			}
		});
		buttonDeleteNodeAndRelationship.setBounds(134, 32, 89, 23);
		panel_25.add(buttonDeleteNodeAndRelationship);
		
		JPanel vandalism = new JPanel();
		tabbedPane.addTab("Vandalism", null, vandalism, null);
		vandalism.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		vandalism.add(tabbedPane_1, BorderLayout.CENTER);
		
		JPanel panelAllVandalism = new JPanel();
		tabbedPane_1.addTab("All vandalism", null, panelAllVandalism, null);
		panelAllVandalism.setLayout(null);
		
		JPanel panel_18 = new JPanel();
		panel_18.setLayout(null);
		panel_18.setBorder(new TitledBorder(null, "Find all vandalism", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_18.setBounds(10, 11, 415, 463);
		panelAllVandalism.add(panel_18);
		
		final JLabel labelCountAllVandalism = new JLabel("");
		labelCountAllVandalism.setBounds(188, 55, 65, 14);
		panel_18.add(labelCountAllVandalism);
		
		
		JScrollPane scrollPane_7 = new JScrollPane();
		scrollPane_7.setBounds(10, 79, 395, 373);
		panel_18.add(scrollPane_7);
		
		tableAllVandalism = new JTable();
		tableAllVandalism.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane_7.setViewportView(tableAllVandalism);
		
		JButton btnShowAllVandalism = new JButton("Find all vandalism");
		btnShowAllVandalism.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String allVandalism = new String(); 
				
				DefaultTableModel model = new DefaultTableModel();
				model.addColumn("title");
				model.addColumn("revid");
				model.addColumn("time");
				model.addColumn("node number");
				model.addColumn("user name");
				List<Object[]> resultList;
				int counter=0;
				
					try {
						resultList = ReadXML.showAllVandalism();
						if(resultList.size()!=0){
							for(int i=0;i<resultList.size();i++){
								counter++;
								model.addRow(resultList.get(i));
							}
						}
					} catch (ClientHandlerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UniformInterfaceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				
				
				tableAllVandalism.setModel(model);
				
				//textAllVandalism.setText(allVandalism);
				labelCountAllVandalism.setText(Integer.toString(counter));
				
			}
		});
		btnShowAllVandalism.setBounds(10, 21, 395, 23);
		panel_18.add(btnShowAllVandalism);
		
		JLabel lblHowManyVandalism = new JLabel("How many vandalism?");
		lblHowManyVandalism.setBounds(31, 54, 127, 14);
		panel_18.add(lblHowManyVandalism);
		
		JButton btnGoToHowManyVandalism = new JButton("link to wiki");
		btnGoToHowManyVandalism.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToUrl(tableAllVandalism);
			}
		});
		btnGoToHowManyVandalism.setBounds(287, 46, 89, 23);
		panel_18.add(btnGoToHowManyVandalism);
		
		
		JPanel panel_19 = new JPanel();
		panel_19.setLayout(null);
		panel_19.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Save all vandalism", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panel_19.setBounds(10, 485, 415, 82);
		panelAllVandalism.add(panel_19);
		
		JLabel label_17 = new JLabel("folder path");
		label_17.setBounds(45, 15, 65, 14);
		panel_19.add(label_17);
		
		textFolerPathVandalism = new JTextField();
		textFolerPathVandalism.setEditable(false);
		textFolerPathVandalism.setColumns(10);
		textFolerPathVandalism.setBounds(121, 12, 284, 20);
		panel_19.add(textFolerPathVandalism);
		
		JButton btnChooseFolderForVandalism = new JButton("save as");
		final JFileChooser jfcAllVandalism = new JFileChooser();
		jfcAllVandalism.setCurrentDirectory(new File("d://"));
		btnChooseFolderForVandalism.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jfcAllVandalism.setFileSelectionMode(1);
				 int state = jfcAllVandalism.showOpenDialog(null);
		            if (state == 1) {  
		                return;  
		            } else {  
		                File f = jfcAllVandalism.getSelectedFile();
		                textFolerPathVandalism.setText(f.getAbsolutePath());  
		            }  
			}
		});
		btnChooseFolderForVandalism.setBounds(121, 43, 89, 23);
		panel_19.add(btnChooseFolderForVandalism);
		
		JButton btnSubmitVandalism = new JButton("submit");
		btnSubmitVandalism.setEnabled(false);
		btnSubmitVandalism.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String folderPath = textFolerPathVandalism.getText();		
					try {
						ReadXML.GetVandalism(folderPath);
					} catch (ClientHandlerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UniformInterfaceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (org.codehaus.jettison.json.JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}		
			}
		});
		btnSubmitVandalism.setBounds(261, 43, 89, 23);
		panel_19.add(btnSubmitVandalism);
		
		listenOneText(textFolerPathVandalism, btnSubmitVandalism);
		
		JPanel panelUserVandalism = new JPanel();
		tabbedPane_1.addTab("User vansalism", null, panelUserVandalism, null);
		panelUserVandalism.setLayout(null);
		
		JPanel panel_17 = new JPanel();
		panel_17.setLayout(null);
		panel_17.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Find vandalism by user name", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_17.setBounds(10, 11, 415, 432);
		panelUserVandalism.add(panel_17);
		
		JScrollPane scrollPane_8 = new JScrollPane();
		scrollPane_8.setBounds(10, 107, 395, 314);
		panel_17.add(scrollPane_8);
		
		tableVandalismByUser = new JTable();
		tableVandalismByUser.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane_8.setViewportView(tableVandalismByUser);
		
		textShowVandalismUserName = new JTextField();
		textShowVandalismUserName.addInputMethodListener(new InputMethodListener() {
			public void caretPositionChanged(InputMethodEvent arg0) {
			}
			public void inputMethodTextChanged(InputMethodEvent arg0) {
			}
		});
		textShowVandalismUserName.setColumns(10);
		textShowVandalismUserName.setBounds(152, 23, 190, 20);
		panel_17.add(textShowVandalismUserName);
		
		
		JLabel label_18 = new JLabel("user name");
		label_18.setBounds(58, 26, 84, 14);
		panel_17.add(label_18);
		
		final JLabel labelCountUserVandalism = new JLabel("");
		labelCountUserVandalism.setBounds(196, 81, 65, 14);
		panel_17.add(labelCountUserVandalism);
		
		JButton btnShowVandalismByUser = new JButton("Find vandalism");
		btnShowVandalismByUser.setEnabled(false);
		btnShowVandalismByUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				Map<String, String> vandalismAndCount = new HashMap<String, String>();
//				String user = textShowVandalismUserName.getText();
//				String vandalismByUser = new String();   
//				try {
//					vandalismAndCount = ReadXML.showVandalismByUser(user);
//				} catch (ClientHandlerException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (UniformInterfaceException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (URISyntaxException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (org.codehaus.jettison.json.JSONException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//
//				vandalismByUser = vandalismAndCount.get("vandalismText");
//				String count = vandalismAndCount.get("count");
//				if(vandalismByUser.equals("")) vandalismByUser = "There is no vamdalism";
//				textVandalismByUser.setText(vandalismByUser);
//				labelCountUserVandalism.setText(count);
				String user = textShowVandalismUserName.getText();
				DefaultTableModel model = new DefaultTableModel();
				model.addColumn("title");
				model.addColumn("revid");
				model.addColumn("time");
				model.addColumn("node number");
				model.addColumn("user name");
				List<Object[]> resultList;
				int counter=0;
				
					try {
						resultList = ReadXML.showVandalismByUser(user);
						if(resultList.size()!=0){
							for(int i=0;i<resultList.size();i++){
								counter++;
								model.addRow(resultList.get(i));
							}
						}
					} catch (ClientHandlerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UniformInterfaceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					tableVandalismByUser.setModel(model);
					labelCountUserVandalism.setText(Integer.toString(counter));	
					
			}
		});
		btnShowVandalismByUser.setBounds(10, 54, 395, 23);
		panel_17.add(btnShowVandalismByUser);
		
		listenOneText(textShowVandalismUserName, btnShowVandalismByUser);
		
		JLabel label_13 = new JLabel("How many vandalism?");
		label_13.setBounds(37, 81, 127, 14);
		panel_17.add(label_13);
		
		JButton btnGoToVandalismByUser = new JButton("link to wiki");
		btnGoToVandalismByUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToUrl(tableVandalismByUser);
			}
		});
		btnGoToVandalismByUser.setBounds(286, 77, 89, 23);
		panel_17.add(btnGoToVandalismByUser);
		
		
		
		JPanel panel_20 = new JPanel();
		panel_20.setLayout(null);
		panel_20.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Save vandalism by user name", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panel_20.setBounds(10, 454, 415, 125);
		panelUserVandalism.add(panel_20);
		
		JLabel label_19 = new JLabel("folder path");
		label_19.setBounds(46, 26, 65, 14);
		panel_20.add(label_19);
		
		textFolerPathVandalismByUser = new JTextField();
		textFolerPathVandalismByUser.setEditable(false);
		textFolerPathVandalismByUser.setColumns(10);
		textFolerPathVandalismByUser.setBounds(121, 23, 212, 20);
		panel_20.add(textFolerPathVandalismByUser);
		
		textUserVandalismByUser = new JTextField();
		textUserVandalismByUser.setColumns(10);
		textUserVandalismByUser.setBounds(121, 66, 118, 20);
		panel_20.add(textUserVandalismByUser);
		
		JButton btnChooseFolderForVandalismByUser = new JButton("save as");
		final JFileChooser jfcVandalismByUser = new JFileChooser();
		jfcVandalismByUser.setCurrentDirectory(new File("d://"));
		btnChooseFolderForVandalismByUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jfcVandalismByUser.setFileSelectionMode(1);
				 int state = jfcVandalismByUser.showOpenDialog(null);
		            if (state == 1) {  
		                return;  
		            } else {  
		                File f = jfcVandalismByUser.getSelectedFile();
		                textFolerPathVandalismByUser.setText(f.getAbsolutePath());  
		            }  				
			}
		});
		btnChooseFolderForVandalismByUser.setBounds(121, 97, 89, 23);
		panel_20.add(btnChooseFolderForVandalismByUser);
		
		JButton btnSubmitVandalismByUser = new JButton("submit");
		btnSubmitVandalismByUser.setEnabled(false);
		btnSubmitVandalismByUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = textUserVandalismByUser.getText();
				String folderPath = textFolerPathVandalismByUser.getText();
					try {
						ReadXML.getVandalismByUser(folderPath, user);
					} catch (ClientHandlerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UniformInterfaceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (org.codehaus.jettison.json.JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		});
		btnSubmitVandalismByUser.setBounds(220, 97, 89, 23);
		panel_20.add(btnSubmitVandalismByUser);
		
		listenTwoText(textUserVandalismByUser, textFolerPathVandalismByUser, btnSubmitVandalismByUser);
		listenTwoText(textFolerPathVandalismByUser, textUserVandalismByUser, btnSubmitVandalismByUser);
		
		
		
		JLabel label_20 = new JLabel("user name");
		label_20.setBounds(46, 69, 65, 14);
		panel_20.add(label_20);
		
		JPanel panel_8 = new JPanel();
		tabbedPane_1.addTab("Article vandalism", null, panel_8, null);
		panel_8.setLayout(null);
		
		JPanel panel_9 = new JPanel();
		panel_9.setLayout(null);
		panel_9.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Find vandalism by article", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_9.setBounds(10, 11, 415, 432);
		panel_8.add(panel_9);
		
		JScrollPane scrollPane_6 = new JScrollPane();
		scrollPane_6.setBounds(10, 105, 395, 316);
		panel_9.add(scrollPane_6);
		
		tableVandalismByArticle = new JTable();
		tableVandalismByArticle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane_6.setViewportView(tableVandalismByArticle);
		
		textShowVandalismByArticle = new JTextField();
		textShowVandalismByArticle.setColumns(10);
		textShowVandalismByArticle.setBounds(152, 23, 190, 20);
		panel_9.add(textShowVandalismByArticle);
		
		JLabel lblTitle_2 = new JLabel("title");
		lblTitle_2.setBounds(58, 26, 84, 14);
		panel_9.add(lblTitle_2);
		
		final JLabel labelCountArticlelVandalism = new JLabel("");
		labelCountArticlelVandalism.setBounds(189, 79, 65, 14);
		panel_9.add(labelCountArticlelVandalism);
		
		JButton buttonShowVandalismByArticle = new JButton("Find vandalism");
		buttonShowVandalismByArticle.setEnabled(false);
		buttonShowVandalismByArticle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = textShowVandalismByArticle.getText();
				DefaultTableModel model = new DefaultTableModel();
				model.addColumn("title");
				model.addColumn("revid");
				model.addColumn("time");
				model.addColumn("node number");
				model.addColumn("user name");
				List<Object[]> resultList;
				int counter=0;
				
					try {
						resultList = ReadXML.showVandalismByTitle(title);
						if(resultList.size()!=0){
							for(int i=0;i<resultList.size();i++){
								counter++;
								model.addRow(resultList.get(i));
							}
						}
					} catch (ClientHandlerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UniformInterfaceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					tableVandalismByArticle.setModel(model);
					labelCountArticlelVandalism.setText(Integer.toString(counter));	
			}
		});
		buttonShowVandalismByArticle.setBounds(10, 51, 395, 23);
		panel_9.add(buttonShowVandalismByArticle);
		
		listenOneText(textShowVandalismByArticle, buttonShowVandalismByArticle);
		
		JLabel label_21 = new JLabel("How many vandalism?");
		label_21.setBounds(36, 79, 127, 14);
		panel_9.add(label_21);
		
		JButton btnGoToVandalismByArticle = new JButton("link to wiki");
		btnGoToVandalismByArticle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToUrl(tableVandalismByArticle);
			}
		});
		btnGoToVandalismByArticle.setBounds(285, 75, 89, 23);
		panel_9.add(btnGoToVandalismByArticle);
		
		
		
		JPanel panel_11 = new JPanel();
		panel_11.setLayout(null);
		panel_11.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Save vandalism by title", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panel_11.setBounds(10, 454, 415, 125);
		panel_8.add(panel_11);
		
		JLabel label_10 = new JLabel("folder path");
		label_10.setBounds(46, 26, 65, 14);
		panel_11.add(label_10);
		
		textFolerPathVandalismByTitle = new JTextField();
		textFolerPathVandalismByTitle.setEditable(false);
		textFolerPathVandalismByTitle.setColumns(10);
		textFolerPathVandalismByTitle.setBounds(121, 23, 212, 20);
		panel_11.add(textFolerPathVandalismByTitle);
		
		textTitleVandalismByTitle = new JTextField();
		textTitleVandalismByTitle.setColumns(10);
		textTitleVandalismByTitle.setBounds(121, 66, 118, 20);
		panel_11.add(textTitleVandalismByTitle);
		
		JButton buttonSaveAsVandalismByTitle = new JButton("save as");
		final JFileChooser jfcVandalismByTitle = new JFileChooser();
		jfcVandalismByTitle.setCurrentDirectory(new File("d://"));
		buttonSaveAsVandalismByTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				jfcVandalismByTitle.setFileSelectionMode(1);
				 int state = jfcVandalismByTitle.showOpenDialog(null);
		            if (state == 1) {  
		                return;  
		            } else {  
		                File f = jfcVandalismByTitle.getSelectedFile();
		                textFolerPathVandalismByTitle.setText(f.getAbsolutePath());  
		            }  	
				
			}
		});
		buttonSaveAsVandalismByTitle.setBounds(121, 97, 89, 23);
		panel_11.add(buttonSaveAsVandalismByTitle);
		
		JButton buttonSubmitVandalismByTitle = new JButton("submit");
		buttonSubmitVandalismByTitle.setEnabled(false);
		buttonSubmitVandalismByTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String folderPath = textFolerPathVandalismByTitle.getText();
				String title = textTitleVandalismByTitle.getText();
					try {
						ReadXML.getVandalismByTitle(folderPath, title);
					} catch (ClientHandlerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UniformInterfaceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (org.codehaus.jettison.json.JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		});
		buttonSubmitVandalismByTitle.setBounds(220, 97, 89, 23);
		panel_11.add(buttonSubmitVandalismByTitle);
		
		listenTwoText(textFolerPathVandalismByTitle, textTitleVandalismByTitle, buttonSubmitVandalismByTitle);
		listenTwoText(textTitleVandalismByTitle, textFolerPathVandalismByTitle, buttonSubmitVandalismByTitle);
		
		JLabel lblTitle_3 = new JLabel("title");
		lblTitle_3.setBounds(46, 69, 65, 14);
		panel_11.add(lblTitle_3);
		
		JPanel statistics = new JPanel();
		tabbedPane.addTab("Statistics", null, statistics, null);
		statistics.setLayout(null);
		
		final JLabel lblShowHowManyTitle = new JLabel("");
		lblShowHowManyTitle.setBounds(296, 13, 66, 19);
		statistics.add(lblShowHowManyTitle);
		
		JButton btnHowManyTitle = new JButton("How many titles in the Neo4j?");
		btnHowManyTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String countTitle = ReadXML.countTitle();
				lblShowHowManyTitle.setText(countTitle);
			}
		});
		btnHowManyTitle.setBounds(10, 11, 255, 23);
		statistics.add(btnHowManyTitle);
		
		final JLabel lblShowHowManyRevision = new JLabel("");
		lblShowHowManyRevision.setBounds(296, 53, 66, 19);
		statistics.add(lblShowHowManyRevision);
		
		JButton btnHowManyRevisions = new JButton("How many revisions in the Neo4j?");
		btnHowManyRevisions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String revisionNumber = ReadXML.getTheNumberOfRevision();
				lblShowHowManyRevision.setText(revisionNumber);
			}
		});
		btnHowManyRevisions.setBounds(10, 49, 255, 23);
		statistics.add(btnHowManyRevisions);
			
		
		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new TitledBorder(null, "Titles", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_6.setBounds(10, 96, 180, 511);
		statistics.add(panel_6);
		panel_6.setLayout(null);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 82, 160, 418);
		panel_6.add(scrollPane_2);
		
		tableShowAllTitles = new JTable();
		tableShowAllTitles.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane_2.setViewportView(tableShowAllTitles);
		
		JButton btnGetAllTitles = new JButton("Get all titles");
		btnGetAllTitles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				String listTitle = ReadXML.listTitle();
//				if(listTitle.equals("")) listTitle = "There is no title";
//				textAreaShowAllTitles.setText(listTitle);
				
				DefaultTableModel model = new DefaultTableModel();
				
				model.addColumn("title");
				List<Object[]> resultList;				
					try {
						resultList = ReadXML.listTitle();
						if(resultList.size()!=0){
							for(int i=0;i<resultList.size();i++){
								model.addRow(resultList.get(i));
							}
						}
					} catch (ClientHandlerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UniformInterfaceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
				tableShowAllTitles.setModel(model);
			}
		});
		btnGetAllTitles.setBounds(10, 21, 160, 23);
		panel_6.add(btnGetAllTitles);
		
		JButton btnGoToGetAllTitles = new JButton("link to wiki");
		btnGoToGetAllTitles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToUrl(tableShowAllTitles);
			}
		});
		btnGoToGetAllTitles.setBounds(81, 48, 89, 23);
		panel_6.add(btnGoToGetAllTitles);
		
		
		
		
		
		JPanel panel_7 = new JPanel();
		panel_7.setBorder(new TitledBorder(null, "How many users edit the Article?", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_7.setBounds(200, 96, 206, 511);
		statistics.add(panel_7);
		panel_7.setLayout(null);
		
		textFieldArticleName = new JTextField();
		textFieldArticleName.setBounds(10, 37, 186, 20);
		panel_7.add(textFieldArticleName);
		textFieldArticleName.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Article name:");
		lblNewLabel.setBounds(10, 21, 77, 14);
		panel_7.add(lblNewLabel);
		
		final JLabel lblUserNumber = new JLabel("");
		lblUserNumber.setBounds(110, 105, 46, 14);
		panel_7.add(lblUserNumber);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(10, 130, 186, 370);
		panel_7.add(scrollPane_3);
		
		final JTextArea textAreaShowAllUserList = new JTextArea();
		scrollPane_3.setViewportView(textAreaShowAllUserList);
		
		JButton btnFindUserListByTitle = new JButton("Submit");
		btnFindUserListByTitle.setEnabled(false);
		btnFindUserListByTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String articleName = textFieldArticleName.getText();
				
				Map<String, String> userStatistics = new HashMap<String, String>();
				try {
					userStatistics = ReadXML.getUserListByTitle(articleName);
				} catch (ClientHandlerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UniformInterfaceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (org.codehaus.jettison.json.JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String userList = userStatistics.get("userList");
				String userNumber = userStatistics.get("userNumber");
				if(userList.isEmpty()) userList = "There is no user information";
				
				lblUserNumber.setText(userNumber);
				textAreaShowAllUserList.setText(userList);
			}
		});
		btnFindUserListByTitle.setBounds(107, 71, 89, 23);
		panel_7.add(btnFindUserListByTitle);
		
		listenOneText(textFieldArticleName, btnFindUserListByTitle);
		
		JLabel lblUserNumberIs = new JLabel("User number is");
		lblUserNumberIs.setBounds(10, 105, 89, 14);
		panel_7.add(lblUserNumberIs);
		
		JPanel panel_16 = new JPanel();
		tabbedPane.addTab("Count", null, panel_16, null);
		panel_16.setLayout(null);
		
		JPanel panel_21 = new JPanel();
		panel_21.setLayout(null);
		panel_21.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Count title", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_21.setBounds(10, 11, 201, 596);
		panel_16.add(panel_21);
		
		JScrollPane scrollPane_9 = new JScrollPane();
		scrollPane_9.setBounds(10, 80, 181, 505);
		panel_21.add(scrollPane_9);
		
		tableCountTitle = new JTable();
		tableCountTitle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane_9.setViewportView(tableCountTitle);
		
		JButton btnNumberOfUsers = new JButton("user number");
		btnNumberOfUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//				String titleUserNumber = new String();
//				try {
//					titleUserNumber = ReadXML.countTitleUserNumber();
//				} catch (ClientHandlerException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (UniformInterfaceException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (org.codehaus.jettison.json.JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if(titleUserNumber.equals("")) titleUserNumber = "There is no title";
//				textAreaCountTitle.setText(titleUserNumber);
				
				DefaultTableModel model = new DefaultTableModel();
				model.addColumn("title");
				model.addColumn("number of users");
				List<Object[]> resultList;				
					try {
						resultList = ReadXML.countTitleUserNumber();
						if(resultList.size()!=0){
							for(int i=0;i<resultList.size();i++){
								model.addRow(resultList.get(i));
							}
						}
					} catch (ClientHandlerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UniformInterfaceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
				tableCountTitle.setModel(model);
				
			}
		});
		btnNumberOfUsers.setBounds(10, 21, 181, 23);
		panel_21.add(btnNumberOfUsers);
		
		JButton btnGoToNumberOfUsers = new JButton("link to wiki");
		btnGoToNumberOfUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToUrl(tableCountTitle);				
			}
		});
		btnGoToNumberOfUsers.setBounds(102, 46, 89, 23);
		panel_21.add(btnGoToNumberOfUsers);
		
		JPanel panel_22 = new JPanel();
		panel_22.setLayout(null);
		panel_22.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Count vandalism", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_22.setBounds(221, 11, 201, 596);
		panel_16.add(panel_22);
		
		JScrollPane scrollPane_10 = new JScrollPane();
		scrollPane_10.setBounds(10, 55, 181, 530);
		panel_22.add(scrollPane_10);
		
		final JTextArea textAreaCountVandalism = new JTextArea();
		scrollPane_10.setViewportView(textAreaCountVandalism);
		
		JButton btnVandalismNumber = new JButton("vandalism number");
		btnVandalismNumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String vandalismNumber = new String();
				try {
					vandalismNumber = ReadXML.countTitleVandalismNumber();
				} catch (ClientHandlerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UniformInterfaceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (org.codehaus.jettison.json.JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(vandalismNumber.equals("")) vandalismNumber = "There is no vandalism";
				textAreaCountVandalism.setText(vandalismNumber);
			}
		});
		btnVandalismNumber.setBounds(10, 21, 181, 23);
		panel_22.add(btnVandalismNumber);
		
		
		//=======================================data panel start================================================================		
		JPanel data = new JPanel();
		tabbedPane.addTab("Data", null, data, null);
		data.setLayout(new BoxLayout(data, BoxLayout.X_AXIS));
		
		JTabbedPane tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
		data.add(tabbedPane_2);
		

		
		
		
//		//=======================================get user contribution start================================================================	
		JPanel panelGetContributionByUser = new JPanel();
		tabbedPane_2.addTab("Get contributions by user", null, panelGetContributionByUser, null);
		panelGetContributionByUser.setLayout(null);
		
//		JPanel panel_23 = new JPanel();
//		panel_23.setBorder(new TitledBorder(null, "test title", TitledBorder.LEADING, TitledBorder.TOP, null, null));
//		panel_23.setBounds(23, 45, 357, 128);
//		panelGetContributionByUser.add(panel_23);
//		

		JPanel panel_13 = new JPanel();
		panel_13.setLayout(null);
		panel_13.setBorder(new TitledBorder(null, "get Contris by user", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panel_13.setBounds(10, 446, 415, 133);
		panelGetContributionByUser.add(panel_13);
		
		textFolderPathUser = new JTextField();
		textFolderPathUser.setEditable(false);
		textFolderPathUser.setColumns(10);
		textFolderPathUser.setBounds(128, 25, 211, 20);
		panel_13.add(textFolderPathUser);
		

		textContrisUserName = new JTextField();
		textContrisUserName.setColumns(10);
		textContrisUserName.setBounds(128, 56, 86, 20);
		panel_13.add(textContrisUserName);
		
		JButton btnChooseFolderForUserContris = new JButton("save as");
		final JFileChooser jfc2 = new JFileChooser();
		jfc2.setCurrentDirectory(new File("d://"));
		btnChooseFolderForUserContris.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jfc2.setFileSelectionMode(1);
				 int state = jfc2.showOpenDialog(null);
		            if (state == 1) {  
		                return;  
		            } else {  
		                File f = jfc2.getSelectedFile();
		                textFolderPathUser.setText(f.getAbsolutePath());  
		            }  
				
			}
		});
		btnChooseFolderForUserContris.setBounds(42, 99, 89, 23);
		panel_13.add(btnChooseFolderForUserContris);
		
		JButton btnSubmitContris = new JButton("submit");
		btnSubmitContris.setEnabled(false);
		btnSubmitContris.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String folderPath = textFolderPathUser.getText();
				String user = textContrisUserName.getText();
				try {
					ReadXML.GetContributionsByUserOffline(user, folderPath);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}					
			}
		});
		btnSubmitContris.setBounds(181, 99, 89, 23);
		panel_13.add(btnSubmitContris);
		
		listenTwoText(textFolderPathUser, textContrisUserName, btnSubmitContris);
		listenTwoText(textContrisUserName, textFolderPathUser, btnSubmitContris);
		
		JLabel label_11 = new JLabel("folder path");
		label_11.setBounds(42, 28, 64, 14);
		panel_13.add(label_11);
		
		
		JLabel label_14 = new JLabel("user name");
		label_14.setBounds(42, 59, 69, 14);
		panel_13.add(label_14);
		
		JPanel panel_23 = new JPanel();
		panel_23.setLayout(null);
		panel_23.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Get all contributions by user name", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_23.setBounds(10, 11, 415, 432);
		panelGetContributionByUser.add(panel_23);
		
		JScrollPane scrollPane_11 = new JScrollPane();
		scrollPane_11.setBounds(10, 107, 395, 314);
		panel_23.add(scrollPane_11);
		
		tableGetContrisByUser = new JTable();
		tableGetContrisByUser.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane_11.setViewportView(tableGetContrisByUser);
		
		textGetContrisByUser = new JTextField();
		textGetContrisByUser.setColumns(10);
		textGetContrisByUser.setBounds(152, 23, 190, 20);
		panel_23.add(textGetContrisByUser);
		
		JLabel label_16 = new JLabel("user name");
		label_16.setBounds(58, 26, 84, 14);
		panel_23.add(label_16);
		
		final JLabel labelGetContrisByUser = new JLabel("");
		labelGetContrisByUser.setBounds(237, 82, 65, 14);
		panel_23.add(labelGetContrisByUser);
		
		JButton buttonGetContrisByUser = new JButton("Get all contributions by user name");
		buttonGetContrisByUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String userName = textGetContrisByUser.getText();
//				Map<String, String> userContribsAndCount = new HashMap<String, String>();
//				try {
//					userContribsAndCount = ReadXML.showContrisByUser(userName);
//				} catch (ClientHandlerException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (UniformInterfaceException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				String userContribs = userContribsAndCount.get("userContribs");
//				String userCount = userContribsAndCount.get("userCount");
//				if(userCount == "0"){
//					userContribs = "The user have not edit any article.";
//				}
//				textAreaGetContrisByUser.setText(userContribs);
//				labelGetContrisByUser.setText(userCount);
				DefaultTableModel model = new DefaultTableModel();
				model.addColumn("title");
				model.addColumn("revid");
				model.addColumn("time");
				model.addColumn("node number");
				List<Object[]> resultList;
				int counter=0;
				try {
					resultList = ReadXML.showContrisByUser(userName);
					if(resultList.size()!=0){
						for(int i=0;i<resultList.size();i++){
							counter++;
							model.addRow(resultList.get(i));
						}
					}
				} catch (ClientHandlerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UniformInterfaceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				tableGetContrisByUser.setModel(model);
				labelGetContrisByUser.setText(String.valueOf(counter));
			}
		});
		buttonGetContrisByUser.setEnabled(false);
		buttonGetContrisByUser.setBounds(10, 54, 395, 23);
		panel_23.add(buttonGetContrisByUser);
		
		listenOneText(textGetContrisByUser, buttonGetContrisByUser);
		
		JLabel lblHowManyRevisions = new JLabel("How many revisions that user edited?");
		lblHowManyRevisions.setBounds(10, 82, 217, 14);
		panel_23.add(lblHowManyRevisions);
		
		JButton btnGoTo = new JButton("link to wiki");
		btnGoTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goToUrl(tableGetContrisByUser);
			}
		});
		btnGoTo.setBounds(295, 78, 89, 23);
		panel_23.add(btnGoTo);
	//=======================================get user contribution end================================================================	
//	//=======================================get title information start================================================================	
		JPanel panelGetInfoByTitle = new JPanel();
		tabbedPane_2.addTab("Get information by title", null, panelGetInfoByTitle, null);
		panelGetInfoByTitle.setLayout(null);
		
		JPanel panel_14 = new JPanel();
		panel_14.setLayout(null);
		panel_14.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "get information by title", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panel_14.setBounds(10, 446, 415, 133);
		panelGetInfoByTitle.add(panel_14);
		
		textFolderPathTitle = new JTextField();
		textFolderPathTitle.setEditable(false);
		textFolderPathTitle.setColumns(10);
		textFolderPathTitle.setBounds(128, 37, 211, 20);
		panel_14.add(textFolderPathTitle);
		
		textArticleInfoByTitle = new JTextField();
		textArticleInfoByTitle.setColumns(10);
		textArticleInfoByTitle.setBounds(128, 68, 86, 20);
		panel_14.add(textArticleInfoByTitle);
		
		JButton button_4 = new JButton("save as");
		final JFileChooser jfcByTitle = new JFileChooser();
		jfcByTitle.setCurrentDirectory(new File("d://"));
		button_4.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				jfcByTitle.setFileSelectionMode(1);
				 int state = jfcByTitle.showOpenDialog(null);
		            if (state == 1) {  
		                return;  
		            } else {  
		                File f = jfcByTitle.getSelectedFile();
		                textFolderPathTitle.setText(f.getAbsolutePath());  
		            }  
			}
		});
		button_4.setBounds(42, 99, 89, 23);
		panel_14.add(button_4);
		
		JButton button_6 = new JButton("submit");
		button_6.setEnabled(false);
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = textArticleInfoByTitle.getText();
				String folderPath = textFolderPathTitle.getText();
				try {
					ReadXML.getArticleInfoByTitleOffline(title, folderPath);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		button_6.setBounds(180, 99, 89, 23);
		panel_14.add(button_6);
		
		listenTwoText(textArticleInfoByTitle, textFolderPathTitle, button_6);
		listenTwoText(textFolderPathTitle, textArticleInfoByTitle, button_6);
		
		JLabel label_15 = new JLabel("folder path");
		label_15.setBounds(42, 40, 64, 14);
		panel_14.add(label_15);
		
		
		
		JLabel lblTitle_1 = new JLabel("title");
		lblTitle_1.setBounds(42, 74, 69, 14);
		panel_14.add(lblTitle_1);
		
		JPanel panel_24 = new JPanel();
		panel_24.setLayout(null);
		panel_24.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Get all revisions information by article title", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_24.setBounds(10, 11, 415, 432);
		panelGetInfoByTitle.add(panel_24);
		
		JScrollPane scrollPane_12 = new JScrollPane();
		scrollPane_12.setBounds(10, 105, 395, 316);
		panel_24.add(scrollPane_12);
		
		tableGetInfoByArticle = new JTable();
		tableGetInfoByArticle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane_12.setViewportView(tableGetInfoByArticle);
		
		textFieldGetInfoByArticle = new JTextField();
		textFieldGetInfoByArticle.setColumns(10);
		textFieldGetInfoByArticle.setBounds(152, 23, 190, 20);
		panel_24.add(textFieldGetInfoByArticle);
		
		JLabel label_24 = new JLabel("title");
		label_24.setBounds(58, 26, 84, 14);
		panel_24.add(label_24);
		
		final JLabel labelFieldGetInfoByArticle = new JLabel("");
		labelFieldGetInfoByArticle.setBounds(213, 80, 65, 14);
		panel_24.add(labelFieldGetInfoByArticle);
		
		JButton buttonFieldGetInfoByArticle = new JButton("Get all revision information by article title");
		buttonFieldGetInfoByArticle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String title = textFieldGetInfoByArticle.getText();
//				Map<String, String> articleInfoAndCount = new HashMap<String, String>();
//				try {
//					articleInfoAndCount = ReadXML.showRevisionInfoByTitle(title);
//				} catch (ClientHandlerException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (UniformInterfaceException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
//				
//				String articleInfo = articleInfoAndCount.get("articleInfo");
//				String articleCount = articleInfoAndCount.get("articleCount");
//				if(articleCount == "0"){
//					articleInfo = "The user have not edit any article.";
//				}
//				textAreaFieldGetInfoByArticle.setText(articleInfo);
//				labelFieldGetInfoByArticle.setText(articleCount);
				DefaultTableModel model = new DefaultTableModel();
				model.addColumn("title");
				model.addColumn("revid");
				model.addColumn("time");
				model.addColumn("comment");
				model.addColumn("node number");
				List<Object[]> resultList;
				int counter=0;
				try {
					resultList = ReadXML.showRevisionInfoByTitle(title);
					if(resultList.size()!=0){
						for(int i=0;i<resultList.size();i++){
							counter++;
							model.addRow(resultList.get(i));
						}
					}
				} catch (ClientHandlerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UniformInterfaceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				tableGetInfoByArticle.setModel(model);
				labelFieldGetInfoByArticle.setText(String.valueOf(counter));
				
			}
		});
		buttonFieldGetInfoByArticle.setEnabled(false);
		buttonFieldGetInfoByArticle.setBounds(10, 54, 395, 23);
		panel_24.add(buttonFieldGetInfoByArticle);
		
		listenOneText(textFieldGetInfoByArticle, buttonFieldGetInfoByArticle);
		
		JLabel lblHowManyRevisions_1 = new JLabel("How many revisions of the article?");
		lblHowManyRevisions_1.setBounds(10, 80, 210, 14);
		panel_24.add(lblHowManyRevisions_1);
		
		JButton btnGoToGetInfoByArticle = new JButton("link to wiki");
		btnGoToGetInfoByArticle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToUrl(tableGetInfoByArticle);
			}
		});
		btnGoToGetInfoByArticle.setBounds(288, 80, 89, 23);
		panel_24.add(btnGoToGetInfoByArticle);
	//=======================================get title information end================================================================	
	//=======================================data panel end================================================================	
		JPanel nodeNumber = new JPanel();
		tabbedPane.addTab("Node number", null, nodeNumber, null);
		nodeNumber.setLayout(null);
		
		JPanel panel_12 = new JPanel();
		panel_12.setBounds(10, 11, 415, 81);
		nodeNumber.add(panel_12);
		panel_12.setLayout(null);
		panel_12.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Find node number by user name", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JLabel lblUserName_1 = new JLabel("User name:");
		lblUserName_1.setBounds(10, 26, 78, 14);
		panel_12.add(lblUserName_1);
		
		textFindNodeNumberByUser = new JTextField();
		textFindNodeNumberByUser.setColumns(10);
		textFindNodeNumberByUser.setBounds(108, 23, 86, 20);
		panel_12.add(textFindNodeNumberByUser);
		
		JLabel label_12 = new JLabel("Node number:");
		label_12.setBounds(10, 51, 96, 14);
		panel_12.add(label_12);
		
		final JLabel labelFindNodeNumberByUser = new JLabel("");
		labelFindNodeNumberByUser.setBounds(108, 51, 167, 14);
		panel_12.add(labelFindNodeNumberByUser);
		
		JButton buttonFindNodeNumberByUser = new JButton("submit");
		buttonFindNodeNumberByUser.setEnabled(false);
		buttonFindNodeNumberByUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = textFindNodeNumberByUser.getText();
				String nodeNumber = ReadXML.findNodeNumberByUserName(user);
				if (nodeNumber.equals("")) 
					nodeNumber = "The user is not exist";
				labelFindNodeNumberByUser.setText(nodeNumber);
			}
		});
		buttonFindNodeNumberByUser.setBounds(222, 22, 89, 23);
		panel_12.add(buttonFindNodeNumberByUser);
		
		listenOneText(textFindNodeNumberByUser, buttonFindNodeNumberByUser);
		
		JPanel panel_10 = new JPanel();
		panel_10.setBounds(10, 289, 415, 108);
		nodeNumber.add(panel_10);
		panel_10.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Find latest revision information by title", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_10.setLayout(null);
		
		JLabel lblTitle = new JLabel("Title:");
		lblTitle.setBounds(10, 28, 46, 14);
		panel_10.add(lblTitle);
		
		textFindLatestArticleTitle = new JTextField();
		textFindLatestArticleTitle.setBounds(50, 25, 140, 20);
		panel_10.add(textFindLatestArticleTitle);
		textFindLatestArticleTitle.setColumns(10);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(10, 56, 395, 41);
		panel_10.add(scrollPane_4);
		
		final JTextArea textAreaFindLatestArticleTitle = new JTextArea();
		scrollPane_4.setViewportView(textAreaFindLatestArticleTitle);
		
		JButton btnFindLatestArticleTitle = new JButton("submit");
		btnFindLatestArticleTitle.setEnabled(false);
		btnFindLatestArticleTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String title = textFindLatestArticleTitle.getText();
				String revisionLatestEdit = new String();
				try {
					revisionLatestEdit = ReadXML.findArticleLatestInfo(title);
				} catch (ClientHandlerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UniformInterfaceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (org.codehaus.jettison.json.JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(revisionLatestEdit.equals("")) revisionLatestEdit = "There is no information about the title";
				textAreaFindLatestArticleTitle.setText(revisionLatestEdit);
			}
		});
		btnFindLatestArticleTitle.setBounds(222, 24, 89, 23);
		panel_10.add(btnFindLatestArticleTitle);
		
		listenOneText(textFindLatestArticleTitle, btnFindLatestArticleTitle);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Find revision information by revid", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(10, 401, 415, 102);
		nodeNumber.add(panel_2);
		
		JLabel lblRevid_1 = new JLabel("Revid:");
		lblRevid_1.setBounds(10, 28, 46, 14);
		panel_2.add(lblRevid_1);
		
		textFindRevisionInfoByRevid = new JTextField();
		textFindRevisionInfoByRevid.setColumns(10);
		textFindRevisionInfoByRevid.setBounds(50, 25, 140, 20);
		panel_2.add(textFindRevisionInfoByRevid);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 56, 395, 35);
		panel_2.add(scrollPane);
		
		final JTextArea textAreaFindRevisionInfoByRevid = new JTextArea();
		scrollPane.setViewportView(textAreaFindRevisionInfoByRevid);
		
		JButton buttonFindRevisionInfoByRevid = new JButton("submit");
		buttonFindRevisionInfoByRevid.setEnabled(false);
		buttonFindRevisionInfoByRevid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String revid = textFindRevisionInfoByRevid.getText();
				if(!revid.matches("^[1-9]\\d*$"))
				{					
				JOptionPane.showMessageDialog(null, "revid must be digital number");
				return;
				}
				String revInfo = new String();
				try {
					revInfo = ReadXML.findRevisionInfoByRevid(revid);
				} catch (ClientHandlerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UniformInterfaceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (org.codehaus.jettison.json.JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(revInfo.equals("")) revInfo = "There is no information about the revsion";
				textAreaFindRevisionInfoByRevid.setText(revInfo);
			}
		});
		buttonFindRevisionInfoByRevid.setBounds(222, 24, 89, 23);
		panel_2.add(buttonFindRevisionInfoByRevid);
		
		listenOneText(textFindRevisionInfoByRevid, buttonFindRevisionInfoByRevid);
		
		JPanel panel_3 = new JPanel();
		panel_3.setLayout(null);
		panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Find latest revision edit by user", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(10, 180, 415, 102);
		nodeNumber.add(panel_3);
		
		JLabel lblUser = new JLabel("User:");
		lblUser.setBounds(10, 24, 46, 14);
		panel_3.add(lblUser);
		
		textFindLatestUserName = new JTextField();
		textFindLatestUserName.setColumns(10);
		textFindLatestUserName.setBounds(70, 21, 140, 20);
		panel_3.add(textFindLatestUserName);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 52, 395, 39);
		panel_3.add(scrollPane_1);
		
		final JTextArea textAreaFindLatestUser = new JTextArea();
		scrollPane_1.setViewportView(textAreaFindLatestUser);
		
		JButton buttonFindLatestUser = new JButton("submit");
		buttonFindLatestUser.setEnabled(false);
		buttonFindLatestUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = textFindLatestUserName.getText();
				String userLatestEdit = new String();
				try {
					userLatestEdit = ReadXML.findUserLastestInfo(user);
				} catch (ClientHandlerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UniformInterfaceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (org.codehaus.jettison.json.JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(userLatestEdit.equals("")) userLatestEdit = "There is no infomation about the user";
				textAreaFindLatestUser.setText(userLatestEdit);
			}
		});
		buttonFindLatestUser.setBounds(220, 20, 89, 23);
		panel_3.add(buttonFindLatestUser);
		
		listenOneText(textFindLatestUserName, buttonFindLatestUser);
		
		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Find revision information by title and user", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_4.setBounds(10, 501, 415, 106);
		nodeNumber.add(panel_4);
		
		JLabel label_8 = new JLabel("Title:");
		label_8.setBounds(10, 28, 46, 14);
		panel_4.add(label_8);
		
		textFindRevisionByTitleAndUserTitle = new JTextField();
		textFindRevisionByTitleAndUserTitle.setColumns(10);
		textFindRevisionByTitleAndUserTitle.setBounds(50, 25, 99, 20);
		panel_4.add(textFindRevisionByTitleAndUserTitle);
		
		textFindRevisionByTitleAndUserUser = new JTextField();
		textFindRevisionByTitleAndUserUser.setColumns(10);
		textFindRevisionByTitleAndUserUser.setBounds(205, 25, 101, 20);
		panel_4.add(textFindRevisionByTitleAndUserUser);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(10, 56, 395, 39);
		panel_4.add(scrollPane_5);
		
		final JTextArea textAreaFindRevisionByTitleAndUser = new JTextArea();
		scrollPane_5.setViewportView(textAreaFindRevisionByTitleAndUser);
		
		JButton buttonFindRevisionByTitleAndUser = new JButton("submit");
		buttonFindRevisionByTitleAndUser.setEnabled(false);
		buttonFindRevisionByTitleAndUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = textFindRevisionByTitleAndUserTitle.getText();
				String user = textFindRevisionByTitleAndUserUser.getText();
				String revisonByTitleAndUser = new String();
						try {
							revisonByTitleAndUser = ReadXML.findRevisionByTitleAndUser(title, user);
						} catch (ClientHandlerException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (UniformInterfaceException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (org.codehaus.jettison.json.JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					if(revisonByTitleAndUser.equals("")) revisonByTitleAndUser = "The user did not edited the title";
			
				textAreaFindRevisionByTitleAndUser.setText(revisonByTitleAndUser);
			}
		});
		buttonFindRevisionByTitleAndUser.setBounds(316, 24, 89, 23);
		panel_4.add(buttonFindRevisionByTitleAndUser);
		
		listenTwoText(textFindRevisionByTitleAndUserTitle, textFindRevisionByTitleAndUserUser, buttonFindRevisionByTitleAndUser);
		listenTwoText(textFindRevisionByTitleAndUserUser, textFindRevisionByTitleAndUserTitle, buttonFindRevisionByTitleAndUser);
		
		JLabel label_9 = new JLabel("User:");
		label_9.setBounds(159, 28, 46, 14);
		panel_4.add(label_9);
		
		
		
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Find user node and name by revid", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setBounds(10, 92, 415, 81);
		nodeNumber.add(panel_5);
		
		JLabel lblRevid = new JLabel("Revid:");
		lblRevid.setBounds(10, 26, 78, 14);
		panel_5.add(lblRevid);
		
		textFindUserByRevid = new JTextField();
		textFindUserByRevid.setColumns(10);
		textFindUserByRevid.setBounds(108, 23, 86, 20);
		panel_5.add(textFindUserByRevid);
		
		JLabel lblUserNodeNumber = new JLabel("User node number:");
		lblUserNodeNumber.setBounds(10, 51, 110, 14);
		panel_5.add(lblUserNodeNumber);
		
		final JLabel labelFindUserByRevidNodeNumber = new JLabel("");
		labelFindUserByRevidNodeNumber.setBounds(118, 51, 86, 14);
		panel_5.add(labelFindUserByRevidNodeNumber);
		
		JLabel lblUserName = new JLabel("User name:");
		lblUserName.setBounds(198, 51, 66, 14);
		panel_5.add(lblUserName);
		
		final JLabel lblFindUserByRevidUserName = new JLabel("");
		lblFindUserByRevidUserName.setBounds(274, 51, 110, 14);
		panel_5.add(lblFindUserByRevidUserName);
		
		JButton buttonFindUserByRevid = new JButton("submit");
		buttonFindUserByRevid.setEnabled(false);
		buttonFindUserByRevid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String revid = textFindUserByRevid.getText();
				if(!revid.matches("^[1-9]\\d*$"))
				{					
				JOptionPane.showMessageDialog(null, "revid must be digital number");
				return;
				}
				ArrayList<String> userList = new ArrayList<String>();
				try {
					userList = ReadXML.findUserByRevid(revid);
				} catch (ClientHandlerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UniformInterfaceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (org.codehaus.jettison.json.JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String nodeNumber = new String();
				String userName = new String();
				if(!userList.isEmpty()){		
					nodeNumber = userList.get(0);
					userName = userList.get(1);
				}else{
					nodeNumber = "no revision";
					userName = "no revision";
				}
				labelFindUserByRevidNodeNumber.setText(nodeNumber);
				lblFindUserByRevidUserName.setText(userName);
			}
		});
		buttonFindUserByRevid.setBounds(222, 22, 89, 23);
		panel_5.add(buttonFindUserByRevid);
		
		listenOneText(textFindUserByRevid, buttonFindUserByRevid);
		
		JMenuBar menuBar = new JMenuBar();
		frmCreatingAProvenance.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("New menu");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mnNewMenu.add(mntmNewMenuItem);
	}
	
	private void initializeCannotConnectNeo4j(){
		frmCreatingAProvenance = new JFrame();
		frmCreatingAProvenance.setBounds(100, 100, 450, 300);
		frmCreatingAProvenance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCreatingAProvenance.getContentPane().setLayout(null);
		
		JLabel lblCheckConnect = new JLabel("Cannot connect to the Neo4j, please check the connect.....");
		lblCheckConnect.setFont(new Font("Traditional Arabic", Font.PLAIN, 17));
		lblCheckConnect.setForeground(SystemColor.windowBorder);
		lblCheckConnect.setBounds(10, 66, 393, 39);
		frmCreatingAProvenance.getContentPane().add(lblCheckConnect);
		
	}
	
	public static void listenOneText(final JTextField testField, final JButton jButton){
		Document doc1 = testField.getDocument();
		doc1.addDocumentListener(new DocumentListener() {
			String name;

			public void insertUpdate(DocumentEvent e) {
				name = testField.getText();
				if (!name.equals("") ) {
					jButton.setEnabled(true);
				} else {
					jButton.setEnabled(false);
				}
			}

			public void removeUpdate(DocumentEvent e) {
				name = testField.getText();				
				if (!name.equals("")) {
					jButton.setEnabled(true);
				} else {
					jButton.setEnabled(false);
				}
			}

			public void changedUpdate(DocumentEvent e) {
				name = testField.getText();
				if (!name.equals("")) {
					jButton.setEnabled(true);
				} else {
					jButton.setEnabled(false);
				}
			}
		});		
	}
	
	
	public static void listenTwoText(final JTextField testField1, final JTextField testField2, final JButton jButton){
		Document docTest1 = testField1.getDocument();
		docTest1.addDocumentListener(new DocumentListener() {
			//DocumentListener.changedUpdate(DocumentEvent)
			String test = new String();
			String test2 = new String();
			
			public void insertUpdate(DocumentEvent e) {
			test = testField2.getText();
			test2 = testField1.getText();
			if(!test.equals("") && !test2.equals("")) {
				jButton.setEnabled(true);
			}else{
				jButton.setEnabled(false);
			}
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				test = testField2.getText();
				test2 = testField1.getText();
				if(!test.equals("") && !test2.equals("")) {
					jButton.setEnabled(true);
				}else{
					jButton.setEnabled(false);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				test = testField2.getText();
				test2 = testField1.getText();
				if(!test.equals("") && !test2.equals("")) {
					jButton.setEnabled(true);
				}else{
					jButton.setEnabled(false);
				}
			}
		});
		
	}
	
	public static void listenFourText(final JTextField testField1, final JTextField testField2, final JTextField testField3, final JTextField testField4, final JButton jButton){
		Document docTest1 = testField1.getDocument();
		docTest1.addDocumentListener(new DocumentListener() {
			//DocumentListener.changedUpdate(DocumentEvent)
			String test = new String();
			String test2 = new String();
			String test3 = new String();
			String test4 = new String();
			
			@Override
			public void insertUpdate(DocumentEvent e) {
			test = testField1.getText();
			test2 = testField2.getText();
			test3 = testField3.getText();
			test4 = testField4.getText();
			if(!test.equals("") && !test2.equals("") && !test3.equals("") && !test4.equals("")) {
				jButton.setEnabled(true);
			}else{
				jButton.setEnabled(false);
			}
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				test = testField1.getText();
				test2 = testField2.getText();
				test3 = testField3.getText();
				test4 = testField4.getText();
				if(!test.equals("") && !test2.equals("") && !test3.equals("") && !test4.equals("")) {
					jButton.setEnabled(true);
				}else{
					jButton.setEnabled(false);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				test = testField1.getText();
				test2 = testField2.getText();
				test3 = testField3.getText();
				test4 = testField4.getText();
				if(!test.equals("") && !test2.equals("") && !test3.equals("") && !test4.equals("")) {
					jButton.setEnabled(true);
				}else{
					jButton.setEnabled(false);
				}
			}
		});
		
	}
	
	
	public static void goToUrl(JTable tableName){
		String cmmd ="rundll32 url.dll FileProtocolHandler "; 

		String title=(String) tableName.getValueAt(tableName.getSelectedRow(), 0);
		title = title.replaceAll("\\\\", "%5C");
		title = title.replaceAll("\"", "%22");
		title = title.replaceAll("=", "%3D");
		title = title.replaceAll("[+]", "%2B");
		title = title.replaceAll("\\^", "%5E");
		String url="http://en.wikipedia.org/wiki/"+title;
		Runtime rt=Runtime.getRuntime();
		try { 
		rt.exec(cmmd + url); 
		} catch (IOException e1) { 
		e1.printStackTrace(); 
		}
	}
	
}
