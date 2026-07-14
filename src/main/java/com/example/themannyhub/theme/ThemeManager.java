package com.example.themannyhub.theme;

import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;

import java.util.Objects;

/**
 * Centralised theming helper.
 *
 * <p>The application uses AtlantaFX's Cupertino Light stylesheet for a modern
 * macOS-inspired look. Every {@link Scene} created anywhere in the app should
 * be passed through {@link #apply(Scene)} so the same stylesheet is loaded
 * consistently on every window (login, dashboard, dialogs, etc.).</p>
 */
public final class ThemeManager {

    /** AtlantaFX theme used by the entire application. */
    public static final Theme THEME = new CupertinoLight();

    private ThemeManager() {
        // utility class
    }

    /**
     * Apply the application theme to the given scene.
     *
     * <p>Replaces any stylesheets currently attached to the scene's root with
     * the Cupertino Light user-agent stylesheet. Safe to call multiple times;
     * only one copy of the stylesheet is ever added.</p>
     */
    public static void apply(Scene scene) {
        Objects.requireNonNull(scene, "scene");

        var root = scene.getRoot();
        if (root == null) {
            // Scene root may not be set yet (rare in practice). Defer.
            return;
        }

        var stylesheets = root.getStylesheets();
        // Remove any previously applied AtlantaFX stylesheets to avoid duplicates.
        stylesheets.removeIf(uri ->
                uri != null && uri.endsWith(THEME.getUserAgentStylesheet()));

        stylesheets.add(THEME.getUserAgentStylesheet());
    }

    /**
     * Convenience for Application subclasses: sets the user-agent stylesheet on
     * the Application's {@link javafx.scene.Parent#getUserAgentStylesheet()}
     * which propagates to every Scene created afterwards.
     */
    public static void applyToApplication() {
        Application.setUserAgentStylesheet(THEME.getUserAgentStylesheet());
    }
}
