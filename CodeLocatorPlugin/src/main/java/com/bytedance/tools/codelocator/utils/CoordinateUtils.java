package com.bytedance.tools.codelocator.utils;

import com.bytedance.tools.codelocator.model.WApplication;

public class CoordinateUtils {

    public static final int TABLE_RIGHT_MARGIN = 5;

    public static final int DEFAULT_BORDER = 10;

    public static final int SCALE_TO_HEIGHT = 700;

    public static final int SCALE_TO_LAND_PANEL_HEIGHT = 540;

    public static final int SCALE_TO_LAND_HEIGHT = 300;

    public static final int PANEL_WIDTH = 420;

    public static final int MOUSE_CLICK_OFFSET = 2; // 光标点击位置和实际点击Y有视觉偏移量, 减掉

    public static int TREE_PANEL_HEIGHT = 370;

    public static int convertPanelXToPhoneX(WApplication application, int panelX, float scale, int transX) {
        if (application == null) {
            return 0;
        }
        int x = panelX;
        if (x < 0 || x > application.getOverrideScreenWidth()) {
            return -1;
        }
        return (int) (application.getPanelToPhoneRatio() * (x / scale - transX));
    }

    public static int convertPhoneXToPanelX(WApplication application, int phoneX) {
        if (application == null) {
            return 0;
        }
        return (int) (phoneX / application.getPanelToPhoneRatio());
    }

    public static int convertPanelYToPhoneY(WApplication application, int panelY, float scale, int transY) {
        if (application == null) {
            return 0;
        }
        int y = panelY - MOUSE_CLICK_OFFSET;
        if (y < 0 || y > application.getPanelHeight()) {
            return -1;
        }
        return (int) (application.getPanelToPhoneRatio() * (y / scale - transY));
    }

    public static int convertPhoneYToPanelY(WApplication application, int phoneY) {
        if (application == null) {
            return 0;
        }
        return (int) (phoneY / application.getPanelToPhoneRatio());
    }

    public static int convertPanelDistanceToPhoneDistance(WApplication application, int panelDistance) {
        return (int) (panelDistance * application.getPanelToPhoneRatio());
    }

    public static int convertPhoneDistanceToPanelDistance(WApplication application, int phoneDistance) {
        if (application == null) {
            return 0;
        }
        return (int) (phoneDistance / application.getPanelToPhoneRatio());
    }
}
