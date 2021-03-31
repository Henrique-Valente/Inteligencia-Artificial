import java.awt.GridLayout;
import javax.swing.JFrame;

public class Frame extends JFrame{
    Screen s;
    public Frame(MyPoint[] set, int xres, int yres, int scale){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(xres,yres);
        setResizable(false);
        setTitle("Graphics");
        init(set, xres, yres, scale);
    }
    
    public void init(MyPoint[] set, int xres, int yres, int scale){
        setLocationRelativeTo(null);
        
        setLayout(new GridLayout(1,1,0,0));
        
        s = new Screen(set, xres, yres, scale);
        add(s);

        setVisible(true);
    }
    
}
