package net.garrettsites.picturebook.model;

import android.content.res.Resources;

import net.garrettsites.picturebook.R;

public class ErrorCodes {
    public static final String BUNDLE_ERROR_CODE_ARG = "ErrorCode";
    public static final String BUNDLE_EXCEPTION_ARG = "Exception";

    public enum Error {
        NO_LOGGED_IN_ACCOUNT,
        NO_ALBUMS,
        PROVIDER_EXCEPTION_THROWN
    }

    /**
     * Converts an Error enum to an integer.
     * @param errorEnum The enum to convert to an integer.
     * @return The integer representation of the given enum.
     */
    public static int toInt(Error errorEnum) {
        return errorEnum.ordinal();
    }

    /**
     * Converts an integer to the Error (enum) representation.
     * @param errorCode The integer to convert to an Error enum.
     * @return The Error enum represented by the integer.
     */
    public static Error toEnum(int errorCode) {
        return Error.values()[errorCode];
    }

    /**
     * Gets a localized string resource to show the user for the given error code.
     * @param errorCode The error code to generate a string for.
     * @return A localized string that's safe to show to the user.
     */
    public static int getLocalizedErrorStringResource(int errorCode) {
        return getLocalizedErrorStringResource(ErrorCodes.toEnum(errorCode));
    }

    /**
     * When given an errorCode and exception, format a message with the error code's error message
     * concatenated to the exception's message.
     * @param errorCode The error code to lookup a string for.
     * @param exception An exception, which may be null. If null, no exception message will be
     *                  appended.
     * @return A localized error message that can be displayed to the user.
     */
    public static String getLocalizedErrorStringResource(Resources r, int errorCode, Throwable exception) {
        String message = r.getString(getLocalizedErrorStringResource(errorCode));

        if (exception != null) {
            if (exception instanceof PictureBookException) {
                PictureBookException pbe = (PictureBookException) exception;
                message = message + "\n\n" + r.getString(getLocalizedErrorStringResource(pbe.getErrorCode()));
            } else {
                message = message + "\n\n" + exception.getMessage();
            }
        }

        return message;
    }

    /**
     * Gets a localized string resource to show the user for the given error code.
     * @param errorCode The error code to generate a string for.
     * @return A localized string that's safe to show to the user.
     */
    public static int getLocalizedErrorStringResource(Error errorCode) {
        switch (errorCode) {
            case NO_LOGGED_IN_ACCOUNT:
                return R.string.error_no_account;
            case NO_ALBUMS:
                return R.string.error_no_albums;
            case PROVIDER_EXCEPTION_THROWN:
                return R.string.error_provider_exception;
            default:
                return R.string.generic_error;
        }
    }
}
