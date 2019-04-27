/**
 * Message Handle
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package com.dji.sdk.sample.skyspotter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.brother.ptouch.sdk.PrinterInfo;
import com.dji.sdk.sample.R;

public class MsgHandle extends Handler {

    final public static int FUNC_SETTING = 2;
    final public static int FUNC_TRANSFER = 3;
    private final static int FUNC_OTHER = 1;
    private final Context mContext;
    private final MsgDialog mDialog;
    private String mResult;
    private String mBattery;
    private boolean isCancelled = false;
    private int funcID = FUNC_OTHER;

    public MsgHandle(Context context, MsgDialog Dialog) {
        funcID = FUNC_OTHER;
        mContext = context;
        mDialog = Dialog;

    }

    /**
     * set the function id
     */
    public void setFunction(int funcID) {

        this.funcID = funcID;
    }

    /**
     * set the printing result
     */
    public void setResult(String results) {

        mResult = results;
    }

    /**
     * set the Battery info.
     */
    public void setBattery(String battery) {
        mBattery = battery;
    }

    /**
     * Message Handler which deal with the messages from UI thread or print
     * thread START: start message SDK_EVENT: message from SDK UPDATE: end
     * message
     */
    @Override
    public void handleMessage(Message msg) {

        switch (msg.what) {
            case Common.MSG_PRINT_START:
            case Common.MSG_DATA_SEND_START:
                mDialog.showStartMsgDialog(mContext
                        .getString(R.string.progress_dialog_message_communication_start));
                break;
            case Common.MSG_TRANSFER_START:
                mDialog.showStartMsgDialog(mContext
                        .getString(R.string.progress_dialog_message_transfer_start));
                break;
            case Common.MSG_GET_FIRM:
                mDialog.showPrintCompleteMsgDialog("Result = " + mResult);

                break;
            case Common.MSG_SDK_EVENT:
                String strMsg = msg.obj.toString();
                if (strMsg.equals(PrinterInfo.Msg.MESSAGE_START_COMMUNICATION
                        .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_preparing_connection));
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_START_CREATE_DATA
                        .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_createData));
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_START_SEND_DATA
                        .toString())) {
                    if (funcID != FUNC_OTHER) {
                        mDialog.setMessage(mContext
                                .getString(R.string.progress_dialog_message_sendingData));
                    } else {
                        mDialog.setMessage(mContext
                                .getString(R.string.progress_dialog_message_sendingPrintingData));
                    }
                    break;

                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_END_SEND_DATA
                        .toString())) {
                    if (funcID == FUNC_OTHER) {
                        mDialog.setMessage(mContext
                                .getString(R.string.progress_dialog_message_startPrint));
                    } else if (funcID == FUNC_TRANSFER) {
                        mDialog.setMessage(mContext
                                .getString(R.string.progress_dialog_message_dataSent));
                    }
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_END_SEND_TEMPLATE
                        .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_dataSent));
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_PRINT_COMPLETE
                        .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_printed));
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_PRINT_ERROR
                        .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.error_message_runtime));
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_END_COMMUNICATION
                        .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_close_connection));
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_PAPER_EMPTY
                        .toString())) {
                    if (!isCancelled)
                        mDialog.setMessage(mContext
                                .getString(R.string.progress_dialog_message_set_paper));
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_START_COOLING
                        .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_cooling_start));
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_END_COOLING
                        .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_cooling_end));
                    break;
                } else if (strMsg.equals(PrinterInfo.Msg.MESSAGE_WAIT_PEEL
                        .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_waiting_peel));
                    break;
                } else if (strMsg
                        .equals(PrinterInfo.Msg.MESSAGE_START_SEND_TEMPLATE
                                .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_sending_template));
                    break;
                } else if (strMsg
                        .equals(PrinterInfo.Msg.MESSAGE_START_UPDATE_BLUETOOTH_SETTING
                                .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_setting_bluetooth_setting));
                    break;
                } else if (strMsg
                        .equals(PrinterInfo.Msg.MESSAGE_START_GET_BLUETOOTH_SETTING
                                .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_getting_bluetooth_setting));
                    break;
                } else if (strMsg
                        .equals(PrinterInfo.Msg.MESSAGE_START_GET_TEMPLATE_LIST
                                .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_getting_template));
                    break;
                } else if (strMsg
                        .equals(PrinterInfo.Msg.MESSAGE_START_REMOVE_TEMPLATE_LIST
                                .toString())) {
                    mDialog.setMessage(mContext
                            .getString(R.string.progress_dialog_message_removing_template));
                    break;
                } else {
                    break;
                }
            case Common.MSG_PRINT_END:
            case Common.MSG_DATA_SEND_END:
                if (!mBattery.equals("")) {
                    mDialog.showPrintCompleteMsgDialog((mResult + "\nBattery: " + mBattery));
                } else {
                    mDialog.showPrintCompleteMsgDialog(mResult);
                }
                isCancelled = false;
                break;
            case Common.MSG_PRINT_CANCEL:
                mDialog.showStartMsgDialog(mContext
                        .getString(R.string.cancel_printer_msg));
                mDialog.disableCancel();
                isCancelled = true;
                break;
            case Common.MSG_WRONG_OS:
                mDialog.showPrintCompleteMsgDialog(mContext
                        .getString(R.string.error_message_weong_os));
                break;
            case Common.MSG_NO_USB:
                mDialog.showPrintCompleteMsgDialog(mContext
                        .getString(R.string.error_message_no_usb));
                break;
            default:
                break;
        }
    }
}
