import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ImageDetectionUI extends JFrame {

    private JLabel licensePlateLabel;
    private JTextField licensePlateTextField;
    private JButton selectImageButton;
    private JButton recognizeButton;
    private JLabel imageDisplayLabel;
    private ImageIcon selectedImage;
    private JTextArea outputTextArea;

    public ImageDetectionUI() {
        setTitle("Image Detection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        licensePlateLabel = new JLabel("车牌号:");
        licensePlateTextField = new JTextField(10);
        selectImageButton = new JButton("选择图片");
        recognizeButton = new JButton("识别");
        imageDisplayLabel = new JLabel();
        selectedImage = null;

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(licensePlateLabel);
        panel.add(licensePlateTextField);
        panel.add(selectImageButton);
        panel.add(recognizeButton);

        Container container = getContentPane();
        container.add(panel, BorderLayout.NORTH);
        container.add(new JScrollPane(imageDisplayLabel), BorderLayout.CENTER);
        container.add(new JScrollPane(outputTextArea), BorderLayout.SOUTH);

        selectImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String filePath = selectedFile.getAbsolutePath();

                    try {
                        selectedImage = new ImageIcon(filePath);
                        imageDisplayLabel.setIcon(selectedImage);
                        outputTextArea.append("选择的文件路径: " + filePath + "\n");

                        try {
                            FileWriter writer = new FileWriter("input.txt");
                            writer.write(filePath);
                            writer.close();
                            outputTextArea.append("文件路径已写入 input.txt\n");

                            // 启动 imgpro.exe
                            startImgproExe();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } catch (Exception ex) {
                        outputTextArea.append("无法加载图片: " + filePath + "\n");
                        selectedImage = null;
                    }
                }
            }
        });

        recognizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    FileReader reader = new FileReader("result.txt");
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line;
                    StringBuilder resultText = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        resultText.append(line).append("\n");
                    }
                    bufferedReader.close();
                    reader.close();

                    outputTextArea.setText(resultText.toString());
                    outputTextArea.append("识别结果已加载\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    outputTextArea.append("无法读取识别结果\n");
                }
            }
        });
    }

   private void startImgproExe() {
    try {
        // 获取 imgpro.exe 文件路径
        String exeFilePath = "imgpro.exe";

        // 创建 ProcessBuilder 对象，设置 imgpro.exe 文件路径和其他参数（如果有）
        ProcessBuilder processBuilder = new ProcessBuilder(exeFilePath);

        // 启动外部进程
        Process process = processBuilder.start();

        // 等待进程执行完毕
        int exitCode = process.waitFor();

        // 检查进程退出码
        if (exitCode == 0) {
            outputTextArea.append("imgpro.exe 进程执行成功\n");
        } else {
            outputTextArea.append("imgpro.exe 进程执行失败，退出码：" + exitCode + "\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ImageDetectionUI ui = new ImageDetectionUI();
                ui.setVisible(true);
            }
        });
    }
}
