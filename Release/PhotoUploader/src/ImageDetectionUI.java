import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

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

        licensePlateLabel = new JLabel("���ƺ�:");
        licensePlateTextField = new JTextField(10);
        selectImageButton = new JButton("ѡ��ͼƬ");
        recognizeButton = new JButton("ʶ��");
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
                        outputTextArea.append("ѡ����ļ�·��: " + filePath + "\n");

                        try {
                            FileWriter writer = new FileWriter("input.txt");
                            writer.write(filePath);
                            writer.close();
                            outputTextArea.append("�ļ�·����д�� input.txt\n");

                            // ���� imgpro.exe
                            startImgproExe();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } catch (Exception ex) {
                        outputTextArea.append("�޷�����ͼƬ: " + filePath + "\n");
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

                    // �������ʾ�ڳ��ƺ��ı�����
                    licensePlateTextField.setText(resultText.toString());
                    outputTextArea.append("ʶ�����Ѽ��ص����ƺ��ı���\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    outputTextArea.append("�޷���ȡʶ����\n");
                }
            }
        });
    }

   private void startImgproExe() {
    try {
        // ��ȡ imgpro.exe �ļ�·��
        String exeFilePath = "imgpro.exe";

        // ���� ProcessBuilder �������� imgpro.exe �ļ�·������������������У�
        ProcessBuilder processBuilder = new ProcessBuilder(exeFilePath);

        // �����ⲿ����
        Process process = processBuilder.start();

        // �ȴ�����ִ�����
        int exitCode = process.waitFor();

        // �������˳���
        if (exitCode == 0) {
            outputTextArea.append("imgpro.exe ����ִ�гɹ�\n");
        } else {
            outputTextArea.append("imgpro.exe ����ִ��ʧ�ܣ��˳��룺" + exitCode + "\n");
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
