package ar.edu.et7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionDisplay extends JFrame {
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private JTextArea promptArea;
    private JPanel checkboxPanel; 
    private JButton nextButton;
    private Timer timer;
    private JLabel timerLabel;
    private int timeRemaining = 30 * 60; // 30 minutos en segundos
    private int score = 0;

    public QuestionDisplay(List<Question> questions) {
        this.questions = questions;
        Collections.shuffle(this.questions); // Mezcla las preguntas

        // Configuración de la ventana
        setTitle("Multiple Choice Quiz");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Ajustamos el espaciado entre los componentes del BorderLayout

        // Inicialización de componentes
        promptArea = new JTextArea();
        promptArea.setEditable(false);
        promptArea.setLineWrap(true); 
        promptArea.setWrapStyleWord(true); // Aseguramos que las palabras no se corten en mitad de línea

        // Panel principal que contiene todos los componentes
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1, 5, 5)); 
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 

        // Añadir los componentes al panel
        mainPanel.add(new JScrollPane(promptArea)); // Agregar el JTextArea dentro de un JScrollPane

        // Crear un panel para los checkboxes
        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS)); 
        checkboxPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(checkboxPanel);

        // Botón para pasar a la siguiente pregunta
        nextButton = new JButton("Siguiente");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
                showNextQuestion();
            }
        });
        mainPanel.add(nextButton);

        // Inicializar y añadir el temporizador
        timerLabel = new JLabel();
        mainPanel.add(timerLabel);

        // Envolver el panel principal en un JScrollPane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        add(scrollPane, BorderLayout.CENTER); 

        // Iniciar el temporizador
        startTimer();

        // Mostrar la primera pregunta
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);

            // Unir título, estímulo y pregunta en un solo bloque de texto
            String fullText = "Title: " + q.getTitle() + "\n\n"
                            + "Stimulus: " + q.getStimulus() + "\n\n"
                            + q.getPrompt();
            promptArea.setText(fullText);

            // Limpiar el panel de checkboxes
            checkboxPanel.removeAll();

            // Crear los checkboxes dinámicamente según el número de opciones
            for (int i = 0; i < q.getChoices().size(); i++) {
                Question.Choice choice = q.getChoices().get(i);
                JCheckBox checkBox = new JCheckBox(choice.getContent());
                checkBox.setActionCommand(choice.getId());
                checkboxPanel.add(checkBox);
            }

            // Actualizar la interfaz
            revalidate();
            repaint();

            currentQuestionIndex++;
        } else {
            endQuiz();
        }
    }

    private void checkAnswer() {
        if (currentQuestionIndex > 0) {
            Question q = questions.get(currentQuestionIndex - 1);
            String selectedChoiceId = getSelectedChoiceId();
            if (selectedChoiceId != null && q.getAnswers().stream().anyMatch(answer -> answer.contains(selectedChoiceId))) {
                score += q.getPoints(); // Aumentar puntaje por respuesta correcta
            }
        }
    }

    private String getSelectedChoiceId() {
        for (Component comp : checkboxPanel.getComponents()) {
            JCheckBox checkBox = (JCheckBox) comp;
            if (checkBox.isSelected()) {
                return checkBox.getActionCommand();
            }
        }
        return null;
    }

    private void showNextQuestion() {
        showQuestion();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeRemaining--;
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                timerLabel.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));
                if (timeRemaining <= 0) {
                    timer.cancel();
                    endQuiz();
                }
            }
        }, 0, 1000);
    }

    private void endQuiz() {
        // Mostrar el puntaje
        JOptionPane.showMessageDialog(this, "Quiz terminado. Puntaje: " + score);
        System.exit(0);
    }
}
