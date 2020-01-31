import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.text.Text;

// Главный класс приложения JavaFX наследуется от javafx.application.Application
public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args); // Здесь вызывается метод start.
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Stage представляет пользовательский интерфейс,
        // является контейнером верхнего уровня для всех графических элементов.

        // установка надписи
        Text text = new Text("Hello from JavaFX!");
        text.setLayoutY(80);    // установка положения надписи по оси Y
        text.setLayoutX(100);   // установка положения надписи по оси X

        Group root = new Group(text); // Корневой элемент объекта Stage.
        Scene scene = new Scene(root, 300, 250, Color.BLUE);

        stage.setScene(scene); // Установливаем сцену для объекта Stage.
        stage.setTitle("First Application");
        stage.setWidth(300);
        stage.setHeight(250);
        stage.show(); // Отображаем окно на экране.
    }
}
