
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;



/**
 * @author Karten Lehn
 * @modified by Denis Niklas Risken
 *
 * Für das Programm habe ich den JoglStartCodePP auf der Lernplattform genutzt und bearbeitet
 *
 */
public class StartCodeMainWindowPP  {

    private static String TITLE = "Karteduell";
    private static final int CANVAS_WIDTH = 800;  // width of the drawable
    private static final int CANVAS_HEIGHT = 650; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second
    private static JButton button;
    private static JFrame frame;
    private static GLCapabilities capabilities;

    public StartCodeMainWindowPP() {

        /**
         * @modified by Denis Niklas Risken
         * Die GUI habe ich eigenständig erstellt
         */
        frame = new JFrame("Kartenduell");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(800,800);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);



        Font font = new Font("Georgia Pro Black", Font.PLAIN, 20);
        button = new JButton("Start Programm", new ImageIcon(""));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.green);
        button.setFont(font);

        JButton closeButton = new JButton("Close Programm");
        closeButton.setBackground(Color.DARK_GRAY);
        closeButton.setForeground(Color.green);
        closeButton.setFont(font);

        JLabel label = new JLabel("Kartenduell");
        label.setFont(new Font("Georgia Pro Black", Font.BOLD, 40));
        label.setOpaque(true);
        label.setBackground(Color.DARK_GRAY);
        label.setForeground(Color.green);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        /**
         * folgende OpenGL Eigenschaften habe ich übernommen
         * Die GUI Elemente habe ich eigenständig zum frame eingefügt, sowie die ActionListener
         */

        GLProfile profile = GLProfile.get(GLProfile.GL3);
        capabilities = new GLCapabilities(profile);

        GLCanvas canvas = new StartRendererPP(capabilities);
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

        frame.add(canvas, BorderLayout.CENTER);
        frame.add(button, BorderLayout.SOUTH);
        frame.add(label, BorderLayout.NORTH);
        frame.add(closeButton, BorderLayout.EAST);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }
                }.start();
            }
        });


        closeButton.addActionListener(e -> {
            new Thread() {
                @Override
                public void run() {
                    if (animator.isStarted()) animator.stop();
                    System.exit(0);
                }
            }.start();
        });

        frame.setTitle(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        animator.start();

        canvas.requestFocusInWindow();
        startProgramm();
    }

    /**
     * @modified by Denis Niklas Risken
     * @method: startProgramm() selber geschrieben
     */

    static public void startProgramm() {
        button.addActionListener(e ->
                new StartCodeMainWindowPP()


        );
    }

    /** @modified by Denis Niklas Risken
     *  @method: die Main Methode wurde von mir bearbeitet, um das Programm neu starten zu können
     */
    public static void main(String[] args) {
        new StartCodeMainWindowPP();
        startProgramm();
    }

}
