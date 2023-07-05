import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColorPickerUI extends JFrame {

    private JButton openButton;
    private JLabel colorLabel;
    private JLabel imageLabel;

    private BufferedImage loadedImage;
    private Color selectedColor;

    public ColorPickerUI() {
        setTitle("颜色提取器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        openButton = new JButton("打开图片");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(ColorPickerUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        BufferedImage image = ImageIO.read(selectedFile);
                        loadedImage = image;
                        updateImageLabel(image);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        add(buttonPanel, BorderLayout.NORTH);

        colorLabel = new JLabel("");
        colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(colorLabel, BorderLayout.SOUTH);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (loadedImage != null) {
                    int x = e.getX();
                    int y = e.getY();
                    if (x >= 0 && x < loadedImage.getWidth() && y >= 0 && y < loadedImage.getHeight()) {
                        Color pixelColor = new Color(loadedImage.getRGB(x, y));
                        selectedColor = pixelColor;
                        String colorHex = getColorHex(pixelColor);
                        colorLabel.setText("所选像素点的颜色色号：" + colorHex);
                    }
                }
            }
        });
        add(imageLabel, BorderLayout.CENTER);

        setSize(800, 500);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private String getColorHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private void updateImageLabel(BufferedImage image) {
        ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(600, 400, Image.SCALE_SMOOTH));
        imageLabel.setIcon(imageIcon);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ColorPickerUI colorPickerUI = new ColorPickerUI();
                colorPickerUI.setVisible(true);
            }
        });
    }
}