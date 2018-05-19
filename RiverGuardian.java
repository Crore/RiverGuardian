import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
public class RiverGuardian extends JFrame{
    public static final int WIDTH =  600;
    public static final int HEIGHT = 600;
    Container c = getContentPane();
    static JTextArea names = new JTextArea(10, 50);
    static JList<String> userList;
    private static int direction = 1;
    final JPanel startPanel = new JPanel();
    private static final String TITLE = "River Guardian";
    public RiverGuardian() {
        super(TITLE);
        startPanel.setBackground(Color.RED);
        JButton start = new JButton("Start");
        JButton exit = new JButton("Exit");
        startPanel.add(start);
        startPanel.add(exit);
        this.add(startPanel);
        ImageIcon pic = new ImageIcon("Resources\\Enemy.jpg");
        Image img = pic.getImage();
        setVisible(true);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setIgnoreRepaint(true);
        start.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    c.removeAll();
                    JFrame frame = RiverGuardian.this;
                    c.add(GameCanvas.getGameCanvas(false));
                    GameCanvas.getGameCanvas(false).grabFocus();
                    frame.setContentPane(c);
                    frame.repaint();
                }
            });
        exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        setIconImage(img);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new RiverGuardian();
                }
            });
    }
    public static int getDirection() {
        return direction;
    }
    public static void setDirection(int direction) {
        RiverGuardian.direction = direction;
    }
}

