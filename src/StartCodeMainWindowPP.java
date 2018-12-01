
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class StartCodeMainWindowPP extends JFrame {

    private static final long serialVersionUID = 1L;

    private static String TITLE = "Start Code Main Window - Programmable Pipeline";
    private static final int CANVAS_WIDTH = 800;  // width of the drawable
    private static final int CANVAS_HEIGHT = 650; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second

    public StartCodeMainWindowPP() {

        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities capabilities = new GLCapabilities(profile);

        GLCanvas canvas = new StartRendererPP(capabilities);
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

        this.getContentPane().add(canvas);
        this.addWindowListener(new WindowAdapter() {
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
        this.setTitle(TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        animator.start();

        canvas.requestFocusInWindow();
    }

    public static void main(String[] args) {
        new StartCodeMainWindowPP();
    }

}
