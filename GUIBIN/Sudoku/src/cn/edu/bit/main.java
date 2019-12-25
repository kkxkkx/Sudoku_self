/**  
* @Title: main.java
* @Package cn.edu.bit
* @Description: TODO
* @author WangKeXin 
* @date 2019��12��25�� ����8:40:18
* @version V1.0  
*/
package cn.edu.bit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
* @ClassName: main
* @Description: ����ҳ��
* @author WangKeXin
* @date 2019��12��25�� ����8:40:18
*
*/
public class main extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
	

	        primaryStage.setTitle("ImageFilter");
			FXMLLoader loader=new FXMLLoader(
					main.class.getResource("MainWindow.fxml"));
			
			AnchorPane root=(AnchorPane)loader.load();
			Scene scene=new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.show();
	
	}
	public static void main(String[] args) {
		launch(args);
	}

}