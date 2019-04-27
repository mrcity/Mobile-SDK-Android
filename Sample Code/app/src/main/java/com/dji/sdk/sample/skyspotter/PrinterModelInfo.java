/**
 * Information defined for printer models
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package com.dji.sdk.sample.skyspotter;

import java.util.Locale;

public class PrinterModelInfo {

    /**
     * Arrays of paper sizes.
     * Element 0 is default.
     * The PocketJet variant for Locale.US is the same as non-US
     * except the order changes to set a different default.
     */
    private static final String[] PS_A6_ONLY = {"A6"};
    private static final String[] PS_A7_ONLY = {"A7"};
    private static final String[] PS_CUSTOM_ONLY = {"CUSTOM"};
    private static final String[] PS_PJ = {"A4", "LETTER", "LEGAL", "A5", "A5_LANDSCAPE", "CUSTOM"};
    private static final String[] PS_PJ_US = {"LETTER", "LEGAL", "A4", "A5", "A5_LANDSCAPE", "CUSTOM"};
    private static final String[] PS_PT = {"W3_5", "W6", "W9", "W12", "W18", "W24", "HS_W6", "HS_W9", "HS_W12", "HS_W18", "HS_W24"};
    private static final String[] PS_QL = {"W17H54", "W17H87", "W23H23", "W29H42", "W29H90", "W38H90", "W39H48", "W52H29", "W54H29", "W62H29", "W62H100", "W60H86", "W12", "W29", "W38", "W50", "W54", "W62", "W62RB"};

    private static final String[] PS_QL1100 = {"W17H54", "W17H87", "W23H23", "W29H42", "W29H90", "W38H90", "W39H48", "W52H29", "W62H29", "W62H100", "W60H86","W102H51","W102H152", "W103H164", "W12", "W29", "W38", "W50", "W54", "W62", "W102", "W103"};
    private static final String[] PS_QL1115 = {"W17H54", "W17H87", "W23H23", "W29H42", "W29H90", "W38H90", "W39H48", "W52H29", "W62H29", "W62H100", "W60H86","W102H51","W102H152", "W103H164", "W12", "W29", "W38", "W50", "W54", "W62", "W102", "W103", "DT_W90", "DT_W102", "DT_W102H51", "DT_W102H152"};

    private static final String[] PS_PT_E8 = {"W3_5", "W6", "W9", "W12", "W18", "W24", "HS_W6", "HS_W9", "HS_W12", "HS_W18", "HS_W24", "W36", "R6_5", "R6_0", "R5_0", "R4_0", "R3_5", "R3_0", "R2_5", "FLE_W21H45"};
    private static final String[] PS_PT_P9 = {"W3_5", "W6", "W9", "W12", "W18", "W24", "HS_W6", "HS_W9", "HS_W12", "HS_W18", "HS_W24", "W36", "FLE_W21H45"};
    private static final String[] PS_PT3 = {"W3_5", "W6", "W9", "W12"};

    /**
     * Arrays of printer ports.
     * Element 0 is default.
     */
    private static final String[] PORTS_USB = {Common.USB};
    private static final String[] PORTS_BT_USB = {Common.BLUETOOTH, Common.USB};
    private static final String[] PORTS_NET_USB = {Common.NET, Common.USB};
    private static final String[] PORTS_NET_BT_USB = {Common.NET, Common.BLUETOOTH, Common.USB};
    private static final String[] PORTS_BT_USB_BLE = {Common.BLUETOOTH, Common.USB, Common.BLE};
    private static final String[] PORTS_NET_BT_USB_BLE = {Common.NET, Common.BLUETOOTH, Common.USB, Common.BLE};

    // Port order is different for TD4 printers. 
    private static final String[] PORTS_USB_BT = {Common.USB, Common.BLUETOOTH};
    private static final String[] PORTS_NET_USB_BT = {Common.NET, Common.USB, Common.BLUETOOTH};
    private static final String[] PORTS_BT = {Common.BLUETOOTH};
    /**
     * String array containing the names of all printer models.
     */
    private static String[] model = null;

    /**
     * Get an array of the names of all printer models.
     *
     * @return array of names of all printer models
     */
    public static String[] getModelNames() {
        if (model != null) {
            return model;
        }
        // Lazy initialization
        Model[] models = Model.values();
        int count = models.length;
        String[] m = new String[count];
        for (int i = 0; i < count; ++i) {
            m[i] = models[i].name();
        }
        model = m;
        return model;
    }

    /**
     * Get the port or paper size information for a specific model.
     *
     * @param model printer model name
     * @param value type of information, either Common.SETTINGS_PORT or Common.SETTINGS_PAPERSIZE
     * @return the requested information, if input parameters are valid, otherwise return null
     */
    public static String[] getPortOrPaperSizeInfo(String model, String value) {
        String name = model.trim();
        Model m;
        String[] result = null;

        try {
            m = Model.valueOf(name);
            if (value.equalsIgnoreCase(Common.SETTINGS_PORT)) {
                result = m.getPorts();
            } else if (value.equalsIgnoreCase(Common.SETTINGS_PAPERSIZE)) {
                result = m.getPaperSizes();
            }
        } catch (IllegalArgumentException ignored) {
        }
        return result;
    }


    /**
     * All printer models.
     * Each model holds a list of supported communication ports and paper sizes.
     */
    public enum Model {
        MW_140BT(PORTS_BT_USB, PS_A7_ONLY),
        MW_145BT(MW_140BT),
        MW_145MFi(MW_140BT),
        MW_260(PORTS_BT_USB, PS_A6_ONLY),
        MW_260MFi(MW_260),
        PJ_520(PORTS_USB, PS_PJ, PS_PJ_US),
        PJ_522(PJ_520),
        PJ_523(PJ_520),
        PJ_622(PJ_520),
        PJ_623(PJ_520),
        PJ_722(PJ_520),
        PJ_723(PJ_520),
        PJ_560(PORTS_BT_USB, PS_PJ, PS_PJ_US),
        PJ_562(PJ_560),
        PJ_563(PJ_560),
        PJ_662(PJ_560),
        PJ_663(PJ_560),
        PJ_762(PJ_560),
        PJ_763(PJ_560),
        PJ_763MFi(PJ_560),
        PJ_773(PORTS_NET_USB, PS_PJ, PS_PJ_US),
        RJ_4030(PORTS_BT_USB, PS_CUSTOM_ONLY),
        RJ_4030Ai(RJ_4030),
        RJ_4040(PORTS_NET_USB, PS_CUSTOM_ONLY),
        RJ_3050(PORTS_NET_BT_USB, PS_CUSTOM_ONLY),
        RJ_3150(RJ_3050),
        TD_2020(PORTS_USB, PS_CUSTOM_ONLY),
        TD_2120N(PORTS_NET_BT_USB, PS_CUSTOM_ONLY),
        TD_2130N(TD_2120N),
        TD_4000(PORTS_USB_BT, PS_CUSTOM_ONLY),
        TD_4100N(PORTS_NET_USB_BT, PS_CUSTOM_ONLY),
        QL_710W(PORTS_NET_USB, PS_QL),
        QL_720NW(QL_710W),
        QL_580N(QL_710W),
        PT_E550W(PORTS_NET_USB, PS_PT),
        PT_E500(PORTS_USB,PS_PT),
        PT_P750W(PT_E550W),
        PT_P710BT(PORTS_BT_USB, PS_PT),
        PT_D800W(PORTS_NET_USB, PS_PT_P9),
        PT_E800W(PT_D800W),
        PT_E850TKW(PORTS_NET_USB, PS_PT_E8),
        PT_P900W(PT_D800W),
        PT_P950NW(PT_D800W),
        PT_P300BT(PORTS_BT, PS_PT3),
        QL_800(PORTS_USB, PS_QL),
        QL_810W(QL_710W),
        QL_820NWB(PORTS_NET_USB_BT, PS_QL),
        RJ_2030(PORTS_BT_USB, PS_CUSTOM_ONLY),
        RJ_2050(RJ_3050),
        RJ_2140(PORTS_NET_USB, PS_CUSTOM_ONLY),
        RJ_2150(RJ_3050),
        RJ_3050Ai(RJ_3050),
        RJ_3150Ai(RJ_3050),
        QL_1100(PORTS_USB, PS_QL1100),
        QL_1110NWB(PORTS_NET_USB_BT, PS_QL1100),
        QL_1115NWB(PORTS_NET_USB_BT, PS_QL1115),
        RJ_4230B(PORTS_BT_USB_BLE, PS_CUSTOM_ONLY),
        RJ_4250WB(PORTS_NET_BT_USB_BLE, PS_CUSTOM_ONLY);


        private final String[] mPorts;
        private final String[] mPaperSizes;
        private final String[] mPaperSizesUS;

        Model(String[] ports, String[] paperSizes, String[] paperSizesUS) {
            mPorts = ports;
            mPaperSizes = paperSizes;
            mPaperSizesUS = paperSizesUS;
        }

        Model(String[] ports, String[] paperSizes) {
            this(ports, paperSizes, null);
        }

        Model(Model alias) {
            this(alias.mPorts, alias.mPaperSizes, alias.mPaperSizesUS);
        }

        public String[] getPorts() {
            return mPorts;
        }

        public String[] getPaperSizes() {
            return (Locale.getDefault().equals(Locale.US) && (mPaperSizesUS != null))
                    ? mPaperSizesUS : mPaperSizes;
        }

        public String getDefaultPaperSize() {
            String[] paperSizes = getPaperSizes();
            String paperSize = (paperSizes != null) ? paperSizes[0] : "";
            return (paperSize != null) ? paperSize : "";
        }
    } // enum Model
}	
