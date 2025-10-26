package se.boffman.burp.toplevelnavigationhighlighter;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.core.Registration;

import burp.api.montoya.persistence.Preferences;

import burp.api.montoya.proxy.Proxy;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static burp.api.montoya.core.Annotations.annotations;

public class TopLevelNavigationHighlighter implements BurpExtension, ProxyRequestHandler {

    private static final String EXTENSION_NAME = "Top Level Navigation Highlighter";
    private static final String EXTENSION_SHORT_NAME = "TLNH";
    private static final String PREF_KEY_COLOR = "tlnh.highlight.color";

    private MontoyaApi api;
    private Preferences prefs;
    private HighlightColor selectedColor = HighlightColor.BLUE; // default
    @SuppressWarnings("unused")
    private Registration proxyReg; // keep a reference to avoid early GC/unregister

    // ===== BurpExtension entry point =====
    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        this.prefs = api.persistence().preferences();

        api.extension().setName(EXTENSION_NAME);

        // Load persisted color (if any)
        try {
            String saved = prefs.getString(PREF_KEY_COLOR);
            if (saved != null) {
                selectedColor = HighlightColor.valueOf(saved);
            }
        } catch (Exception ignored) { /* fall back to default */ }

        // Register Proxy handler (Proxy-only; not every tool)
        Proxy proxy = api.proxy();
        proxyReg = proxy.registerRequestHandler(this);

        // Add a small settings tab so the user can pick a color
        api.userInterface().registerSuiteTab(EXTENSION_SHORT_NAME, buildSettingsPanel());

        api.logging().logToOutput(EXTENSION_SHORT_NAME + " loaded. Current color: " + selectedColor);
    }

    // ===== ProxyRequestHandler methods =====
    // Required by interface, but we don't need to do anything at this stage
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        return ProxyRequestReceivedAction.continueWith(interceptedRequest);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        // Check for Sec-Fetch-Mode header == navigate OR nested-navigate
        String mode = interceptedRequest.headerValue("Sec-Fetch-Mode");
        if (mode != null) {
            String m = mode.trim().toLowerCase();
            if (m.equals("navigate") || m.equals("nested-navigate")) {
                // Add a highlight (keep any existing notes)
                Annotations a = annotations(selectedColor);
                return ProxyRequestToBeSentAction.continueWith(interceptedRequest, a);
            }
        }
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }

    // ===== Simple settings UI =====
    private Component buildSettingsPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.weightx = 0;
        gc.weighty = 0;

        JLabel label = new JLabel("Highlight color for navigation requests:");
        JComboBox<HighlightColor> colorBox = new JComboBox<>(HighlightColor.values());
        colorBox.setSelectedItem(selectedColor);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            HighlightColor choice = (HighlightColor) Objects.requireNonNull(colorBox.getSelectedItem());
            selectedColor = choice;
            prefs.setString(PREF_KEY_COLOR, choice.name());
            api.logging().logToOutput("[" + EXTENSION_SHORT_NAME + "] Saved color: " + choice);
            JOptionPane.showMessageDialog(panel, "Saved highlight color: " + choice, "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        gc.gridx = 0; gc.gridy = 0;
        panel.add(label, gc);
        gc.gridx = 1;
        panel.add(colorBox, gc);
        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 2;
        panel.add(saveBtn, gc);
        
        // Add filler to push content to top-left
        gc.gridx = 0; gc.gridy = 2;
        gc.weightx = 1.0; gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createGlue(), gc);

        outerPanel.add(panel, BorderLayout.NORTH);
        return outerPanel;
    }
}