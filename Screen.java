import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Screen extends JPanel{
    MyPoint[] set;
    int xres, yres, scale;
    public Screen(MyPoint[] set, int xres, int yres, int scale){
        this.set = set;
        this.xres = xres;
        this.yres = yres;
        this.scale = scale;
        repaint();
    }

    public void paint(Graphics g) {
        int i;
        int xoffset = xres/2, yoffset = yres/2;
        for(i=0;i<set.length-1;i++){
            // System.out.println( ((set[i].x*scale)+offset) + " " + ((set[i].y*scale)+offset) );
            g.drawLine((set[i].x*scale)+xoffset, (set[i].y*scale)+yoffset, (set[i+1].x*scale)+xoffset, (set[i+1].y*scale)+yoffset);
        }
        g.drawLine((set[0].x*scale)+xoffset, (set[0].y*scale)+yoffset, (set[i].x*scale)+xoffset, (set[i].y*scale)+yoffset);
    }
}