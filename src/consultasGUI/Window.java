package consultasGUI;
import javax.swing.*;

import java.sql.ResultSetMetaData;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
public class Window extends JFrame {
	//connection
    Connection conn;
    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/proyectoBD";
    String user = "root";
    String password = "yangyang9099";
    private Statement statement; 
    private ResultSet resultSet;  
	//GUI
	private JFrame frame;
	private JSplitPane splitpanel;
	private JSplitPane panel_top;
	private JScrollPane panel_buttom;
	private JPanel panel_top_left;
	private JPanel panel_top_right;
	private JComboBox<String>  select_table;
	private JRadioButton show_all;
	private JRadioButton show_some;
	private JComboBox<String> statistical_box;
	private JComboBox<String> id_enfermedad;
	private JButton button1;
	private JButton button2;
	private JCheckBox check;
	private JTextArea area_query;
	private JTable table;  
	String s1 = "Mostrar porcentaje de pacientes con distintas edades a las que afecte la enfermedad";
	String s2 = "Mostrar los genes que codifican la enfermedad";
	String s3 = "Mostrar el porcentaje de pacientes de cada género afectados por la enfermedad";
	String s4 = "Ver el número de ingresos por año de cada enfermedad";
	
	public Window() {
		try {  
            Class.forName(driver);  
            conn = DriverManager.getConnection(url, user, password);  
        }catch ( ClassNotFoundException cnfex ) { 
            System.err.println("fail install JDBC/ODBC driver");  
            cnfex.printStackTrace();   
            System.exit( 1 ); // terminate program   
        }catch ( SQLException sqlex ) { 
            System.err.println( "can not connect to db" );   
            sqlex.printStackTrace();   
            System.exit( 1 ); // terminate program   
        }   
		//global design
		splitpanel=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitpanel.setDividerSize(5);
		
		panel_top=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		panel_top.setDividerSize(2);
		panel_top.setDividerLocation(200);
		panel_top_left=new JPanel();
		panel_top_right=new JPanel();
		panel_top.setLeftComponent(panel_top_left);
		panel_top.setRightComponent(panel_top_right);
		panel_top.setBorder(BorderFactory.createLineBorder(Color.blue));
		splitpanel.setTopComponent(panel_top);
			//top_left components
			select_table=new JComboBox<String>();
			select_table.addItem("Enfermedades_raras");
			select_table.addItem("Gen");
			select_table.addItem("Paciente");
			select_table.addItem("Proteina");
			select_table.addItem("Tratamiento");	
			panel_top_left.add(select_table);
			ButtonGroup buttonGroup=new ButtonGroup();
			show_all=new JRadioButton("Show all");
			show_all.setSelected(true);
			show_some=new JRadioButton("Show first 10 lines");
			buttonGroup.add(show_all);
			buttonGroup.add(show_some);
			button1 = new JButton("Query");
			button1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String tab_name= select_table.getSelectedItem().toString();
					String query =null;
					if(show_all.isSelected()) {query = "select * from "+tab_name + ";";}
					else { query = "select * from "+tab_name + " limit 10 " +";";}
					
					try {
						statement = conn.createStatement();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}   
					try {
						resultSet = statement.executeQuery(query);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						displayResultSet( resultSet );
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}   
				}
			});
			panel_top_left.setBorder(BorderFactory.createTitledBorder("Table info"));
			panel_top_left.add(show_all);
			panel_top_left.add(show_some);
			panel_top_left.add(button1);
			//top_right components
			id_enfermedad = new JComboBox<String>();
			id_enfermedad.setEditable(true);
			id_enfermedad.addItem("enf_00003D7");
			id_enfermedad.addItem("enf_000029A");
			panel_top_right.add(id_enfermedad);
			statistical_box = new JComboBox<String>();
			statistical_box.addItem(s1);
			statistical_box.addItem(s2);
			statistical_box.addItem(s3);
			statistical_box.addItem(s4);
			panel_top_right.add(statistical_box);
			check = new JCheckBox("Diseña otra consulta en leguanje sql: ");
			area_query=new JTextArea("SELECT table1.column, table2.column\n" + 
					"\n" + 
					"FROM table1, table2\n" + 
					"\n" + 
					"WHERE table1.column1 = table2.column2;");
			panel_top_right.add(check);
			panel_top_right.add(area_query);
			button2 = new JButton("Query");
			panel_top_right.add(button2);
			panel_top_right.setBorder(BorderFactory.createTitledBorder("Statistical studies"));
			button2.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String query = null;
					if(check.isSelected()) {
						query = area_query.getText().toString();
					}
					else {
					 String enf_id = id_enfermedad.getSelectedItem().toString();
					 	if(statistical_box.getSelectedItem()==s1) {
					 		query = "select num.n/total.t*100,edad from (select count(*)as n, "
					 				+ "edad  from PACIENTE,PACIENTE_has_Enfermedades_Raras where Enfermedades_Raras_IdENFERM = " 
					 				+'"' +enf_id+'"' + " group by edad)num, (select count(*) as t from PACIENTE,PACIENTE_has_Enfermedades_Raras  "
					 						+ "where Enfermedades_Raras_IdENFERM= "+ '"'+enf_id+'"' +")total order by edad;";
					 	}
					 	if(statistical_box.getSelectedItem()==s2) {
					 		query = "select GEN_idGEN from Enfermedades_Raras where IdENFERM = "+'"'+enf_id+'"'+";";
					 	}
					 	if(statistical_box.getSelectedItem()==s3) {
					 		query = "select round(num.n/total.t*100,1),sexo from\n" + 
					 				"(select count(*)as n, sexo  from PACIENTE,PACIENTE_has_Enfermedades_Raras  where\n" + 
					 				"Enfermedades_Raras_IdENFERM= "+'"'+enf_id+'"'+ "group by sexo)num ,\n" + 
					 				"(select count(*) as t from PACIENTE,PACIENTE_has_Enfermedades_Raras  where\n" + 
					 				"Enfermedades_Raras_IdENFERM= "+'"'+enf_id+'"'+ ")total order by sexo";
					 	}
					 	if(statistical_box.getSelectedItem()==s4) {
					 		query = "SELECT count(*),DATE_FORMAT(FECHA_INGRESO, \"%Y\" ),\n" + 
					 				"Enfermedades_Raras_IdENFERM FROM PACIENTE left join\n" + 
					 				"PACIENTE_has_Enfermedades_Raras on dni=PACIENTE_DNI \n" + 
					 				"group by Enfermedades_Raras_IdENFERM,\n" + 
					 				"DATE_FORMAT(FECHA_INGRESO, \"%Y\" )";
					 	}
					}
					 	try {
							statement = conn.createStatement();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}   
						try {
							resultSet = statement.executeQuery(query);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							displayResultSet( resultSet );
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}   
					}
				
			});

		this.setContentPane(splitpanel);
		this.setVisible(true);
		this.setSize(800,700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	private void displayResultSet( ResultSet rs ) throws SQLException {   
        
        //notice when no record 
        if ( ! rs.next() ) {   
            JOptionPane.showMessageDialog( this, "no record" );   
            return;   
        }       
        Vector columnHeads = new Vector();   
        Vector rows = new Vector();     
        try {   
            //colnames
            ResultSetMetaData rsmd = rs.getMetaData();            
              
            for ( int i = 1; i <= rsmd.getColumnCount(); ++i )   
                columnHeads.addElement( rsmd.getColumnName( i ) );      
            //row.content  
            do {  
                rows.addElement( getNextRow( rs, rsmd ) );   
            } while ( rs.next() );     
            
            // show two vectors at table
            table = new JTable( rows, columnHeads );   
            panel_buttom = new JScrollPane( table ); 
			splitpanel.setBottomComponent(panel_buttom);
			splitpanel.setDividerLocation(220);
        }catch ( SQLException sqlex ) {   
            sqlex.printStackTrace();   
        }   
    }  
      
    @SuppressWarnings({ "rawtypes", "unchecked" })  
    private Vector getNextRow( ResultSet rs, ResultSetMetaData rsmd )throws SQLException{  
        Vector currentRow = new Vector();   
          
        for ( int i = 1; i <= rsmd.getColumnCount(); ++i )  
            currentRow.addElement( rs.getString( i ) );    
        return currentRow;   
    }   
  
    public void shutDown() {          
        try {  
            conn.close();   
        }catch ( SQLException sqlex ) {  
            System.err.println( "Unable to disconnect" );  
            sqlex.printStackTrace();  
        }  
    }   
  
}
