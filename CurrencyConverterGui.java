import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class CurrencyConverterGUI extends JFrame {

    // Example rates (should be updated with current rates)
    static final double USD_TO_EUR = 0.92;
    static final double USD_TO_GBP = 0.75;
    static final double USD_TO_PKR = 277.86;
    static final double USD_TO_AUD = 1.51;
    static final double EUR_TO_USD = 1.08;
    static final double EUR_TO_GBP = 0.85;
    static final double EUR_TO_PKR = 301.44;
    static final double EUR_TO_AUD = 1.64;
    static final double GBP_TO_USD = 1.27;
    static final double GBP_TO_EUR = 1.17;
    static final double GBP_TO_PKR = 354.05;
    static final double GBP_TO_AUD = 1.92;
    static final double PKR_TO_USD = 0.0036;
    static final double PKR_TO_EUR = 0.0033;
    static final double PKR_TO_GBP = 0.0028;
    static final double PKR_TO_AUD = 0.0054;
    static final double AUD_TO_USD = 0.66;
    static final double AUD_TO_EUR = 0.61;
    static final double AUD_TO_GBP = 0.52;
    static final double AUD_TO_PKR = 184.14;

    static LinkedList<ConversionRecord> historicData = new LinkedList<>();

    private JTextField amountField;
    private JComboBox<String> sourceCurrencyBox;
    private JComboBox<String> targetCurrencyBox;
    private JLabel resultLabel;
    private String username;
    private String phone;
    private String email;

    public CurrencyConverterGUI(String username, String phone, String email) {
        this.username = username;
        this.phone = phone;
        this.email = email;

        setTitle("Currency Converter");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveHistoricData();
            }
        });

        add(new JLabel("Enter amount:"));
        amountField = new JTextField();
        add(amountField);

        add(new JLabel("Source currency:"));
        sourceCurrencyBox = new JComboBox<>(new String[] { "USD", "EUR", "GBP", "PKR", "AUD" });
        add(sourceCurrencyBox);

        add(new JLabel("Target currency:"));
        targetCurrencyBox = new JComboBox<>(new String[] { "USD", "EUR", "GBP", "PKR", "AUD" });
        add(targetCurrencyBox);

        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ConvertButtonListener());
        add(convertButton);

        resultLabel = new JLabel("Converted Amount");
        add(resultLabel);

        JButton historyButton = new JButton("Show History");
        historyButton.addActionListener(new HistoryButtonListener());
        add(historyButton);

        loadHistoricData();
    }

    private class ConvertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String amountText = amountField.getText();
            if (isValidAmount(amountText)) {
                double amount = Double.parseDouble(amountText);
                String sourceCurrency = (String) sourceCurrencyBox.getSelectedItem();
                String targetCurrency = (String) targetCurrencyBox.getSelectedItem();
                double convertedAmount = performConversion(amount, sourceCurrency, targetCurrency);
                if (convertedAmount != -1) {
                    resultLabel.setText(String.format("%.2f %s is equal to %.2f %s", amount, sourceCurrency,
                            convertedAmount, targetCurrency));
                    addToHistory(amount, sourceCurrency, targetCurrency, convertedAmount);
                } else {
                    resultLabel.setText("Conversion error.");
                }
            } else {
                resultLabel.setText("Invalid amount.");
            }
        }
    }

    private class HistoryButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder history = new StringBuilder("<html>Historic Data:<br>");
            for (ConversionRecord record : historicData) {
                history.append(record).append("<br>");
            }
            history.append("</html>");
            JOptionPane.showMessageDialog(null, history.toString(), "Conversion History",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static boolean isValidAmount(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double performConversion(double amount, String sourceCurrency, String targetCurrency) {
        return switch (sourceCurrency) {
            case "USD" -> switch (targetCurrency) {
                case "EUR" -> amount * USD_TO_EUR;
                case "GBP" -> amount * USD_TO_GBP;
                case "PKR" -> amount * USD_TO_PKR;
                case "AUD" -> amount * USD_TO_AUD;
                default -> -1;
            };
            case "EUR" -> switch (targetCurrency) {
                case "USD" -> amount * EUR_TO_USD;
                case "GBP" -> amount * EUR_TO_GBP;
                case "PKR" -> amount * EUR_TO_PKR;
                case "AUD" -> amount * EUR_TO_AUD;
                default -> -1;
            };
            case "GBP" -> switch (targetCurrency) {
                case "USD" -> amount * GBP_TO_USD;
                case "EUR" -> amount * GBP_TO_EUR;
                case "PKR" -> amount * GBP_TO_PKR;
                case "AUD" -> amount * GBP_TO_AUD;
                default -> -1;
            };
            case "PKR" -> switch (targetCurrency) {
                case "USD" -> amount * PKR_TO_USD;
                case "EUR" -> amount * PKR_TO_EUR;
                case "GBP" -> amount * PKR_TO_GBP;
                case "AUD" -> amount * PKR_TO_AUD;
                default -> -1;
            };
            case "AUD" -> switch (targetCurrency) {
                case "USD" -> amount * AUD_TO_USD;
                case "EUR" -> amount * AUD_TO_EUR;
                case "GBP" -> amount * AUD_TO_GBP;
                case "PKR" -> amount * AUD_TO_PKR;
                default -> -1;
            };
            default -> -1;
        };
    }

    private void addToHistory(double amount, String sourceCurrency, String targetCurrency, double convertedAmount) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = formatter.format(new Date());
        historicData.addFirst(new ConversionRecord(amount, sourceCurrency, targetCurrency, convertedAmount, dateTime));
        if (historicData.size() > 90) {
            historicData.removeLast();
        }
    }

    private void saveHistoricData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("historicData.dat"))) {
            out.writeObject(historicData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHistoricData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("historicData.dat"))) {
            historicData = (LinkedList<ConversionRecord>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // If there's an error, we just start with an empty list
            historicData = new LinkedList<>();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserInformationGUI();
        });
    }
}

class ConversionRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private final double amount;
    private final String sourceCurrency;
    private final String targetCurrency;
    private final double convertedAmount;
    private final String dateTime;

    public ConversionRecord(double amount, String sourceCurrency, String targetCurrency, double convertedAmount,
            String dateTime) {
        this.amount = amount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.convertedAmount = convertedAmount;
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return String.format("%.2f %s to %s: %.2f %s on %s", amount, sourceCurrency, targetCurrency, convertedAmount,
                targetCurrency, dateTime);
    }
}

class UserInformationGUI extends JFrame {
    private JTextField usernameField;
    private JTextField phoneField;
    private JTextField emailField;

    public UserInformationGUI() {
        setTitle("User Information");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitButtonListener());
        add(submitButton);

        setVisible(true);
    }

    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            if (!username.isEmpty() && !phone.isEmpty() && !email.isEmpty()) {
                dispose(); // Close the user info window
                SwingUtilities.invokeLater(() -> {
                    CurrencyConverterGUI converterGUI = new CurrencyConverterGUI(username, phone, email);
                    converterGUI.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
