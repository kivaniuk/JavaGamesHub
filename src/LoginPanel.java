import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel
{
    private Image backgroundImage;
    private Session session;
    private GameFrame frame;

    // lockout state shared between login and forgot password
    private int loginAttempts = 0;
    private int forgotAttempts = 0;
    private boolean isLockedOut = false;
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_SECONDS = 60;

    // stored as fields so lockOut() can disable them
    private JTextField usernameField;
    private JPasswordField pinField;
    private JButton loginButton;
    private JButton forgotButton;
    private JButton createAccountButton;
    private JLabel errorLabel;

    public LoginPanel(Session session, GameFrame frame)
    {
        backgroundImage = new ImageIcon("resources/Backgrounds/LoginBackground.png").getImage();
        this.session = session;
        this.frame = frame;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Welcome, Gamer!");
        welcomeLabel.setForeground(new Color(255, 200, 50));
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField("");
        usernameField.setMaximumSize(new Dimension(230, 35));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 13));

        JLabel pinLabel = new JLabel("PIN");
        pinLabel.setForeground(Color.WHITE);
        pinLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        pinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        pinField = new JPasswordField();
        pinField.setMaximumSize(new Dimension(230, 35));
        pinField.setAlignmentX(Component.CENTER_ALIGNMENT);
        pinField.setFont(new Font("Tahoma", Font.PLAIN, 13));

        loginButton = new JButton("LOGIN");
        loginButton.setMaximumSize(new Dimension(230, 38));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(30, 100, 200));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 15));
        loginButton.setBorder(BorderFactory.createRaisedBevelBorder());
        loginButton.setFocusPainted(false);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        forgotButton = new JButton("Forgot Password?");
        forgotButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotButton.setForeground(new Color(255, 200, 50));
        forgotButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
        forgotButton.setBorderPainted(false);
        forgotButton.setContentAreaFilled(false);
        forgotButton.setFocusPainted(false);
        forgotButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        createAccountButton = new JButton("First time here? Create an Account");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.setForeground(new Color(100, 200, 255));
        createAccountButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
        createAccountButton.setBorderPainted(false);
        createAccountButton.setContentAreaFilled(false);
        createAccountButton.setFocusPainted(false);
        createAccountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addActionListener(e ->
        {
            if (isLockedOut) return;

            String enteredUsername = usernameField.getText().trim();
            String enteredPin = new String(pinField.getPassword()).trim();

            if (session.login(enteredUsername, enteredPin))
            {
                loginAttempts = 0;
                if (session.isAdmin())
                {
                    frame.getAdminPanel().refresh(session);
                    frame.showScreen("ADMIN");
                }
                else
                {
                    frame.getMenuPanel().refresh(session);
                    frame.showScreen("MENU");
                }

            }
            else
            {
                loginAttempts++;
                int remaining = MAX_ATTEMPTS - loginAttempts;
                if (loginAttempts >= MAX_ATTEMPTS)
                {
                    lockOut();
                }
                else
                {
                    errorLabel.setText("Invalid username or PIN. " + remaining + " attempts left.");
                }
            }
        });

        forgotButton.addActionListener(e ->
        {
            if (isLockedOut) return;
            showForgotPasswordDialog();
        });

        createAccountButton.addActionListener(e -> showCreateAccountDialog());

        frame.getRootPane().setDefaultButton(loginButton);

        JPanel formBox = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor(new Color(0, 0, 0, 216));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setOpaque(false);
        formBox.setMaximumSize(new Dimension(300, 380));
        formBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        formBox.add(Box.createVerticalStrut(15));
        formBox.add(welcomeLabel);
        formBox.add(Box.createVerticalStrut(15));
        formBox.add(usernameLabel);
        formBox.add(Box.createVerticalStrut(5));
        formBox.add(usernameField);
        formBox.add(Box.createVerticalStrut(12));
        formBox.add(pinLabel);
        formBox.add(Box.createVerticalStrut(5));
        formBox.add(pinField);
        formBox.add(Box.createVerticalStrut(15));
        formBox.add(loginButton);
        formBox.add(Box.createVerticalStrut(8));
        formBox.add(errorLabel);
        formBox.add(Box.createVerticalStrut(8));
        formBox.add(forgotButton);
        formBox.add(Box.createVerticalStrut(5));
        formBox.add(createAccountButton);
        formBox.add(Box.createVerticalStrut(15));

        add(Box.createVerticalGlue());
        add(formBox);
        add(Box.createVerticalGlue());
    }

    private void lockOut()
    {
        isLockedOut = true;
        loginAttempts = 0;
        forgotAttempts = 0;

        // disable all inputs
        usernameField.setEnabled(false);
        pinField.setEnabled(false);
        loginButton.setEnabled(false);
        forgotButton.setEnabled(false);
        createAccountButton.setEnabled(false);

        errorLabel.setForeground(new Color(255, 100, 100));
        errorLabel.setText("Too many attempts! Wait " + LOCKOUT_SECONDS + "s...");

        // countdown timer - fires every second
        int[] secondsLeft = {LOCKOUT_SECONDS};
        Timer countdownTimer = new Timer(1000, null);
        countdownTimer.addActionListener(e ->
        {
            secondsLeft[0]--;
            if (secondsLeft[0] <= 0)
            {
                countdownTimer.stop();
                isLockedOut = false;
                usernameField.setEnabled(true);
                pinField.setEnabled(true);
                loginButton.setEnabled(true);
                forgotButton.setEnabled(true);
                createAccountButton.setEnabled(true);
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("You may try again.");
            }
            else
            {
                errorLabel.setText("Too many attempts! Wait " + secondsLeft[0] + "s...");
            }
        });
        countdownTimer.start();
    }

    public void showForgotPasswordDialog()
    {
        JDialog dialog = new JDialog();
        dialog.setTitle("Forgot Password");
        dialog.setSize(320, 220);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);
        dialog.setModal(true);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

        JLabel infoLabel = new JLabel("Enter your username:");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(220, 28));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton nextButton = new JButton("Next");
        nextButton.setBackground(new Color(30, 100, 200));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        nextButton.setBorder(BorderFactory.createRaisedBevelBorder());
        nextButton.setFocusPainted(false);
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(236, 233, 216));
        cancelButton.setForeground(new Color(0, 78, 152));
        cancelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelButton.setFocusPainted(false);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.add(Box.createHorizontalGlue());
        buttonRow.add(nextButton);
        buttonRow.add(Box.createHorizontalStrut(10));
        buttonRow.add(cancelButton);
        buttonRow.add(Box.createHorizontalGlue());

        nextButton.addActionListener(e ->
        {
            String username = usernameField.getText().trim();
            if (username.isEmpty())
            {
                statusLabel.setText("Please enter a username!");
                return;
            }

            String question = session.getSecurityQuestion(username);
            if (question == null)
            {
                statusLabel.setText("Username not found!");
                return;
            }

            // username found - switch dialog to security question step
            dialog.getContentPane().removeAll();
            dialog.setSize(320, 260);
            dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

            JLabel questionLabel = new JLabel("Security Question:");
            questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

            JLabel questionText = new JLabel(question);
            questionText.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionText.setFont(new Font("Tahoma", Font.ITALIC, 12));
            questionText.setForeground(new Color(0, 78, 152));

            JLabel answerLabel = new JLabel("Your Answer:");
            answerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JTextField answerField = new JTextField();
            answerField.setMaximumSize(new Dimension(220, 28));
            answerField.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel answerStatusLabel = new JLabel("");
            answerStatusLabel.setForeground(Color.RED);
            answerStatusLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
            answerStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton submitButton = new JButton("Submit");
            submitButton.setBackground(new Color(30, 100, 200));
            submitButton.setForeground(Color.WHITE);
            submitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
            submitButton.setBorder(BorderFactory.createRaisedBevelBorder());
            submitButton.setFocusPainted(false);
            submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton cancelButton2 = new JButton("Cancel");
            cancelButton2.setBackground(new Color(236, 233, 216));
            cancelButton2.setForeground(new Color(0, 78, 152));
            cancelButton2.setFont(new Font("Tahoma", Font.BOLD, 11));
            cancelButton2.setBorder(BorderFactory.createRaisedBevelBorder());
            cancelButton2.setFocusPainted(false);
            cancelButton2.setAlignmentX(Component.CENTER_ALIGNMENT);
            cancelButton2.addActionListener(ev -> dialog.dispose());

            submitButton.addActionListener(ev ->
            {
                String answer = answerField.getText().trim();

                if (session.verifySecurityAnswer(username, answer))
                {
                    // correct — log user in and go to menu
                    forgotAttempts = 0;
                    session.login(username, session.getPinForRecovery(username));
                    dialog.dispose();
                    frame.getMenuPanel().refresh(session);
                    frame.showScreen("MENU");
                }
                else
                {
                    forgotAttempts++;
                    int remaining = MAX_ATTEMPTS - forgotAttempts;
                    if (forgotAttempts >= MAX_ATTEMPTS)
                    {
                        dialog.dispose();
                        lockOut();
                    }
                    else
                    {
                        answerStatusLabel.setForeground(Color.RED);
                        answerStatusLabel.setText("Wrong answer! " + remaining + " attempts left.");
                    }
                }
            });

            JPanel answerButtonRow = new JPanel();
            answerButtonRow.setLayout(new BoxLayout(answerButtonRow, BoxLayout.X_AXIS));
            answerButtonRow.add(Box.createHorizontalGlue());
            answerButtonRow.add(submitButton);
            answerButtonRow.add(Box.createHorizontalStrut(10));
            answerButtonRow.add(cancelButton2);
            answerButtonRow.add(Box.createHorizontalGlue());

            dialog.add(Box.createVerticalStrut(15));
            dialog.add(questionLabel);
            dialog.add(Box.createVerticalStrut(8));
            dialog.add(questionText);
            dialog.add(Box.createVerticalStrut(12));
            dialog.add(answerLabel);
            dialog.add(Box.createVerticalStrut(5));
            dialog.add(answerField);
            dialog.add(Box.createVerticalStrut(10));
            dialog.add(answerStatusLabel);
            dialog.add(Box.createVerticalStrut(10));
            dialog.add(answerButtonRow);

            dialog.revalidate();
            dialog.repaint();
        });

        dialog.add(Box.createVerticalStrut(15));
        dialog.add(infoLabel);
        dialog.add(Box.createVerticalStrut(10));
        dialog.add(usernameField);
        dialog.add(Box.createVerticalStrut(10));
        dialog.add(statusLabel);
        dialog.add(Box.createVerticalStrut(10));
        dialog.add(buttonRow);

        dialog.setVisible(true);
    }

    public void showCreateAccountDialog()
    {
        JDialog dialog = new JDialog();
        dialog.setTitle("Create Account");
        dialog.setSize(320, 380);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);
        dialog.setModal(true);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(220, 25));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel pinLabel = new JLabel("PIN (4 digits):");
        pinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField pinField = new JPasswordField();
        pinField.setMaximumSize(new Dimension(220, 25));
        pinField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel confirmPinLabel = new JLabel("Confirm PIN:");
        confirmPinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField confirmPinField = new JPasswordField();
        confirmPinField.setMaximumSize(new Dimension(220, 25));
        confirmPinField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel securityQuestionLabel = new JLabel("Security Question:");
        securityQuestionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        securityQuestionLabel.setFont(new Font("Tahoma", Font.BOLD, 10));

        JTextField securityQuestionField = new JTextField();
        securityQuestionField.setMaximumSize(new Dimension(220, 25));
        securityQuestionField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel securityAnswerLabel = new JLabel("Answer:");
        securityAnswerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField securityAnswerField = new JTextField();
        securityAnswerField.setMaximumSize(new Dimension(220, 25));
        securityAnswerField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton createButton = new JButton("Create");
        createButton.setBackground(new Color(236, 233, 216));
        createButton.setForeground(new Color(0, 78, 152));
        createButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        createButton.setBorder(BorderFactory.createRaisedBevelBorder());
        createButton.setFocusPainted(false);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(236, 233, 216));
        cancelButton.setForeground(new Color(0, 78, 152));
        cancelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelButton.setFocusPainted(false);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        createButton.addActionListener(e ->
        {
            String username = usernameField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();
            String confirmPin = new String(confirmPinField.getPassword()).trim();
            String question = securityQuestionField.getText().trim();
            String answer = securityAnswerField.getText().trim();

            if (username.isEmpty() || pin.isEmpty() || question.isEmpty() || answer.isEmpty())
            {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("All fields are required!");
                return;
            }
            if (!pin.equals(confirmPin))
            {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("PINs don't match!");
                return;
            }
            if (pin.length() != 4)
            {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("PIN must be exactly 4 digits!");
                return;
            }

            boolean success = session.register(username, pin, question, answer);
            if (success)
            {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Account created! You can now log in.");
                Timer timer = new Timer(1500, event -> dialog.dispose());
                timer.setRepeats(false);
                timer.start();
            }
            else
            {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Username already taken!");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.add(Box.createHorizontalGlue());
        buttonRow.add(createButton);
        buttonRow.add(Box.createHorizontalStrut(10));
        buttonRow.add(cancelButton);
        buttonRow.add(Box.createHorizontalGlue());

        dialog.add(Box.createVerticalStrut(15));
        dialog.add(usernameLabel);
        dialog.add(Box.createVerticalStrut(5));
        dialog.add(usernameField);
        dialog.add(Box.createVerticalStrut(10));
        dialog.add(pinLabel);
        dialog.add(Box.createVerticalStrut(5));
        dialog.add(pinField);
        dialog.add(Box.createVerticalStrut(10));
        dialog.add(confirmPinLabel);
        dialog.add(Box.createVerticalStrut(5));
        dialog.add(confirmPinField);
        dialog.add(Box.createVerticalStrut(10));
        dialog.add(securityQuestionLabel);
        dialog.add(Box.createVerticalStrut(5));
        dialog.add(securityQuestionField);
        dialog.add(Box.createVerticalStrut(10));
        dialog.add(securityAnswerLabel);
        dialog.add(Box.createVerticalStrut(5));
        dialog.add(securityAnswerField);
        dialog.add(Box.createVerticalStrut(10));
        dialog.add(statusLabel);
        dialog.add(Box.createVerticalStrut(10));
        dialog.add(buttonRow);

        dialog.setVisible(true);
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}