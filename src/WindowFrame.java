import javax.swing.*;

public class WindowFrame extends JFrame
{
    public WindowFrame()
    {
        this.setTitle("Conway's Game Of Life - Created by GitHub.Com/JustPrem");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.add(new WindowCanvas(5, 50,50));

        this.pack();
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(true);
    }
}